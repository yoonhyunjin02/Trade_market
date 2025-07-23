const uploadArea = document.getElementById('imageUploadArea');
const fileInput = document.getElementById('hiddenFileInput');
const previewImage = document.getElementById('previewImage');
const placeholder = document.getElementById('uploadPlaceholder');
const fileNameDisplay = document.getElementById('fileNameDisplay');

function initImageUpload() {
    uploadArea.addEventListener('click', function() {
        fileInput.click();
    });

    fileInput.addEventListener('change', function(e) {
        const file = e.target.files[0];

        if (file) {
            fileNameDisplay.textContent = file.name;

            const reader = new FileReader();
            reader.onload = function(e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
                placeholder.style.display = 'none';
                uploadArea.classList.add('has-image');
            };
            reader.readAsDataURL(file);
        } else {
            previewImage.style.display = 'none';
            placeholder.style.display = 'flex';
            uploadArea.classList.remove('has-image');
            fileNameDisplay.textContent = '파일 선택';
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    initImageUpload();
});

(function loadGoogleMap() {
    const apiKey = document.querySelector('meta[name="gmaps-api-key"]').content;
    const script = document.createElement("script");
    script.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(apiKey)}&v=weekly&callback=initMap`;
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);
})();

let autocomplete, map, geocoder
// Google Maps API는 전역 객체 google.maps에 로딩됨. google.maps 바로 사용하면 됨(Legacy Script Loading 방식)
// 필요한 라이브러리를 개별적으로 불러와서 사용함(Module Loading 방식)
window.initMap = async function () {

    const { Map } = await google.maps.importLibrary("maps"); // 지도 라이브러리
    const { Autocomplete } = await google.maps.importLibrary("places"); // 자동완성 라이브러리
    const {Geocoder} = await google.maps.importLibrary("geocoding");

    map = new Map(document.getElementById("map"), {
        center: { lat: 37.5665, lng: 126.9780 },
        zoom: 13,
      });

    const input = document.getElementById("location");
    autocomplete = new Autocomplete(input, { // 자동완성 주소목록을 띄우는 부분
      componentRestrictions: { country: "kr" },
      fields: ["formatted_address", "geometry"],
    });
      autocomplete.addListener("place_changed", onPlaceSelected); // (이벤트, 선택된 주소) 자동완성에서 주소를 선택하면 이벤트 발생

    //지도 클릭
    map.addListener("click", (e) => {
        const latLng = e.latLng;
        const geocoder = new Geocoder();

        geocoder.geocode({ location:latLng },(result, status) => {
            if (status === "OK" && result[0]) {
                const address = result[0].formatted_address;
                input.value = address;

                if(window._selMarker) window._selMarker.setMap(null);
                window._selMarker = new google.maps.Marker({
                        position: latLng,
                        map: map,
                        title: "당근할 장소",
                });
            }
        });
    });
}
// 해당 장소 상세정보 가져오기
function onPlaceSelected() {
    const place = autocomplete.getPlace(); // 해당 장소 상세정보를 가져오는 기능
    if (!place.geometry) {
        alert("장소를 찾을 수 없습니다.")
        return;
    }

    map.setCenter(place.geometry.location);

    if(window._selMarker) window._selMarker.setMap(null);
    window._selMarker = new google.maps.Marker({
        position: place.geometry.location,
        map: map,
        title: "당근할 장소",
    });
}





