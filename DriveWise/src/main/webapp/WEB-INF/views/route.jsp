<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ page
session="false" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>최적 경로 안내</title>
    <link rel="stylesheet" href="css/header.css" />
    <link rel="stylesheet" href="css/footer.css" />
    <link rel="stylesheet" href="css/route.css" />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/aos/2.3.4/aos.css"
      integrity="sha512-1cK78a1o+ht2JcaW6g8OXYwqpev9+6GqOkz9xmBN9iUUhIndKtxwILGWYOSibOKjLsEdjyjZvYDq/cZwNeak0w=="
      crossorigin="anonymous"
      referrerpolicy="no-referrer"
    />
  </head>
  <body>
    <!-- Navigation -->
    <header>
      <div class="logo-box">
        <a href="/">
          <img class="logo" src="images/logo.png" alt="드라이브와이즈 로고" />
        </a>
      </div>
      <nav class="nav">
        <ul class="nav-menu">
          <li>
            <a href="#">혼잡지표</a>
            <div class="sub-menu">
              <ul>
                <li><a href="#">개요</a></li>
                <li><a href="#">대시보드</a></li>
              </ul>
            </div>
          </li>
          <li>
            <a href="#">안전지표</a>
            <div class="sub-menu">
              <ul>
                <li><a href="#">개요</a></li>
                <li><a href="#">대시보드</a></li>
              </ul>
            </div>
          </li>
          <li>
            <a href="#">최적 경로 안내</a>
            <div class="sub-menu">
              <ul>
                <li><a href="#">최적 경로 안내</a></li>
              </ul>
            </div>
          </li>
        </ul>
      </nav>
      <div class="menu-box">
        <div class="menu-btn">☰</div>
      </div>
      <div class="mobile-nav-wrap">
        <div class="inner-wrap">
          <div class="mobile-nav">
            <ul class="nav-menu">
              <li>
                <a href="#">혼잡지표</a>
              </li>
              <li>
                <a href="#">안전지표</a>
              </li>
              <li>
                <a href="#">최적 경로 안내</a>
              </li>
            </ul>
          </div>
          <div class="info-wrap">
            <div class="link-wrap">
              <span class="link-title">팀 구성</span>
              <div class="link-list-wrap">
                <span>팀명: DriveWise</span>
                <span>팀원: 박상도, 박민후, 전세계, 한창민</span>
                <span>소속: 네이버클라우드캠프 AIaaS 8기</span>
              </div>
              <span class="link-title">문의사항</span>
              <div class="link-list-wrap">
                <span>Email: abcdef@google.com</span>
                <span>Phone: 010-1234-5678</span>
              </div>
            </div>
            <div class="icons-wrap">
              <a
                href="https://github.com/SD-PARK/DriveWise"
                class="sns"
                target="_blank"
              >
                <img src="/images/icon-github.png" alt="Github 링크" />
              </a>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Map -->
    <div id="map"></div>
    <div id="customInfoWindow" class="custom-info-window"></div>

    <!-- Route -->
    <div id="route" class="open">
      <div class="location-wrap">
        <input
          type="text"
          name="st_loc"
          id="start-location"
          maxlength="255"
          placeholder="출발지 입력"
        />
        <input
          type="text"
          name="ed_loc"
          id="end-location"
          maxlength="255"
          placeholder="도착지 입력"
        />
        <div class="btn-wrap">
          <button type="button" id="reset" onclick="resetLocation()">
            다시입력
          </button>
          <button type="button" id="routing" onclick="routing()">
            길 안내
          </button>
        </div>
      </div>

      <div class="route-info-wrap"></div>

      <div class="pulling-btn" onclick="pullingRoute()">〉</div>
    </div>
  </body>
  <script src="https://cdn.jsdelivr.net/npm/cash-dom/dist/cash.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/ScrollTrigger.min.js"></script>
  <script
    src="https://cdnjs.cloudflare.com/ajax/libs/aos/2.3.4/aos.js"
    integrity="sha512-A7AYk1fGKX6S2SsHywmPkrnzTZHrgiVT7GcQkLGDe2ev0aWb8zejytzS8wjo7PGEXKqJOrjQ4oORtnimIRZBtw=="
    crossorigin="anonymous"
    referrerpolicy="no-referrer"
  ></script>
  <script src="js/header.js"></script>
  <script src="js/route.js"></script>
  <script
    async
    defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDrvx7MRCldS49QgZKycSh1oB6Ue_VJ4_8&callback=initMap&loading=async&libraries=places,marker"
  ></script>
</html>
