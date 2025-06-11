import axios from "axios";

const instance = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// 토큰 만료 시 로그인 페이지로 리다이렉트하는 함수
const redirectToLogin = (message = "로그인이 필요합니다.") => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userKey");
  // 현재 페이지 URL을 저장 (로그인 후 돌아오기 위해)
  const currentPath = window.location.pathname;
  if (!currentPath.includes("/login")) {
    localStorage.setItem("redirectUrl", currentPath);
  }
  alert(message);
  window.location.href = "/login";
};

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    const userKey = localStorage.getItem("userKey");

    if (token) {
      // Bearer 토큰 형식으로 전송
      config.headers["Authorization"] = `Bearer ${token}`;
      // 기존 access 헤더도 유지 (호환성)
      config.headers["access"] = token;
    }

    // /user/searchUser/{userKey} 와 같은 요청에서 {userKey} 자동 치환
    if (userKey && config.url && config.url.includes("{userKey}")) {
      config.url = config.url.replace("{userKey}", userKey);
    }

    return config;
  },
  (error) => {
    console.error("요청 인터셉터 에러:", error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터 추가
instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 이미 재시도했던 요청이면 더 이상 재시도하지 않음
    if (originalRequest._retry) {
      console.error("토큰 갱신 후에도 요청 실패:", error);
      redirectToLogin("세션이 만료되었습니다. 다시 로그인해주세요.");
      return Promise.reject(error);
    }

    // 401 Unauthorized 에러 처리
    if (error.response?.status === 401) {
      console.log("인증 오류 발생:", error.response.data);

      // 로그인 페이지가 아닐 경우에만 처리
      if (!window.location.pathname.includes("/login")) {
        try {
          originalRequest._retry = true;

          // 토큰 갱신 시도
          const res = await axios.post(
            "/api/refresh",
            {},
            {
              withCredentials: true,
            }
          );

          if (res.data.accessToken) {
            localStorage.setItem("accessToken", res.data.accessToken);
            originalRequest.headers["Authorization"] = `Bearer ${res.data.accessToken}`;
            originalRequest.headers["access"] = res.data.accessToken;
            return instance(originalRequest);
          }
        } catch (refreshError) {
          console.error("토큰 갱신 실패:", refreshError);
          redirectToLogin("인증이 만료되었습니다. 다시 로그인해주세요.");
          return Promise.reject(refreshError);
        }
      }
    }

    // 403 Forbidden 에러 처리
    if (error.response?.status === 403) {
      redirectToLogin("접근 권한이 없습니다. 다시 로그인해주세요.");
      return Promise.reject(error);
    }

    // 서버 연결 실패 등의 에러 처리
    if (!error.response) {
      console.error("서버 연결 실패:", error.message);
      return Promise.reject(new Error("서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요."));
    }

    return Promise.reject(error);
  }
);

export default instance;
