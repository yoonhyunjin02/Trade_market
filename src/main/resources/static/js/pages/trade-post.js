let map

(function loadGoogleMap() {
    const apiKey = document.querySelector('meta[name="gmaps-api-key"]').content;
    const script = document.createElement("script");
    script.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(apiKey)}&v=weekly&callback=initMap`;
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);
})();
window.initMap = async function () {
    const { Map } = await google.maps.importLibrary("maps");
    const { Geocoder } = await google.maps.importLibrary("geocoding");

    geocoder = new Geocoder();

    map = new Map(document.getElementById("map"), {
        center: { lat: 0, lng: 0},
        zoom: 13,
    });
    const productLocation = getProductLocation();
    if (productLocation) {
        geocoder.geocode({address:productLocation}, (result,status) => {
            if(status === "OK" && result[0]) {
                const tradeAddress = result[0].geometry.location;
                map.setCenter(tradeAddress);

                new google.maps.Marker({
                    map: map,
                    position: tradeAddress,
                    title: "거래 희망 장소",
                });
            } else {
                console.error("Geocode 실패:", status);
            }
        });
    }

}
function getProductLocation() {
  const meta = document.querySelector('meta[name="product-location"]');
  return meta && meta.content ? meta.content.trim() : null;
}



