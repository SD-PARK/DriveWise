gsap.registerPlugin(ScrollTrigger);

let map;
let startLocation, endLocation;
let startMarker = null;
let endMarker = null;

// 경로 안내 박스 ON/OFF
function pullingRoute() {
  const route = document.getElementById("route");
  if (!route) return;
  const pullingBtn = route.querySelector(".pulling-btn");

  if (route.classList.contains("open")) {
    route.classList.remove("open");
    pullingBtn.innerHTML = "〈";
    pullingBtn.style.textAlign = "left";
  } else {
    route.classList.add("open");
    pullingBtn.innerHTML = "〉";
    pullingBtn.style.textAlign = "right";
  }
}

// 도로 기본 스타일
const roadStyle = {
  strokeColor: "#007BFF",
  strokeWeight: 7,
  // strokePattern: [
  //   { icon: { path: google.maps.SymbolPath.CIRCLE() }, repeat: "10px" },
  // ],
};
const overRoadStyle = {
  strokeColor: "blue",
};

// 도로 정보 내용
function InfoWindowInnerHTML(event) {
  if (!event) return;

  let result = "";
  const map = new Map();

  const roadName = event.feature.getProperty("roadName");
  const length = event.feature.getProperty("length");
  const time = event.feature.getProperty("time");
  const maxSpeed = event.feature.getProperty("maxSpeed");

  if (roadName != null) map.set("도로명", roadName);
  if (length != null) map.set("길이", length.toFixed(2) + "m");
  if (time != null) map.set("예상 통행시간", (time / 60.0).toFixed(1) + "분");
  if (maxSpeed != null) map.set("제한속도", maxSpeed + "km/h");

  for (let [key, value] of map) {
    result += key + ": " + value + "<br>";
  }

  return result;
}

// 지도 및 도로 출력
function initMap() {
  const jejuBounds = {
    north: 33.6,
    south: 33.175,
    west: 126.11,
    east: 127,
  };
  const jejuCenter = { lat: 33.38, lng: 126.54 };
  const mapOptions = {
    center: jejuCenter,
    zoom: 12,
    mapId: "c0c88b6e1748f7c8",
    restriction: {
      latLngBounds: jejuBounds,
      strictBounds: true,
    },
    mapTypeControl: false,
    streetViewControl: false,
    fullscreenControl: false,
  };
  map = new google.maps.Map(document.getElementById("map"), mapOptions);

  // printRoadMap(map, geojson);

  addPlaceSearch(document.getElementById("start-location"), "start");
  addPlaceSearch(document.getElementById("end-location"), "end");
}

// 도로 그리기
function printRoadMap(geoJson) {
  if (!map) return;

  clearRoadMap();
  map.data.addGeoJson(geoJson);
  map.data.setStyle((feature) => {
    // 혼잡지수, 경로평가지수에 따른 Style 지정할 예정

    // const linkId = feature.getProperty('link_id');
    // if (linkId === 4050024005) {
    //     return roadStyle;
    // } else {
    //     return { visible: false };
    // }

    return roadStyle;
  });

  infoWindowEvents();
}

// 도로 지우기
function clearRoadMap() {
  if (!map) return;

  map.data.forEach((feature) => map.data.remove(feature));
}

// 도로 정보 출력
function infoWindowEvents() {
  if (!map) return;

  const customInfoWindow = document.getElementById("customInfoWindow");
  if (!customInfoWindow) return;

  const overlay = drawOverlay(customInfoWindow);
  overlay.setMap(map);

  const updateInfoWindowPosition = (event) => {
    const projection = overlay.getProjection();
    const position = projection.fromLatLngToDivPixel(event.latLng);
    gsap.set(customInfoWindow, {
      left: `${position.x}px`,
      top: `${position.y}px`,
    });
  };

  map.data.addListener("mouseover", (event) => {
    customInfoWindow.innerHTML = InfoWindowInnerHTML(event);
    gsap.to(customInfoWindow, { display: "block", opacity: 1, duration: 0.2 });

    map.data.overrideStyle(event.feature, overRoadStyle);

    updateInfoWindowPosition(event);
  });

  map.data.addListener("mousemove", (event) => {
    updateInfoWindowPosition(event);
  });

  map.data.addListener("mouseout", (event) => {
    map.data.overrideStyle(event.feature, roadStyle);
    gsap.to(customInfoWindow, { display: "none", opacity: 0, duration: 0.2 });
  });
}

function drawOverlay(customInfoWindow) {
  const overlay = new google.maps.OverlayView();

  overlay.onAdd = function () {
    const layer = this.getPanes().overlayLayer;
    layer.appendChild(customInfoWindow);
  };
  overlay.draw = function () {};
  overlay.onRemove = function () {
    if (customInfoWindow.parentElement)
      customInfoWindow.parentElement.removeChild(customInfoWindow);
  };

  return overlay;
}

