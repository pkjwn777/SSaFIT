import { defineStore } from "pinia";
import axios from "@/api/axios";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    accessToken: null,
    userKey: null,
    userId: null,
  }),
  actions: {
    // 로그인 후 토큰과 userKey 저장
    setAuth({ accessToken, userKey, userId }) {
      this.accessToken = accessToken;
      this.userKey = userKey;
      this.userId = userId;
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("userKey", userKey);
      localStorage.setItem("userId", userId);
    },
    // 로그아웃 시 토큰 제거 및 서버 로그아웃 요청
    async clearAuth() {
      try {
        // 서버에 로그아웃 요청
        await axios.post(
          "/logout",
          {},
          {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          }
        );
      } catch (error) {
        console.error("로그아웃 요청 실패:", error);
      } finally {
        // 로컬 상태 초기화
        this.accessToken = null;
        this.userKey = null;
        this.userId = null;
        localStorage.removeItem("accessToken");
        localStorage.removeItem("userKey");
        localStorage.removeItem("userId");
        // 추가 쿠키나 세션 스토리지 정리
        document.cookie.split(";").forEach((cookie) => {
          document.cookie = cookie.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
        });
        sessionStorage.clear();
      }
    },
    // 앱 실행 시 localStorage에서 상태 복원
    loadFromStorage() {
      const token = localStorage.getItem("accessToken");
      const key = localStorage.getItem("userKey");
      const id = localStorage.getItem("userId");

      // 토큰이 있을 경우에만 상태 복원
      if (token && key && id) {
        this.accessToken = token;
        this.userKey = key;
        this.userId = id;
      } else {
        // 토큰이 없으면 완전 초기화
        this.clearAuth();
      }
    },
  },
  getters: {
    isLoggedIn: (state) => !!state.accessToken && !!state.userKey,
  },
});
