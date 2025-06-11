<template>
  <footer class="footer">
    <div class="footer-content">
      <!-- 상단 구분선 -->
      <div class="footer-divider"></div>

      <!-- 로고 및 정보 -->
      <div class="footer-info">
        <div class="footer-left">
          <h2 class="footer-logo">SSaFIT</h2>
          <p class="footer-email">ssafit@ssafy.com</p>
          <div class="footer-address">
            서울특별시 강남구
            <br />
            테헤란로 212
          </div>
        </div>

        <!-- 네비게이션 링크 -->
        <div class="footer-nav">
          <div class="nav-column">
            <h3 class="nav-title">Topic</h3>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
          </div>
          <div class="nav-column">
            <h3 class="nav-title">Topic</h3>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
          </div>
          <div class="nav-column">
            <h3 class="nav-title">Topic</h3>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
            <a href="#" class="nav-link">Page</a>
          </div>
        </div>
      </div>

      <!-- 소셜 미디어 아이콘 -->
      <div class="social-icons">
        <a href="#" class="social-icon">
          <i class="fab fa-facebook"></i>
        </a>
        <a href="#" class="social-icon">
          <i class="fab fa-linkedin"></i>
        </a>
        <a href="#" class="social-icon">
          <i class="fab fa-youtube"></i>
        </a>
        <a href="#" class="social-icon">
          <i class="fab fa-instagram"></i>
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup>
// Font Awesome 아이콘을 사용하기 위한 import는 main.js에서 처리
</script>

<style scoped>
.footer {
  width: 100%;
  padding: 2rem 0;
  background-color: white;
}

.footer-content {
  width: 100%;
  margin: 0 auto;
  padding: 0 1rem;
  position: relative;
}

/* 반응형 최대 너비 설정 */
@media (min-width: 640px) {
  .footer-content {
    max-width: 640px;
    padding: 0 1.5rem;
  }
}

@media (min-width: 768px) {
  .footer-content {
    max-width: 768px;
    padding: 0 2rem;
  }
}

@media (min-width: 1024px) {
  .footer-content {
    max-width: 1024px;
  }
}

@media (min-width: 1280px) {
  .footer-content {
    max-width: 1440px;
  }
}

@media (min-width: 1536px) {
  .footer-content {
    max-width: 1800px;
  }
}

.footer-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin-bottom: 2rem;
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-bottom: 3rem;
}

/* 태블릿 이상에서 가로 배치 */
@media (min-width: 768px) {
  .footer-info {
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0;
  }
}

.footer-left {
  display: flex;
  flex-direction: column;
  padding-top: 0.75rem;
}

.footer-logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: #000;
  line-height: 1;
  margin: 0;
}

.footer-email {
  font-size: 0.875rem;
  color: #666;
  margin: 2px 0 0 0;
}

.footer-address {
  font-size: 0.875rem;
  color: #666;
  margin-top: 0.75rem;
  line-height: 1.5;
}

.footer-nav {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
}

/* 태블릿 이상에서 3열 그리드 */
@media (min-width: 768px) {
  .footer-nav {
    display: flex;
    gap: 4rem;
  }
}

@media (min-width: 1280px) {
  .footer-nav {
    gap: 6rem;
  }
}

@media (min-width: 1536px) {
  .footer-nav {
    gap: 8rem;
  }
}

.nav-column {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.nav-title {
  font-size: 1rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 0.5rem;
  line-height: 1;
}

.nav-link {
  font-size: 0.875rem;
  color: #666;
  text-decoration: none;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #000;
}

.social-icons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  padding: 1rem 0;
}

/* 태블릿 이상에서 위치 조정 */
@media (min-width: 768px) {
  .social-icons {
    position: absolute;
    left: 2rem;
    transform: translateY(-2rem);
    padding: 0;
  }
}

@media (min-width: 1280px) {
  .social-icons {
    left: 3rem;
  }
}

@media (min-width: 1536px) {
  .social-icons {
    left: 4rem;
  }
}

.social-icon {
  color: #666;
  font-size: 1.25rem;
  text-decoration: none;
  transition: color 0.2s;
}

.social-icon:hover {
  color: #000;
}
</style>
