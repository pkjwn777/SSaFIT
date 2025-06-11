<template>
  <div class="min-h-screen flex items-center justify-center bg-white px-4">
    <div class="w-full max-w-[420px]">
      <!-- 제목 -->
      <h1 class="text-4xl font-bold text-center mb-2">Please sign in</h1>
      <p class="text-sm text-gray-600 text-center mb-8 leading-snug">
        <span class="block">로그인을 하시면 더 많은 서비스를 이용하실 수 있습니다.</span>
        <span class="block">SSaFIT과 함께 활기찬 하루를 시작하세요.</span>
      </p>

      <!-- 로그인 폼 -->
      <form class="space-y-4" @submit.prevent="handleLogin">
        <!-- ID -->
        <div>
          <label for="id" class="block text-[11px] text-gray-600 mb-1">ID</label>
          <input id="id" v-model="id" type="text" class="custom-input" />
        </div>

        <!-- Password -->
        <div>
          <label for="password" class="block text-[11px] text-gray-600 mb-1">Password</label>
          <div class="relative">
            <input id="password" :type="showPassword ? 'text' : 'password'" v-model="password" class="custom-input pr-10" />
            <img src="@/assets/eye.png" alt="Toggle visibility" class="eye-icon" @click="togglePassword" />
          </div>
          <p class="text-[10px] italic mt-1 text-center text-gray-500">Forgot password?</p>
        </div>

        <!-- 로그인 버튼 -->
        <div class="flex justify-center">
          <button type="submit" class="signin-button">Sign in</button>
        </div>

        <!-- Remember me -->
        <div class="text-[10px] text-gray-400 text-center">
          <input type="checkbox" id="remember" class="mr-1" />
          <label for="remember">Remember me</label>
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
import axios from "@/api/axios";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const id = ref("");
const password = ref("");
const showPassword = ref(false);
const router = useRouter();
const auth = useAuthStore();

// JWT payload decoder (base64 padding 보정 포함)
function decodePayload(token) {
  const base64 = token.split(".")[1];
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), "=");
  return JSON.parse(atob(padded));
}

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const handleLogin = async () => {
  console.log("🟡 handleLogin 호출됨");
  try {
    // Form 방식으로 보내야 Spring Security가 인식
    const params = new URLSearchParams();
    params.append("username", id.value);
    params.append("password", password.value);

    const res = await axios.post("/login", params, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      withCredentials: true,
    });

    console.log("로그인 응답:", res);

    const accessToken = res.headers["access"] || res.headers["authorization"];
    if (!accessToken) {
      console.error("토큰이 응답 헤더에 없음:", res.headers);
      alert("로그인 처리 중 오류가 발생했습니다.");
      return;
    }

    // Bearer 접두사 제거
    const token = accessToken.replace("Bearer ", "");

    // 토큰에서 정보 추출
    const payload = decodePayload(token);
    const userKey = payload.userKey;
    const userId = id.value; // 입력한 ID 사용

    console.log("✅ 저장할 정보:", { token, userKey, userId });

    // Pinia store에 저장
    auth.setAuth({ accessToken: token, userKey, userId });

    // 메인 페이지 이동
    const redirectUrl = localStorage.getItem("redirectUrl") || "/";
    localStorage.removeItem("redirectUrl"); // 사용 후 삭제
    router.push(redirectUrl);
  } catch (error) {
    console.error("로그인 실패:", error);
    alert("로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.");
  }
};
</script>

<style scoped>
.custom-input {
  width: 100%;
  height: 48px;
  padding: 0 1rem;
  font-size: 1rem;
  border: 1px solid #d1d5db; /* gray-300 */
  border-radius: 12px;
  outline: none;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.custom-input:focus {
  border-color: #9ca3af; /* gray-400 */
}

.signin-button {
  height: 50px;
  width: 100%;
  background-color: black;
  color: white;
  font-size: 1.125rem;
  font-weight: bold;
  padding-left: 1.5rem;
  padding-right: 1.5rem;
  border-radius: 0.75rem;
  transition: background-color 0.2s ease;
  border: none;
}

.signin-button:hover {
  background-color: #1f1f1f;
}

.eye-icon {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  cursor: pointer;
}
</style>