// 장소 검색 기능 추가
function addPlaceSearch(input, type = "start") {
  if (!map || !input) return;

  const searchBox = new google.maps.places.SearchBox(input);

  map.addListener("bounds_changed", () => {
    searchBox.setBounds(map.getBounds());
  });

  searchBox.addListener("places_changed", () => {
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
      if (type === "start") {
        if (startMarker) clearMarkers("start");
        const pinBackground = new google.maps.marker.PinElement({
          background: "#4E9525",
          borderColor: "#2E5A1C",
          glyphColor: "#2E5A1C",
        });
        markerSettings = { ...markerSettings, content: pinBackground.element };
        startMarker = new google.maps.marker.AdvancedMarkerElement(
          markerSettings
        );
        startLocation = place;
      } else if (type === "end") {
        if (endMarker) clearMarkers("end");
        const pinBackground = new google.maps.marker.PinElement({
          background: "#F23A3A",
          borderColor: "#C9182B",
          glyphColor: "#C9182B",
        });
        markerSettings = { ...markerSettings, content: pinBackground.element };
        endMarker = new google.maps.marker.AdvancedMarkerElement(
          markerSettings
        );
        endLocation = place;
      }
    });
  });
}

function clearMarkers(type = "both") {
  if ((type === "start" || type === "both") && startMarker) {
    startMarker.setMap(null);
    startMarker = null;
  }
  if ((type === "end" || type === "both") && endMarker) {
    endMarker.setMap(null);
    endMarker = null;
  }
}

// 길 안내
async function routing() {
  if (!startLocation || !endLocation) {
    alert("출발지 또는 목적지를 선택해야 합니다.");
    return;
  }

  const url = "/route/directions";
  const data = {
    startLng: startLocation.geometry.location.lng(),
    startLat: startLocation.geometry.location.lat(),
    endLng: endLocation.geometry.location.lng(),
    endLat: endLocation.geometry.location.lat(),
  };

  fetch(url + "?" + new URLSearchParams(data), {
    method: "GET",
    mode: "cors",
    cache: "no-cache",
    credential: "same-origin",
    header: { "Content-Type": "application/json" },
    redirect: "follow",
    referrerPolicy: "no-referrer",
  })
    .then((response) => response.json())
    .then((data) => {
      printRoadMap(data);
      const routeTotalInfoHTML = createRouteTotalInfoWrap(data);
      const routeInfoHTML = createRouteInfoWrap(data);

      // 'route-info-wrap' 요소 업데이트
      const routeInfoWrap = document.querySelector(".route-info-wrap");
      if (routeInfoWrap)
        routeInfoWrap.innerHTML = routeTotalInfoHTML + routeInfoHTML;
    })
    .catch((error) => console.error("Error:", error));
}

function createRouteTotalInfoWrap(path) {
  if (!path?.features) return "";

  const { totalLength, totalTime } = calculateTotalLengthAndTime(path.features);
  const arrivalTime = calculateArrivalTime(totalTime);

  return createRouteTotalInfoHTML(totalLength, totalTime, arrivalTime);
}

function calculateTotalLengthAndTime(features) {
  return features.reduce(
    (acc, feature) => {
      const info = feature?.properties;
      if (info) {
        acc.totalLength += info.length || 0;
        acc.totalTime += info.time || 0;
      }
      return acc;
    },
    { totalLength: 0, totalTime: 0 }
  );
}

function calculateArrivalTime(totalTime) {
  const now = new Date();
  const arrivalTime = new Date(now.getTime() + totalTime * 1000);

  const hours = arrivalTime.getHours() % 12 || 12;
  const minutes = arrivalTime.getMinutes().toString().padStart(2, "0");
  const period = arrivalTime.getHours() >= 12 ? "오후" : "오전";

  return `${period} ${hours}:${minutes}`;
}

function createRouteTotalInfoHTML(totalLength, totalTime, arrivalTime) {
  return `
    <div class="route-total-wrap">
      <p>
        <span id="route-time">${Math.round(totalTime / 60.0)}</span>분 |
        <span id="route-length">${(totalLength / 1000).toFixed(2)}</span>km
      </p>
      <p>
        <span id="route-arrival-time">${arrivalTime}</span> 도착 예정
      </p>
    </div>`;
}

function createRouteInfoWrap(path) {
  if (!path?.features) return "";

  const groupedFeatures = path.features.reduce((acc, feature) => {
    const roadName = feature.properties.roadName;

    if (acc.length > 0 && acc[acc.length - 1].roadName === roadName) {
      acc[acc.length - 1].features.push(feature);
    } else {
      acc.push({
        roadName: roadName,
        features: [feature],
      });
    }

    return acc;
  }, []);

  const routeInfoHTML = groupedFeatures
    .map((group) => {
      const totalLength = group.features.reduce(
        (sum, f) => sum + f.properties.length,
        0
      );
      const totalTime = group.features.reduce(
        (sum, f) => sum + f.properties.time,
        0
      );
      const firstFeature = group.features[0].properties;

      return `
        <div class="route-wrap">
          <span class="road-name">${group.roadName}</span>
          <span class="time">약 ${totalTime}초 소요</span>
          <br /><span class="length">${totalLength.toFixed(2)}m 이동</span>
        </div>`;
    })
    .join("");

  return `
    <div class="routes-wrap">
      ${routeInfoHTML}
    </div>`;
}

function clearRouteInfoWrap() {
  const routeInfoWrap = document.querySelector(".route-info-wrap");
  if (routeInfoWrap) routeInfoWrap.innerHTML = "";
}

// 장소 초기화
function resetLocation() {
  clearMarkers(); // 마커 지우기
  startLocation = null;
  endLocation = null;
  $("#start-location").val("");
  $("#end-location").val("");
  clearRoadMap(); // 도로 지우기
  clearRouteInfoWrap(); // 경로 정보 지우기
}
