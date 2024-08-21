gsap.registerPlugin(ScrollTrigger);

let startMarker = null;
let linkColorMap = new Map();
let currentIndex = 1; // 임의의 인덱스 초기값

let districtDataLayer = null;
let roadDataLayer = null;

let map;
let isDistrictEnabled = false; // 행정구역이 활성화 상태인지 여부를 추적
let lastClickedFeature = null;

let districtData = [];

// link_Id와 TCI_Index를 저장할 변수
// let link_Id = null;
// let TCI_Index = null;

// let TCIMap = new Map();
// function fullLinkTCI() {
//     const url = 'your-java-backend-endpoint'; // 실제 Java 백엔드의 엔드포인트를 여기에 입력하세요

//     fetch(url, {
//         method: 'GET',
//         headers: { 'Content-Type': 'application/json' },
//     })
//     .then(response => response.json())
//     .then(data => {
//         TCIMap.set('link_Id', data.linkId);
//         TCIMap.set('TCI_Index', data.tciIndex);
//         TCIMap.set('link_Name', data.linkName);
//     })
//     .catch(error => console.error('Error fetching data:', error));
// }

const mockData = [
  { linkId: 4050262400, linkName: "봉개동길", TCI: 82.3 },
  { linkId: 4060374600, linkName: "일주동로", TCI: 47.6 },
  { linkId: 4060532300, linkName: "시흥상동로", TCI: 92.7 },
  { linkId: 4060259700, linkName: "태위로723번길", TCI: 34.2 },
  { linkId: 4050531300, linkName: "번영로", TCI: 58.9 },
  { linkId: 4050069300, linkName: "구산로1길", TCI: 23.4 },
  { linkId: 4050529000, linkName: "번영로", TCI: 76.5 },
  { linkId: 4060390200, linkName: "태위로", TCI: 41.8 },
  { linkId: 4060385600, linkName: "하례로", TCI: 65.7 },
  { linkId: 4060001802, linkName: "일주동로", TCI: 89.1 },
  { linkId: 4050621001, linkName: "세송로", TCI: 15.6 },
  { linkId: 4060420500, linkName: "산록남로762번길", TCI: 97.3 },
  { linkId: 4060415400, linkName: "원님서로", TCI: 54.9 },
  { linkId: 4050508400, linkName: "항몽로", TCI: 61.2 },
  { linkId: 4050093800, linkName: "우정로12길", TCI: 30.5 },
];
const mockDistrictData = [
  { edmCD: 50110101, emdName: "일도일동", avgTCI: 82.3 },
  { emdCD: 50110102, emdName: "일도이동", avgTCI: 78.5 },
  { emdCD: 50110103, emdName: "이도일동", avgTCI: 65.2 },
  { emdCD: 50110104, emdName: "이도이동", avgTCI: 82.1 },
  { emdCD: 50110105, emdName: "삼도일동", avgTCI: 47.3 },
  { emdCD: 50110106, emdName: "삼도이동", avgTCI: 59.4 },
  { emdCD: 50110107, emdName: "건입동", avgTCI: 73.8 },
  { emdCD: 50110108, emdName: "용담일동", avgTCI: 85.7 },
  { emdCD: 50110109, emdName: "용담이동", avgTCI: 69.5 },
  { emdCD: 50110110, emdName: "용담삼동", avgTCI: 54.9 },
  { emdCD: 50110111, emdName: "화북일동", avgTCI: 61.3 },
  { emdCD: 50110112, emdName: "화북이동", avgTCI: 90.1 },
  { emdCD: 50110113, emdName: "삼양일동", avgTCI: 72.4 },
  { emdCD: 50110114, emdName: "삼양이동", avgTCI: 66.7 },
  { emdCD: 50110115, emdName: "삼양삼동", avgTCI: 88.6 },
  { emdCD: 50110116, emdName: "봉개동", avgTCI: 55.2 },
  { emdCD: 50110117, emdName: "아라일동", avgTCI: 64.8 },
  { emdCD: 50110118, emdName: "아라이동", avgTCI: 79.3 },
  { emdCD: 50110119, emdName: "오라일동", avgTCI: 70.7 },
  { emdCD: 50110120, emdName: "오라이동", avgTCI: 83.9 },
];

