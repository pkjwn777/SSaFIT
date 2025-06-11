<script setup>
import { RouterLink, RouterView } from "vue-router";
import { onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();

onMounted(() => {
  // 앱 시작 시 인증 상태 복원 시도
  auth.loadFromStorage();
});
</script>

<template>
  <RouterView />
</template>

<style scoped>
header {
  display: flex;
  justify-content: flex-end;
  padding: 1rem;
  background-color: #fff;
}

nav a {
  margin-left: 1rem;
  font-weight: bold;
  color: #333;
  text-decoration: none;
}
</style>
