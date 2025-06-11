<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="logo-area">
        <!-- ✅ RouterLink로 변경 -->
        <RouterLink to="/" class="logo-text-link">
          <h1 class="logo-text">SSaFIT</h1>
          <p class="sub-text">함께 운동하며 얻는 즐거움</p>
        </RouterLink>
      </div>
      <Navigator />
    </div>
  </header>
</template>

<script setup>
import Navigator from "./Navigator.vue";
import { RouterLink } from "vue-router"; // ✅ RouterLink 사용 시 import 필요
</script>

<style scoped>
.app-header {
  border-bottom: 2px solid #000000;
  margin-bottom: 2rem;
}

.header-inner {
  max-width: 1024px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  height: 4rem;
}

.logo-area {
  display: flex;
  align-items: center;
}

.logo-text-link {
  text-decoration: none;
  color: inherit;
  display: flex;
  flex-direction: column;
}

.logo-text {
  font-size: 2rem;
  font-weight: 900;
  margin: 0;
  line-height: 1;
  letter-spacing: -0.02em;
}

.sub-text {
  font-size: 0.7rem;
  color: #6b7280;
  margin: 0.2rem 0 0 0;
}
</style>