function loadDistrictData() {
  districtDataLayer = new google.maps.Data();
  districtDataLayer.loadGeoJson("./resources/emd.geojson", null, (features) => {
    features.forEach((feature) => {
      districtData.push({
        emdCD: feature.getProperty("EMD_CD"),
        emdName: feature.getProperty("EMD_KOR_NM"),
        geometry: feature.getGeometry(),
      });
    });
  });
}

// 도로가 속한 행정구역을 확인하는 함수
function checkRoadInDistrict(roadFeature) {
  const roadGeometry = roadFeature.getGeometry();
  let districtName = null;
  let emdCD = null;

  districtData.forEach((district) => {
    districtName = district.emdName;
    emdCD = district.emdCD;
  });

  return districtName;
}

//GeoJson 도로 정보 받아오기
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
    mapId: "dd6d20a5306a96c5",
    restriction: {
      latLngBounds: jejuBounds,
      strictBounds: false,
    },
    mapTypeControl: false,
    streetViewControl: false,
    fullscreenControl: false,
  };

  map = new google.maps.Map(document.getElementById("map"), mapOptions);
  loadRoads(); // 기본 도로 정보 로드
  loadDistrictData(); // 행정구역 데이터 로드
  addPlaceSearch(map, document.getElementById("start-location"), "start");
  // fullLinkTCI(); // 서버 TCI 선언 함수 호출
}

// 도로 정보 로드
function loadRoads() {
  clearLayer(districtDataLayer);

  if (!roadDataLayer) {
    roadDataLayer = createRoadLayer();
  }

  fetchAndDisplayTop10Links();
  roadDataLayer.setMap(map);
}

function createRoadLayer() {
  const layer = new google.maps.Data();
  layer.loadGeoJson("./resources/jeju_link.geojson");
  layer.setStyle(styleRoadFeature);
  infoWindowEvents(layer);
  return layer;
}

// 도로 스타일 설정
function styleRoadFeature(feature) {
  const linkId = feature.getProperty("LINK_ID");
  let color = linkColorMap.has(linkId)
    ? linkColorMap.get(linkId)
    : assignColor(linkId);
  return { strokeColor: color, strokeWeight: 4 };
}

function assignColor(linkId) {
  const index = currentIndex;
  currentIndex = (currentIndex % 4) + 1;
  const color = getColorByIndex(index);
  linkColorMap.set(linkId, color);
  return color;
}

function loadDistricts() {
  clearLayer(roadDataLayer);
  districtDataLayer = createDistrictLayer();

  fetchAndDisplayTop10Districts();
  districtDataLayer.setMap(map);
}
function clearLayer(layer) {
  if (layer) {
    layer.setMap(null);
  }
}

function createDistrictLayer() {
  districtDataLayer.setStyle(setDistrictStyle);
  setupDistrictLayerEvents(districtDataLayer);
  return districtDataLayer;
}
function setDistrictStyle(feature) {
  return feature.getGeometry().getType() === "Polygon" ||
    feature.getGeometry().getType() === "MultiPolygon"
    ? { strokeColor: "#bbcb0f", strokeWeight: 1.0, fillColor: "white" }
    : styleDefault;
}

const styleDefault = {
  strokeOpacity: 1.0,
  strokeWeight: 2.0,
  fillColor: "blue",
  fillOpacity: 0.2,
};
const styleClicked = {
  ...styleDefault,
  fillColor: "#810FCB",
  fillOpacity: 0.5,
};
const styleMouseOver = { ...styleDefault, strokeWeight: 2.0 };

function setupDistrictLayerEvents(layer) {
  const infoWindow = new google.maps.InfoWindow();

  layer.addListener("click", (event) => handleDistrictClick(event, layer));
  layer.addListener("mouseover", (event) =>
    handleDistrictMouseOver(event, layer, infoWindow)
  );
  layer.addListener("mouseout", (event) =>
    handleDistrictMouseOut(event, layer, infoWindow)
  );
}

