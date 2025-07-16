let autocomplete, map, geocoder, tradeSetAddress
// Google Maps API는 전역 객체 google.maps에 로딩됨. google.maps 바로 사용하면 됨.
function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 37.5665, lng: 126.9780 },
        zoom: 13,
      });


    geocoder = new google.maps.Geocoder();
    const input = document.getElementById("location");
      autocomplete = new google.maps.places.Autocomplete(input, { // 자동완성 주소목록을 띄우는 부분
        componentRestrictions: { country: "kr" },
        fields: ["formatted_address", "geometry"],

      });
      autocomplete.addListener("place_changed", onPlaceSelected); // (이벤트, 선택된 주소) 자동완성에서 주소를 선택하면 이벤트 발생

}
// 해당 장소 상세정보 가져오기
function onPlaceSelected() {
    const place = autocomplete.getPlace(); // 해당 장소 상세정보를 가져오는 기능
    if (!place.geometry) {
        alert("장소를 찾을 수 없습니다.")
        return;
    }

    tradeSetAddress = place.formatted_address;

    map.setCenter(place.geometry.location);

    if(window._selMarker) window._selMarker.setMap(null);
    window._selMarker = new google.maps.Marker({
        position: place.geometry.location,
        map: map,
        title: "당근할 장소",
    })
}
