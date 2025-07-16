let autocomplete, map, geocoder, userSetAddress, currentAddress
// Google Maps API는 전역 객체 google.maps에 로딩됨. google.maps 바로 사용하면 됨.
function initMap() {

    geocoder = new google.maps.Geocoder();
    const input = document.getElementById("location");
      autocomplete = new google.maps.places.Autocomplete(input, {
        componentRestrictions: { country: "kr" }, // 국가 대한민국
        fields: ["formatted_address", "geometry"], // 주소 문자열, 좌표정보
        // 예) "formatted_address": "서울특별시 종로구 세종로 1-68",
        // 예) "geometry": { location: Lat(위도)Lng(경도), viewport: LatLngBounds(직사각형 범위 = 출력화면) }
      });
      autocomplete.addListener("place_changed", onPlaceSelected); // (이벤트, 선택된 주소) 자동완성에서 주소를 선택하면 이벤트 발생

      document.getElementById("set-form")
          .addEventListener("submit", e => {
            e.preventDefault();
            if (userSetAddress) {
              // 서버에 설정만 할 경우: 원한다면 여기서 fetch/post
              // 선택된 주소를 현재위치와 비교할 수 있게 해야함
              alert(`내 동네 설정: ${userSetAddress}`);
            } else {
              alert("주소를 선택해 주세요.");
            }
          });
}

function onPlaceSelected() {
  // 선택된 장소 정보
  const place = autocomplete.getPlace();
  // 위치정보 없으면 종료
  if (!place.geometry) return;
  // 내 동네설정을 저장
  userSetAddress = place.formatted_address;
}
