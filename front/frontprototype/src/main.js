import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import axios from "axios";

import "./assets/tailwind.css";

// ✅ Axios 기본 설정
axios.defaults.baseURL = "http://localhost:8080";
axios.defaults.withCredentials = true; // refresh 시 쿠키 포함 등

// ✅ 요청 인터셉터: accessToken을 "access" 헤더로 보냄
axios.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem("accessToken");
  if (accessToken) {
    config.headers["access"] = accessToken;
  }
  return config;
});

// ✅ 응답 인터셉터: access token 만료 시 자동으로 refresh 시도
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const path = window.location.pathname;
    const isLoginPage = path === "/login";

    // access token 만료 감지 및 재시도
    if (error.response && error.response.status === 401 && error.response.data === "access token expired" && !originalRequest._retry && !isLoginPage) {
      originalRequest._retry = true;
      try {
        // ✅ refresh 요청에는 refresh 토큰이 자동 쿠키로 보내진다고 가정
        const res = await axios.post("/refresh", {}, { withCredentials: true });
        const newAccessToken = res.data.accessToken;

        // ✅ 새 access 토큰 저장 및 원래 요청에 부착 후 재시도
        localStorage.setItem("accessToken", newAccessToken);
        originalRequest.headers["access"] = newAccessToken;
        return axios(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem("accessToken");
        router.push("/login");
        return Promise.reject(refreshError);
      }
    }

    // 그 외 401 에러 → 로그인 페이지 외면 루트로 보냄
    if (error.response && error.response.status === 401 && !isLoginPage) {
      router.push("/");
    }

    return Promise.reject(error);
  }
);

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount("#app");

export default axios;
