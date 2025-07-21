let autocomplete, map, geocoder, userSetAddress, currentAddress; // 지역변수

async function initMap() {

    const { Map } = await google.maps.importLibrary("maps"); // 지도 라이브러리
    const { Autocomplete } = await google.maps.importLibrary("places"); // 자동완성 라이브러리
    const { Geocoder } = await google.maps.importLibrary("geocoding");
    // 1) 지도 초기화
    map = new Map(document.getElementById("map"), { // Google Maps API는 html에서 키를 받아서 전역 객체 google.maps로 사용
        center: { lat: 37.5665, lng: 126.9780 },
        zoom: 13,
    });
    geocoder = new Geocoder();

    // 2) 주소 자동완성
    const input = document.getElementById("address");
    autocomplete = new Autocomplete(input, {
        componentRestrictions: { country: "kr" }, // 국가 대한민국
        fields: ["formatted_address", "geometry"], // 주소 문자열, 좌표정보
        // 예) "formatted_address": "서울특별시 종로구 세종로 1-68",
        // 예) "geometry": { location: Lat(위도)Lng(경도), viewport: LatLngBounds(직사각형 범위 = 출력화면) }
    });
    autocomplete.addListener("place_changed", onPlaceSelected); // (이벤트, 선택된 주소) 자동완성에서 주소를 선택하면 이벤트 발생

    // 3) 폼 제출 막고, 자동완성으로 주소 설정
    document.getElementById("set-form")
        .addEventListener("submit", e => {
            e.preventDefault();
            if (userSetAddress) {
                // 서버에 설정만 할 경우: 원한다면 여기서 fetch/post
                // 선택된 주소를 현재위치와 비교할 수 있게 해야함
                doCompare();
                alert(`내 동네 설정: ${userSetAddress}`);
            } else {
                alert("주소를 선택해 주세요.");
            }
        });

    // 4) 현재 위치 조회 & 비교 시작
    compareWithCurrentLocation();
}

// 자동완성으로 주소 선택 시
function onPlaceSelected() {
    // 선택된 장소 정보
    const place = autocomplete.getPlace();
    // 위치정보 없으면 종료
    if (!place.geometry) return;
    // 내 동네설정을 저장
    userSetAddress = place.formatted_address;

    // 지도를 검색 위치로 이동
    map.setCenter(place.geometry.location);

    // 기존 마커 제거
    if (window._selMarker) window._selMarker.setMap(null);
    // 새 마커를 선택된 장소에 찍음
    window._selMarker = new google.maps.Marker({
        position: place.geometry.location,
        map: map,
        title: "내 동네",
    });
}

// 현재 위치 조회 → 역 지오코딩(좌표를 주소로 변환) → 비교
function compareWithCurrentLocation() {
    if (!navigator.geolocation) { // 정보없으면 함수종료
        showMessage("브라우저에서 위치 서비스를 지원하지 않습니다.", false);
        return;
    }

    navigator.geolocation.getCurrentPosition(
        pos => {
            const coords = {
                lat: pos.coords.latitude, // 위도
                lng: pos.coords.longitude // 경도
            };
            //역 지오코딩
            geocoder.geocode(
                { location: coords }, // 요청
                (results, status) => { // 콜백함수
                    // results: 서버가 반환한 결과배열
                    // status: 요청 처리 상태를 나타내는 문자열 코드
                    if (status === "OK" && results[0]) {
                        currentAddress = results[0].formatted_address; // 사람이 읽을 수 있는 주소 문자열임
                        // 현재 위치 마커
                        map.setCenter(coords);
                        new google.maps.Marker({
                            position: coords,
                            map: map,
                            title: "현재 위치",
                        });
                        // 비교
                        doCompare();
                        // 이전에 자동완성해둔 userSetAddress와 currentAddress를 비교
                    } else {
                        // 역 지오코딩 실패
                        showMessage("현재 위치를 가져오지 못했습니다." + status, false);
                    }
                });
        }, // pos => {}
        // 위치 조회 자체를 거부하거나 실패한 경우
        () => showMessage("위치 정보를 허용해 주세요.", false)
    );
}

// userSetAddress 와 currentAddress 비교
function doCompare() {
    const msgEl = document.getElementById("compare-msg"); // 메세지 표시
    const btn = document.getElementById("confirm-btn"); // 버튼
    const hidden = document.getElementById("confirm-address"); // 폼 전송용 숨겨진 필드

    if (!userSetAddress) {
        showMessage("먼저 내 동네를 검색해서 선택해 주세요." + userSetAddress, false);
        return;
    }

    // 단순 포함 비교 (더 정밀히 하고 싶으면 커스터마이즈)
    if (
        currentAddress.includes(userSetAddress) ||
        userSetAddress.includes(currentAddress)
    ) {
        showMessage(
            `현재 위치는 ${userSetAddress} 입니다. 현재 위치가 내 동네 설정과 같습니다.`,
            true
        );
        // 폼 전송용 필드에 인증할 주소를 채움
        hidden.value = userSetAddress;
        // 동네 인증 버튼 활성화
        btn.disabled = false;
    } else {
        showMessage(
            `현재 위치는 ${currentAddress} 입니다. 내 동네 설정(${userSetAddress})과 일치하지 않습니다.`,
            false
        );
    }
}

// 비교 결과 메시지 보여주기
function showMessage(text, ok) {
    const msgEl = document.getElementById("compare-msg");
    msgEl.textContent = text;
    msgEl.style.display = "block";
    msgEl.style.color = ok ? "green" : "red";
}

// 동적 bootstrap 로더 삽입
const script = document.createElement("script");
script.src = `https://maps.googleapis.com/maps/api/js?key=${googleMapsApiKey}&v=weekly&callback=initMap`;
script.async = true;
script.defer = true;
document.head.appendChild(script);