<template>
  <div class="min-h-screen flex flex-col bg-white">
    <AppHeader />

    <main class="flex flex-col items-center px-6 lg:px-24 py-8 space-y-12">
      <template v-if="video">
        <Video :video="video" />
        <ReviewList :videoKey="video.videoKey" />
      </template>
      <p v-else class="text-gray-400 mt-10">비디오 정보를 불러오는 중입니다...</p>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useVideoStore } from "@/stores/video"; // ✅ video.js에서 가져오기
import axios from "axios";

import AppHeader from "@/components/AppHeader.vue";
import Video from "@/components/Video.vue";
import ReviewList from "@/components/ReviewList.vue";
import Footer from "@/components/Footer.vue";

const route = useRoute();
const router = useRouter();
const videoStore = useVideoStore(); // ✅ store 인스턴스 사용

const video = ref(null);

onMounted(async () => {
  if (videoStore.selectedVideo) {
    video.value = videoStore.selectedVideo;
  } else {
    // 새로고침 등으로 Pinia state가 날아갔을 경우 fallback 처리
    try {
      const res = await axios.get(`/video/detail/${route.params.videoKey}`);
      video.value = res.data;
      videoStore.setVideo(res.data); // 선택된 video를 다시 store에 복원
    } catch (err) {
      console.error("비디오 불러오기 실패:", err);
      alert("비디오 정보를 불러올 수 없습니다.");
      router.replace("/");
    }
  }
});
</script>
