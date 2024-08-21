let map;
let markers = [];
let markerCluster;
let markersAmount;

// 마커 좌표 리스트
let latlng_list =  [
                {lat: 37.51932559999999, lng: 126.8371642},
                {lat: 37.484085, lng: 126.782803},
                {lat: 37.484085, lng: 126.882803},
                {lat: 36.51932559999999, lng: 127.8371642},
                {lat: 36.484085, lng: 127.782803},
                {lat: 36.484085, lng: 127.882803}
            ]

// 구글맵 초기화 
function initMap() {
    const myLatLng = {
        lat: 14.627527,
        lng: 160.364443
    }

    map = new google.maps.Map(
        document.getElementById("map"), {
            center: myLatLng,
            scrollwhell: false,
            zoom: 1.9
        }
    );
    refreshMap();
}

// 마커 생성함수
function createMarkers() {
    // console.log('latlng_list')
    // console.log(latlng_list)
    
    for (let i = 0; i < latlng_list.length; i++) {

        let mker = new google.maps.Marker({
            position: latlng_list[i],
            map,
            animation: google.maps.Animation.DROP,
        })

        markers.push(mker);
    }
    // console.log('markers')
    // console.log(markers);
    // Add a marker clusterer to manage the markers.
    // 클러스터링
    markerCluster = new MarkerClusterer(map, markers,{
        imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m',
        gridSize: 100
    });
}

function refreshMap() {
    if (markerCluster instanceof MarkerClusterer) {
        // Clear all clusters and markers
        markerCluster.clearMarkers()
    }
    markers = [];
    createMarkers();
}
