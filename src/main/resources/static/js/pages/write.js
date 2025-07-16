let autocomplete, map, geocoder, userSetAddress, currentAddress
// Google Maps API는 전역 객체 google.maps에 로딩됨. google.maps 바로 사용하면 됨.
function initMap() {

    geocoder = new google.maps.Geocoder();
    const input = document.getElementById("location");
      autocomplete = new google.maps.places.Autocomplete(input, { // 자동완성 주소목록을 띄우는 부분
        componentRestrictions: { country: "kr" },
        fields: ["formatted_address", "geometry"],

      });
      autocomplete.addListener("place_changed", onPlaceSelected); // (이벤트, 선택된 주소) 자동완성에서 주소를 선택하면 이벤트 발생

}
function onPlaceSelected() {
    const place = autocomplete.getPlace(); // 해당 장소 상세정보를 가져오는 기능
    if (!place.geometry) {
        alert("장소를 찾을 수 없습니다.")
        return;
    }

}
