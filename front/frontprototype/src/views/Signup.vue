<template>
  <div class="signup-page">
    <!-- 텍스트 (컨테이너 바로 위) -->
    <div class="title-section">
      <h1 class="title-text">Let’s SSaFIT</h1>
      <p class="subtitle-text">SSaFIT과 함께 사람들과 함께 운동해보세요.</p>
    </div>

    <!-- 흰색 컨테이너 -->
    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="input-label">ID</label>
          <input type="text" v-model="id" class="input-field" required @input="id = id.replace(/[^\wㄱ-ㅎ가-힣]/g, '')" />
        </div>
        <div>
          <label class="input-label">Password</label>
          <input type="password" v-model="password" class="input-field" required @input="password = password.replace(/[^a-zA-Z0-9]/g, '')" />
        </div>
        <!-- Email 입력란 -->
        <div>
          <label class="input-label">Email</label>
          <input type="email" v-model="email" class="input-field" required />
        </div>
        <div style="margin-top: 1rem">
          <button type="submit" class="submit-button">Submit</button>
        </div>
      </form>
    </div>
  </div>

  <!-- 페이지 하단 SSaFIT 홈 링크 -->
  <div class="mt-10 text-center">
    <RouterLink to="/" class="text-[10px] text-gray-400 hover:text-black font-semibold">SSaFIT</RouterLink>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const id = ref("");
const password = ref("");
const email = ref("");
const router = useRouter();

const handleSubmit = async () => {
  try {
    await axios.post("/user/regist", {
      userId: id.value,
      userPassword: password.value,
      userEmail: email.value,
    });
    router.push("/login");
  } catch (e) {
    alert("회원가입 실패: 입력 정보를 확인하세요.");
  }
};
</script>

<style scoped>
.signup-page {
  background-image: url("@/assets/signup.png");
  background-size: cover;
  background-position: center;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.title-section {
  margin-bottom: 1.5rem;
  text-align: center;
}

.title-text {
  font-size: 2rem;
  font-weight: 700;
  color: white !important;
  margin-bottom: 0.5rem;
}

.subtitle-text {
  font-size: 0.875rem;
  color: white !important;
}

.form-container {
  background-color: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.2);
  width: 300px;
  z-index: 10;
}

.input-field {
  width: 100%;
  height: 40px;
  padding: 0 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
  box-sizing: border-box;
}

.submit-button {
  width: 100%;
  height: 45px;
  background-color: black;
  color: white;
  font-weight: bold;
  border-radius: 8px;
  border: none;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #222;
}

.input-label {
  font-size: 13px;
  color: #4b5563;
  margin-bottom: 0.25rem;
  display: block;
}
</style>