function handleDistrictClick(event, layer) {
  const clickedFeature = event.feature;

  if (lastClickedFeature) {
    layer.revertStyle(lastClickedFeature);
    lastClickedFeature = null;
  }

  // 행정구역 정보 가져오기
  const mapData = setDistrictMapData(event);

  // 가져온 정보를 HTML로 변환하여 road-info 요소에 표시
  const roadInfoElement = document.getElementById("road-info");
  if (roadInfoElement) {
    roadInfoElement.innerHTML = InfoWindowInnerHTML(event);
  }

  lastClickedFeature = clickedFeature;
  layer.overrideStyle(clickedFeature, styleClicked);
  const route = document.getElementById("route");

  selectButton(document.getElementById("road-info-btn"));
  document.querySelectorAll("#TCI-realTime-rank").forEach((container) => {
    container.style.display = "none"; // Hide rank sections
  });
  document.getElementById("road-info").style.display = "flex"; // Show road info section
  document.getElementById("real-time-rank").style.display = "none";
  document.getElementById("selected-road-info").style.display = "flex";
  if (route && !route.classList.contains("open")) {
    route.classList.add("open");
    const pullingBtn = route.querySelector(".pulling-btn");
    pullingBtn.innerHTML = "〉";
    pullingBtn.style.textAlign = "right";
  }
}

function handleDistrictMouseOver(event, layer, infoWindow) {
  const hoveredFeature = event.feature;

  if (hoveredFeature !== lastClickedFeature) {
    const districtName = hoveredFeature.getProperty("EMD_KOR_NM");
    const position = event.latLng;

    infoWindow.setContent(
      `<div class="info-window"><div class="info-window-title">${districtName}</div></div>`
    );
    infoWindow.setPosition(position);
    infoWindow.open(map);

    layer.revertStyle();
    layer.overrideStyle(hoveredFeature, styleMouseOver);
  }
}

function handleDistrictMouseOut(event, layer, infoWindow) {
  infoWindow.close();
  if (event.feature !== lastClickedFeature) {
    layer.revertStyle(event.feature);
  }
}

/*
// 서버에서 LinkVO 데이터를 받아와 TCI로 상위 10개를 선택하는 함수
function fetchAndDisplayTop10Links() {
  // const url = "/getLinks";
  // fetch(url, {
  //   method: "POST",
  //   headers: { "content-Type": "application/json" },
  // })
  //   .then((response) => response.json())
  //   .then((data) => displayLinks(data))
  //   .catch((error) => console.error("Error:", error));
  displayLinks(mockData);
}
  */
function fetchAndDisplayTop10Links() {
  displayLinks(mockData, "linkName", "TCI", "linkId");
}

// 행정구역 데이터를 받아 상위 10개를 출력하는 함수
function fetchAndDisplayTop10Districts() {
  displayLinks(mockDistrictData, "emdName", "avgTCI", "emdCD");
}

// 데이터를 받아서 내림차순 정렬 후 상위 10개를 분류하고 출력하는 함수
function displayLinks(data, nameKey, valueKey, idKey) {
  const top10Items = data
    .sort((a, b) => b[valueKey] - a[valueKey])
    .slice(0, 10);
  const rankSection = document.getElementById("TCI-realTime-rank");
  rankSection.innerHTML = "";
  top10Items.forEach((item, index) =>
    appendLink(item, index + 1, rankSection, nameKey, valueKey)
  );
}

