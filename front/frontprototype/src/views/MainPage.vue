<template>
  <div>
    <!-- Hero Section -->
    <div class="hero">
      <img src="@/assets/header.jpg" alt="SSaFIT Header" class="hero-image" />

      <!-- ✅ Hero 안쪽에 흰색 테마 네비게이터 배치 -->
      <div class="navigator-overlay">
        <Navigator :whiteTheme="true" />
      </div>

      <!-- 텍스트와 버튼 -->
      <div class="hero-content">
        <h1 class="logo-text">SSaFIT</h1>
        <p class="slogan-text">함께 운동하며 얻는 즐거움</p>
        <RouterLink v-if="!isLoggedIn" to="/signup">
          <button class="start-button">시작하기</button>
        </RouterLink>
      </div>

      <div class="hero-overlay"></div>
    </div>

    <!-- 비디오 캐러셀 -->
    <main class="main-section">
      <VideoCarousel orderKey="viewCnt" title="인기 동영상" />
      <VideoCarousel orderKey="videoKey" title="최신 동영상" />
    </main>

    <!-- 푸터 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Navigator from '@/components/Navigator.vue'
import VideoCarousel from '@/components/VideoCarousel.vue'
import Footer from '@/components/Footer.vue'
import { RouterLink } from 'vue-router'

const isLoggedIn = ref(false)

onMounted(() => {
  const token = localStorage.getItem('accessToken')
  isLoggedIn.value = !!token
})
</script>

<style scoped>
.hero {
  position: relative;
  width: 100%;
  height: 50vh;
  overflow: hidden;
}

.hero-image {
  position: absolute;
  width: 100%;
  height: 100%;
  object-fit: cover;
  top: 0;
  left: 0;
  z-index: 1;
}

/* ✅ Navigator: 오른쪽 위 고정 */
.navigator-overlay {
  position: absolute;
  top: 1rem;
  right: 2rem;
  z-index: 3;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 2;
}

/* ✅ Hero content: 정확히 가운데 위치 */
.hero-content {
  position: absolute;
  top: 50%;
  left: 6rem;
  transform: translateY(-50%);
  z-index: 3;
  display: flex;
  flex-direction: column;
  color: white;
}

.logo-text {
  font-size: 5rem;
  font-weight: 900;
  margin-bottom: -1.5rem;
}

.slogan-text {
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
}

.start-button {
  padding: 0.7rem 1.5rem;
  background-color: white;
  color: black;
  border: none;
  border-radius: 0.5rem;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  transition: background-color 0.2s;
}

.start-button:hover {
  background-color: white;
}

.main-section {
  padding: 2rem;
  background-color: white;
}
</style>
