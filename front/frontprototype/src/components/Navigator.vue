<template>
  <nav :class="['navigator', { 'white-theme': whiteTheme }]">
    <div class="menu-group">
      <RouterLink to="/user/search" class="nav-link">User Search</RouterLink>
      <span class="divider">|</span>
      <RouterLink to="/mypage" class="nav-link">My Page</RouterLink>
      <span class="divider">|</span>
      <RouterLink to="/video/search" class="nav-link">Video Search</RouterLink>
    </div>

    <template v-if="isLoggedIn">
      <button @click="logout" class="auth-button">SIGN OUT</button>
    </template>
    <template v-else>
      <RouterLink to="/login" class="auth-button">SIGN IN</RouterLink>
    </template>
  </nav>
</template>

<script setup>
import { computed } from "vue";
import { useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const props = defineProps({
  whiteTheme: { type: Boolean, default: false },
});

const auth = useAuthStore();
const router = useRouter();
const isLoggedIn = computed(() => auth.isLoggedIn);

const logout = async () => {
  try {
    await auth.clearAuth();
    router.push("/login");
  } catch (error) {
    console.error("로그아웃 중 오류 발생:", error);
    // 오류가 발생해도 로컬 상태는 초기화하고 로그인 페이지로 이동
    auth.clearAuth();
    router.push("/login");
  }
};
</script>

<style scoped>
.navigator {
  display: flex;
  align-items: center;
  gap: 2rem;
  font-size: 0.8rem;
  color: #4a5568;
}

.menu-group {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-link {
  text-decoration: none;
  color: inherit;
  font-weight: 500;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #000;
}

.divider {
  color: #e2e8f0;
  font-weight: 300;
}

.auth-button {
  padding: 0.35rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  background-color: transparent;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  color: inherit;
  text-decoration: none;
  transition: all 0.2s;
}

.auth-button:hover {
  background-color: #f8fafc;
  border-color: #cbd5e0;
}

/* White theme styles */
.white-theme {
  color: white;
}

.white-theme .divider {
  color: rgba(255, 255, 255, 0.5);
}

.white-theme .auth-button {
  border-color: white;
  color: white;
}

.white-theme .auth-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>