// HTML 출력 시 TCI-realtime-rank에 동적 section 출력
function appendLink(item, rank, parentElement, nameKey, valueKey, idKey) {
  const section = document.createElement("section");
  section.className = "rank-section";
  section.setAttribute("tabindex", "0"); // 키보드 포커스를 받을 수 있도록 tabindex 추가
  section.setAttribute("role", "button"); // 역할을 버튼으로 지정
  section.style.cursor = "pointer";
  section.innerHTML = `
    <div class="rank-index">${rank}</div>
    <div class="rank-road-info">
      <div class="road-name"><span class="road-infos-key">${
        nameKey === "linkName" ? "도로명" : "지역명"
      }</span>: <span class="road-infos-value">
      ${item[nameKey]}
      </span></div>
      <div class="road-tci"><span class="road-infos-key">TCI</span>: <span class="road-infos-value">
      ${item[valueKey].toFixed(1)}
      </span></div>
    </div>`;

  // 클릭 이벤트 리스너 추가
  parentElement.appendChild(section);

  section.addEventListener("click", () => {
    if (lastClickedFeature) {
      map.data.revertStyle(lastClickedFeature);
    }
    nameKey === "linkName"
      ? zoomToRoad(item.linkId)
      : zoomToDistrict(item.emdCD);
  });
}

// 해당 도로로 지도 이동 및 확대 (줌 레벨 15)
function zoomToRoad(linkId) {
  roadDataLayer.forEach((feature) => {
    if (feature.getProperty("LINK_ID") == linkId) {
      const bounds = new google.maps.LatLngBounds();
      feature.getGeometry().forEachLatLng((latlng) => {
        bounds.extend(latlng);
      });
      map.fitBounds(bounds);
      map.setZoom(15);
      roadDataLayer.overrideStyle(feature, {
        strokeColor: "purple",
        strokeWeight: 5,
      });
      lastClickedFeature = feature; // 클릭된 피처를 저장
    }
  });
}

// 해당 지역으로 지도 이동 및 확대 (줌 레벨 15)
function zoomToDistrict(emdCD) {
  districtDataLayer.forEach((feature) => {
    if (feature.getProperty("EMD_CD") == emdCD) {
      const bounds = new google.maps.LatLngBounds();
      feature.getGeometry().forEachLatLng((latlng) => {
        bounds.extend(latlng);
      });
      map.fitBounds(bounds);
      map.setZoom(15);
      districtDataLayer.overrideStyle(feature, {
        fillColor: "purple",
        fillOpacity: 0.6,
      });
      lastClickedFeature = feature; // 클릭된 피처를 저장
    }
  });
}

// 단계별 색상을 반환하는 함수
function getColorByIndex(index) {
  const colorMap = {
    1: "red",
    2: "orange",
    3: "yellow",
    4: "green",
  };
  return colorMap[index] || "pink";
}

// 페이지 우측 경로 안내 박스 ON/OFF
function toggleRouteBox() {
  const route = document.getElementById("route");
  if (!route) return;

  route.classList.toggle("open");
  const pullingBtn = route.querySelector(".pulling-btn");
  const isOpen = route.classList.contains("open");
  pullingBtn.innerHTML = isOpen ? "〉" : "〈";
  pullingBtn.style.textAlign = isOpen ? "right" : "left";
}

//도로 정보 출력
function InfoWindowInnerHTML(event) {
  let map;

  // 클릭된 피처가 도로인지 행정구역인지 확인
  if (event.feature.getProperty("LINK_ID")) {
    map = setMapData(event); // 도로 데이터 처리
  } else if (event.feature.getProperty("EMD_CD")) {
    map = setDistrictMapData(event); // 행정구역 데이터 처리
  } else {
    return ""; // 처리할 수 없는 피처는 빈 문자열 반환
  }

  let result = "";

  // Map의 각 항목을 순회하며 결과 문자열을 생성합니다.
  map.forEach((value, key) => {
    result += `<div class="road-infos">${key}: ${value}</div>`;
  });

  return result;
}

