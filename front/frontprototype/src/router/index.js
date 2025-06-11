import { createRouter, createWebHistory } from "vue-router";
import MainPage from "@/views/MainPage.vue";
import UserPage from "@/views/UserPage.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: MainPage,
    meta: { requiresAuth: false },
  },
  {
    path: "/user/search",
    name: "UserSearch",
    component: () => import("@/views/UserSearch.vue"),
  },
  {
    path: "/mypage",
    name: "MyPage",
    component: () => import("@/views/MyPage.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/video/search",
    name: "SearchVideo",
    component: () => import("@/views/SearchVideo.vue"),
  },
  {
    path: "/signup",
    name: "Signup",
    component: () => import("@/views/Signup.vue"),
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
  },
  {
    path: "/video/:videoKey",
    name: "VideoPlayer",
    component: () => import("@/views/VideoPlayer.vue"),
    props: true,
  },
  {
    path: "/user/:userKey",
    name: "UserPage",
    component: UserPage,
    props: true,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const isAuthenticated = !!localStorage.getItem("accessToken");

  if (to.meta.requiresAuth === true) {
    if (!isAuthenticated) {
      next("/login");
      return;
    }
  }

  next();
});

export default router;
