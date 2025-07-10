  // window.initMap 으로 전역에 함수 등록
  function initMap() {
    // 1) 지도의 중심 좌표를 정의
    const center = { lat: 37.5665, lng: 126.9780 }; // 위도, 경도

    // 2) 구글 맵 객체 생성
    const map = new google.maps.Map(
      document.getElementById("map"), // 지도를 보여줄 DOM 요소 (id="map")
      {
        zoom: 10,      // 지도 확대 레벨 (1~21 사이)
        center: center // 초기 중심 좌표
      }
    );

    // 3) 마커(marker) 추가
    new google.maps.Marker({
      position: center, // 마커를 찍을 좌표(위에서 정의한 center)
      map,              // 표시할 맵 객체
      title: "서울인듯 서울아닌 서울 같은 곳"     // 마우스를 올렸을 때 보일 툴팁 텍스트
    });
  }