// 도로정보
function setMapData(event) {
  const map = new Map();

  if (!event) return map;

  // linkId에 해당하는 TCI를 찾습니다.
  const linkId = event.feature.getProperty("LINK_ID");
  const linkData = mockData.find((link) => link.linkId == linkId);
  map.set("TCI", linkData ? linkData.TCI.toFixed(1) : "no data");

  // 속성 값을 맵에 추가합니다.
  map.set("도로명", event.feature.getProperty("ROAD_NAME"));
  map.set("길이", event.feature.getProperty("LENGTH").toFixed(2) + "km");
  map.set("차로 수", event.feature.getProperty("LANES"));
  map.set("제한속도", event.feature.getProperty("MAX_SPD") + "km/h");

  const districtName = checkRoadInDistrict(event.feature);
  map.set("행정구역", districtName || "Unknown");
  return map;
}
function setDistrictMapData(event) {
  const map = new Map();

  if (!event) return map;

  // 행정구역 정보를 맵에 추가
  const districtName = event.feature.getProperty("EMD_KOR_NM");
  map.set("행정구역명", districtName);

  // mockDistrictData에서 해당 행정구역의 avgTCI를 찾습니다.
  const emdCD = event.feature.getProperty("EMD_CD");
  const districtData = mockDistrictData.find(
    (district) => district.emdCD == emdCD
  );
  const avgTCI = districtData ? districtData.avgTCI.toFixed(1) : "기본값: ";

  map.set("평균 TCI", avgTCI);

  return map;
}

// 토글 스위치 이벤트 리스너
document.getElementById("toggle-switch").addEventListener("click", function () {
  isDistrictEnabled = !isDistrictEnabled; // 상태를 토글
  if (isDistrictEnabled) {
    loadDistricts(); // 행정구역 로드
    this.classList.add("active"); // 활성화된 상태로 스타일 변경
    this.textContent = "도로 정보"; // 버튼 텍스트 변경
  } else {
    loadRoads(); // 기본 도로 정보 로드
    this.classList.remove("active"); // 비활성화 상태로 스타일 변경
    this.textContent = "행정구역"; // 버튼 텍스트 변경
  }
});

// 이벤트에 따른 출력기능
function infoWindowEvents(dataLayer) {
  const customInfoWindow = document.getElementById("customInfoWindow");
  if (!customInfoWindow) return;

  const roadInfo = document.getElementById("road-info");
  if (!roadInfo) return;

  const overlay = drawOverlay(customInfoWindow);
  overlay.setMap(map);

  dataLayer.addListener("mouseover", (event) => {
    const mapData = setMapData(event);

    customInfoWindow.innerHTML = InfoWindowInnerHTML(event, mapData);
    gsap.to(customInfoWindow, { display: "block", opacity: 1, duration: 0.2 });

    dataLayer.overrideStyle(event.feature, {
      strokeColor: "blue",
      strokeWeight: 5,
    });

    updateInfoWindowPosition(event, overlay, customInfoWindow);
  });

  dataLayer.addListener("mousemove", (event) => {
    updateInfoWindowPosition(event, overlay, customInfoWindow);
  });

  dataLayer.addListener("mouseout", (event) => {
    const linkId = event.feature.getProperty("LINK_ID");
    const color = linkColorMap.get(linkId);
    dataLayer.overrideStyle(event.feature, {
      strokeColor: color,
      strokeWeight: 4,
    });
    gsap.to(customInfoWindow, { display: "none", opacity: 0, duration: 0.2 });
  });

  dataLayer.addListener("click", (event) => {
    const mapData = setMapData(event);

    roadInfo.innerHTML = InfoWindowInnerHTML(event, mapData);
    updateInfoWindowPosition(event, overlay, customInfoWindow);
    // 도로 정보 버튼 활성화 및 실시간 버튼 비활성화
    selectButton(document.getElementById("road-info-btn"));
    document.querySelectorAll("#TCI-realTime-rank").forEach((container) => {
      container.style.display = "none"; // Hide rank sections
    });
    document.getElementById("road-info").style.display = "flex"; // Show road info section
    document.getElementById("real-time-rank").style.display = "none";
    document.getElementById("selected-road-info").style.display = "flex";

    // 클릭된 도로의 행정구역 확인
    const districtName = checkRoadInDistrict(event.feature);

    // route가 닫혀있을 경우 열어준다.
    const route = document.getElementById("route");
    if (route && !route.classList.contains("open")) {
      route.classList.add("open");
      const pullingBtn = route.querySelector(".pulling-btn");
      pullingBtn.innerHTML = "〉";
      pullingBtn.style.textAlign = "right";
    }
  });
}
function updateInfoWindowPosition(event, overlay, customInfoWindow) {
  const projection = overlay.getProjection();
  const position = projection.fromLatLngToDivPixel(event.latLng);
  gsap.set(customInfoWindow, {
    left: `${position.x}px`,
    top: `${position.y}px`,
  });
}
//도로 그리기
function drawOverlay(customInfoWindow) {
  const overlay = new google.maps.OverlayView();

  overlay.onAdd = function () {
    this.getPanes().overlayLayer.appendChild(customInfoWindow);
  };
  overlay.draw = function () {};
  overlay.onRemove = function () {
    if (customInfoWindow.parentElement)
      customInfoWindow.parentElement.removeChild(customInfoWindow);
  };

  return overlay;
}

