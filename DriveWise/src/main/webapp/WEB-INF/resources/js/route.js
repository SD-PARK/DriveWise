gsap.registerPlugin(ScrollTrigger);

let startLocation, endLocation;
let startMarker = null;
let endMarker = null;

// 경로 안내 박스 ON/OFF
function pullingRoute() {
    const route = document.getElementById('route');
    if (!route) return;
    const pullingBtn = route.querySelector('.pulling-btn');

    if (route.classList.contains('open')) {
        route.classList.remove('open');
        pullingBtn.innerHTML = '〈';
        pullingBtn.style.textAlign = 'left';
    } else {
        route.classList.add('open');
        pullingBtn.innerHTML = '〉';
        pullingBtn.style.textAlign = 'right';
    }
}

// 도로 기본 스타일
const roadStyle = {
    strokeColor: 'blue',
    strokeWeight: 3,
}

// 도로 정보 내용
function InfoWindowInnerHTML(event) {
    if (!event) return;
    
    let result = '';
    const map = new Map();
    map.set('도로명', event.feature.getProperty('ROAD_NAME'));
    map.set('길이', event.feature.getProperty('LENGTH').toFixed(2)+'km');
    map.set('차로 수', event.feature.getProperty('LANES'));
    map.set('제한속도', event.feature.getProperty('MAX_SPD')+'km/h');

    for (let [key, value] of map) {
        result += key + ': ' + value + '<br>';
    }
    
    return result;
}

// 지도 및 도로 출력
function initMap() {
    const jejuBounds = {
        north: 33.6,
        south: 33.175,
        west: 126.11,
        east: 127
    };
    const jejuCenter = { lat: 33.38, lng: 126.54 };
    const mapOptions = {
        center: jejuCenter,
        zoom: 12,
        mapId: 'c0c88b6e1748f7c8',
        restriction: {
            latLngBounds: jejuBounds,
            strictBounds: true
        },
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false,
    };
    const map = new google.maps.Map(document.getElementById('map'), mapOptions);

    map.data.loadGeoJson('resources/jeju_link.geojson');
    map.data.setStyle((feature) => {
        const linkId = feature.getProperty('link_id');
        const upItsId = feature.getProperty('up_its_id');
        const dwItsId = feature.getProperty('dw_its_id');
        const empId = feature.getProperty('emp_id');
        // if (upItsId === 4050024005) {
        //     return roadStyle;
        // } else {
        //     return { visible: false };
        // }
        
        return roadStyle;
    });

    infoWindowEvents(map);
    
    addPlaceSearch(map, document.getElementById('start-location'), 'start');
    addPlaceSearch(map, document.getElementById('end-location'), 'end');
}


// 도로 정보 출력
function infoWindowEvents(map) {
    const customInfoWindow = document.getElementById('customInfoWindow');
    if (!customInfoWindow) return;
    
    const overlay = drawOverlay(customInfoWindow);
    overlay.setMap(map);

    const updateInfoWindowPosition = (event) => {
        const projection = overlay.getProjection();
        const position = projection.fromLatLngToDivPixel(event.latLng);
        gsap.set(customInfoWindow, { left: `${position.x}px`, top: `${position.y}px` });
    };

    map.data.addListener('mouseover', (event) => {
        customInfoWindow.innerHTML = InfoWindowInnerHTML(event);
        gsap.to(customInfoWindow, { display: 'block', opacity: 1, duration: 0.2 });

        map.data.overrideStyle(event.feature, { strokeColor: 'blue', strokeWeight: 5 });

        updateInfoWindowPosition(event);
    });

    map.data.addListener('mousemove', (event) => {
        updateInfoWindowPosition(event);
    });

    map.data.addListener('mouseout', (event) => {
        map.data.overrideStyle(event.feature, roadStyle);
        gsap.to(customInfoWindow, { display: 'none', opacity: 0, duration: 0.2 });
    });
}

function drawOverlay(customInfoWindow) {
    const overlay = new google.maps.OverlayView();

    overlay.onAdd = function() {
        const layer = this.getPanes().overlayLayer;
        layer.appendChild(customInfoWindow);
    };
    overlay.draw = function() {};
    overlay.onRemove = function() {
        if (customInfoWindow.parentElement)
            customInfoWindow.parentElement.removeChild(customInfoWindow);
    };

    return overlay;
}

// 장소 검색 기능 추가
function addPlaceSearch(map, input, type='start') {
    if (!input) return;

    const searchBox = new google.maps.places.SearchBox(input);

    map.addListener('bounds_changed', () => {
        searchBox.setBounds(map.getBounds());
    });

    searchBox.addListener('places_changed', () => {
        const places = searchBox.getPlaces();
        if (places.length == 0) return;

        // 검색 결과 위치로 지도 이동
        places.forEach((place) => {
            if (!place.geometry || !place.geometry.location) {
                console.log("Returned place contains no geometry");
                return;
            }
            map.panTo(place.geometry.location);

            let markerSettings = {
                position: place.geometry.location,
                map: map,
                title: place.name,
                gmpClickable: true,
            };

            // 위치 정보 저장
            if (type === 'start') {
                if (startMarker) clearMarkers('start');
                const pinBackground = new google.maps.marker.PinElement({
                    background: "#4E9525",
                    borderColor: "#2E5A1C",
                    glyphColor: "#2E5A1C",
                });
                markerSettings = { ...markerSettings, content: pinBackground.element };
                startMarker = new google.maps.marker.AdvancedMarkerElement(markerSettings);
                startLocation = place;
            } else if (type === 'end') {
                if (endMarker) clearMarkers('end');
                const pinBackground = new google.maps.marker.PinElement({
                    background: "#F23A3A",
                    borderColor: "#C9182B",
                    glyphColor: "#C9182B",
                });
                markerSettings = { ...markerSettings, content: pinBackground.element };
                endMarker = new google.maps.marker.AdvancedMarkerElement(markerSettings);
                endLocation = place;
            }
        });
    });
}

function clearMarkers(type='both') {
    if ((type === 'start' || type === 'both') && startMarker) {
        startMarker.setMap(null);
        startMarker = null;
    }
    if ((type === 'end' || type === 'both') && endMarker) {
        endMarker.setMap(null);
        endMarker = null;
    }
}

// 길 안내
function routing() {
    if (!startLocation || !endLocation) {
        console.log("location not selected.");
        return;
    }

    console.log('Start Location:', {
        name: startLocation.name,
        address: startLocation.formatted_address,
        coordinates: {
            lat: startLocation.geometry.location.lat(),
            lng: startLocation.geometry.location.lng()
        }
    });

    console.log('End Location:', {
        name: endLocation.name,
        address: endLocation.formatted_address,
        coordinates: {
            lat: endLocation.geometry.location.lat(),
            lng: endLocation.geometry.location.lng()
        }
    });
}

// 장소 초기화
function resetLocation() {
    clearMarkers(); // 마커 지우기
    startLocation = null;
    endLocation = null;
    $('#start-location').val('');
    $('#end-location').val('');
}