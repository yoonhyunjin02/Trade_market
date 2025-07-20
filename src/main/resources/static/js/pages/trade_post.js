let map

async function initMap() {
    const { Map } = await google.maps.importLibrary("maps");
    const { Geocoder } = await google.maps.importLibrary("geocoding");

    geocoder = new Geocoder();

    map = new Map(document.getElementById("map"), {
        center: { lat: 0, lng: 0},
        zoom: 13,
    });

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


const script = document.createElement("script");
script.src = `https://maps.googleapis.com/maps/api/js?key=${googleMapsApiKey}&v=weekly&callback=initMap`;
script.async = true;
script.defer = true;
document.head.appendChild(script);