// 장소 검색 기능 추가
function addPlaceSearch(map, input, type = "start") {
  if (!input) return;

  const searchBox = new google.maps.places.SearchBox(input);

  map.addListener("bounds_changed", () => {
    searchBox.setBounds(map.getBounds());
  });

  searchBox.addListener("places_changed", () => {
    const places = searchBox.getPlaces();
    if (places.length === 0) return;

    // 검색 결과 위치로 지도 이동
    places.forEach((place) => {
      if (!place.geometry || !place.geometry.location) {
        return;
      }
      map.panTo(place.geometry.location);
      map.setZoom(15);
      setStartMarker(type, place);
    });
  });
}

// 위치 정보 저장
function setStartMarker(type, place) {
  if (type === "start") {
    clearMarkers("start");
    startMarker = new google.maps.Marker({
      position: place.geometry.location,
      map: map,
      title: place.name,
      gmpClickable: true,
    });
  }
}

// 마커 초기화
function clearMarkers(type = "both") {
  if ((type === "start" || type === "both") && startMarker) {
    startMarker.setMap(null);
    startMarker = null;
  }
}

// --------------------------------------------------------------------
// 버튼 선택을 처리하는 함수
function selectButton(button) {
  // 모든 버튼의 선택을 해제합니다.
  document.querySelectorAll(".output-btn").forEach((btn) => {
    btn.classList.remove("selected");
    btn.style.backgroundColor = ""; // 배경색 초기화
  });
  // 클릭된 버튼을 선택합니다.
  button.classList.add("selected");
  button.style.backgroundColor = "red"; // 선택된 색상 설정
}

// 버튼에 이벤트 리스너를 추가합니다.
document.getElementById("road-info-btn").addEventListener("click", function () {
  selectButton(this);
  showRoadInfoSection();
  hideAllRankSections();
});

document.getElementById("realtime-btn").addEventListener("click", function () {
  selectButton(this);
  showRankSections();
  hideRoadInfoSection();
});

// 초기 로드 시 첫 번째 버튼을 기본 선택 상태로 설정합니다.
document.addEventListener("DOMContentLoaded", function () {
  selectButton(document.querySelector(".output-btn:first-child"));
  showRankSections();
  hideRoadInfoSection();
  // fetchAndDisplayTop10Links();
});
function showRankSections() {
  document.querySelectorAll("#TCI-realTime-rank").forEach((container) => {
    container.style.display = "flex";
  });
}

function hideAllRankSections() {
  document.querySelectorAll("#TCI-realTime-rank").forEach((container) => {
    container.style.display = "none";
  });
}

function showRoadInfoSection() {
  document.getElementById("road-info").style.display = "flex";
  document.getElementById("selected-road-info").style.display = "flex";
}

function hideRoadInfoSection() {
  document.getElementById("road-info").style.display = "none";
  document.getElementById("selected-road-info").style.display = "none";
}

// 장소 초기화
function resetLocation() {
  clearMarkers(); // 마커 지우기
  startLocation = null;
  document.getElementById("start-location").value = "";
}
