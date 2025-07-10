let autocomplete, map, geocoder, userSetAddress, currentAddress;

function initMap() {
  // 1) 지도 초기화
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 37.5665, lng: 126.9780 },
    zoom: 13,
  });
  geocoder = new google.maps.Geocoder();

  // 2) 주소 자동완성
  const input = document.getElementById("address");
  autocomplete = new google.maps.places.Autocomplete(input, {
    componentRestrictions: { country: "kr" },
    fields: ["formatted_address", "geometry"],
  });
  autocomplete.addListener("place_changed", onPlaceSelected);

  // 3) 폼 제출 막고, 자동완성으로 주소 설정
  document.getElementById("set-form")
    .addEventListener("submit", e => {
      e.preventDefault();
      if (userSetAddress) {
        // 서버에 설정만 할 경우: 원한다면 여기서 fetch/post
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
  const place = autocomplete.getPlace();
  if (!place.geometry) return;
  userSetAddress = place.formatted_address;

  // 지도를 검색 위치로 이동
  map.setCenter(place.geometry.location);
  if (window._selMarker) window._selMarker.setMap(null);
  window._selMarker = new google.maps.Marker({
    position: place.geometry.location,
    map: map,
    title: "내 동네",
  });
}

// 현재 위치 조회 → 역지오코딩 → 비교
function compareWithCurrentLocation() {
  if (!navigator.geolocation) {
    showMessage("브라우저에서 위치 서비스를 지원하지 않습니다.", false);
    return;
  }

  navigator.geolocation.getCurrentPosition(
    pos => {
      const coords = { lat: pos.coords.latitude, lng: pos.coords.longitude };
      geocoder.geocode({ location: coords }, (results, status) => {
        if (status === "OK" && results[0]) {
          currentAddress = results[0].formatted_address;
          // 현재 위치 마커
          map.setCenter(coords);
          new google.maps.Marker({
            position: coords,
            map: map,
            title: "현재 위치",
          });
          // 비교
          doCompare();
        } else {
          showMessage("현재 위치를 가져오지 못했습니다.", false);
        }
      });
    },
    () => showMessage("위치 정보를 허용해 주세요.", false)
  );
}

// userSetAddress 와 currentAddress 비교
function doCompare() {
  const msgEl = document.getElementById("compare-msg");
  const btn = document.getElementById("confirm-btn");
  const hidden = document.getElementById("confirm-address");

  if (!userSetAddress) {
    showMessage("먼저 내 동네를 검색해서 선택해 주세요.", false);
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
    hidden.value = userSetAddress;
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
