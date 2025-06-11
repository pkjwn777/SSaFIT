<template>
  <div class="my-8">
    <h2 class="text-xl font-bold mb-4">{{ title }}</h2>

    <div v-if="videos.length > 0" class="carousel-container">
      <button @click="prev" class="carousel-button"><<</button>

      <div class="video-grid">
        <div v-for="video in visibleVideos" :key="video.videoKey" class="video-card" @click="goToVideo(video)">
          <!-- 썸네일 분기: videoType 기반 -->
          <img v-if="video.videoType?.toUpperCase() === 'Y'" :src="getThumbnailUrl(video.videoLink)" alt="YouTube 썸네일" class="thumbnail-image" />
          <img v-else-if="video.videoType?.toUpperCase() === 'S'" :src="getS3ThumbnailUrl(video.videoLink)" alt="S3 썸네일" class="thumbnail-image" />

          <p class="title-text">{{ video.videoTitle }}</p>
          <p class="video-meta">{{ video.videoChannelName }}</p>
          <p class="video-meta">{{ video.videoViewCnt.toLocaleString() }} views</p>
        </div>
      </div>

      <button @click="next" class="carousel-button">>></button>
    </div>
    <div v-else class="no-videos">아직 업로드한 영상이 없습니다.</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import axios from "@/api/axios";
import { useRouter } from "vue-router";
import { useVideoStore } from "@/stores/video";

const props = defineProps({
  orderKey: String,
  title: String,
  userKey: String,
});

const videoStore = useVideoStore();
const router = useRouter();
const videos = ref([]);
const currentIndex = ref(0);

// 화면 크기에 따른 표시할 비디오 수 계산
const getVisibleCount = () => {
  if (window.innerWidth >= 1536) return 6; // 2xl
  if (window.innerWidth >= 1280) return 5; // xl
  if (window.innerWidth >= 1024) return 4; // lg
  if (window.innerWidth >= 768) return 3; // md
  if (window.innerWidth >= 640) return 2; // sm
  return 1; // xs
};

const visibleCount = ref(getVisibleCount());

// 화면 크기 변경 감지
const handleResize = () => {
  visibleCount.value = getVisibleCount();
  // 현재 인덱스가 새로운 표시 개수를 초과하지 않도록 조정
  if (currentIndex.value > videos.value.length - visibleCount.value) {
    currentIndex.value = Math.max(0, videos.value.length - visibleCount.value);
  }
};

const visibleVideos = computed(() => videos.value.slice(currentIndex.value, currentIndex.value + visibleCount.value));

const next = () => {
  if (currentIndex.value + visibleCount.value < videos.value.length) {
    currentIndex.value += visibleCount.value;
  }
};

const prev = () => {
  if (currentIndex.value > 0) {
    currentIndex.value = Math.max(0, currentIndex.value - visibleCount.value);
  }
};

const goToVideo = async (video) => {
  try {
    // 조회수 증가 요청 (응답 대기하지 않음)
    axios.get(`/video/searchOne/${video.videoKey}`).catch(console.error);

    // 비디오 페이지로 이동
    videoStore.setVideo(video);
    router.push({
      name: "VideoPlayer",
      params: { videoKey: video.videoKey },
    });
  } catch (error) {
    console.error("비디오 페이지 이동 실패:", error);
  }
};

// 유튜브 썸네일 URL 생성
const getThumbnailUrl = (link) => {
  if (!link) return "";
  const match = link.match(/(?:v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/);
  const id = match ? match[1] : "";
  return id ? `https://img.youtube.com/vi/${id}/0.jpg` : "";
};

// S3 썸네일 URL 생성
const getS3ThumbnailUrl = (link) => {
  if (!link) return "";
  const baseUrl = link.substring(0, link.lastIndexOf("/") + 1);
  return `${baseUrl}thumbnail/${link.split("/").pop().split(".")[0]}.jpg`;
};

onMounted(() => {
  // 초기 화면 크기에 따른 설정
  handleResize();

  // 화면 크기 변경 이벤트 리스너 등록
  window.addEventListener("resize", handleResize);

  // 비디오 데이터 로드
  fetchVideos();
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});

// 비디오 데이터 로드 함수
const fetchVideos = async () => {
  try {
    let res;
    if (props.userKey) {
      // 특정 사용자의 비디오 목록을 가져오는 경우
      const userKeyInt = parseInt(props.userKey);
      if (isNaN(userKeyInt)) {
        console.error("Invalid userKey:", props.userKey);
        videos.value = [];
        return;
      }
      res = await axios.get(`/video/searchList/${userKeyInt}`);
      videos.value = res.data || [];
    } else {
      // 정렬 기준으로 비디오를 가져오는 경우
      res = await axios.post(`/video/orderBy/${props.orderKey}`);
      videos.value = res.data;
    }
  } catch (error) {
    console.error("🎬 영상 목록 불러오기 실패:", error);
    videos.value = [];
  }
};
</script>

<style scoped>
.carousel-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 1rem 0;
}

@media (min-width: 640px) {
  .carousel-container {
    gap: 1rem;
  }
}

.carousel-button {
  background-color: white;
  border: 1px solid #e5e7eb;
  color: black;
  padding: 0.5rem;
  border-radius: 50%;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
}

@media (min-width: 768px) {
  .carousel-button {
    padding: 0.5rem 0.8rem;
    font-size: 1.25rem;
    width: 3rem;
    height: 3rem;
  }
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 1rem;
  width: 100%;
  margin: 0 auto;
}

/* sm */
@media (min-width: 640px) {
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.25rem;
  }
}

/* md */
@media (min-width: 768px) {
  .video-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 1.5rem;
  }
}

/* lg */
@media (min-width: 1024px) {
  .video-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* xl */
@media (min-width: 1280px) {
  .video-grid {
    grid-template-columns: repeat(5, 1fr);
    max-width: 1440px;
  }
}

/* 2xl */
@media (min-width: 1536px) {
  .video-grid {
    grid-template-columns: repeat(6, 1fr);
    max-width: 1800px;
  }
}

.video-card {
  background-color: #fff;
  border-radius: 0.75rem;
  overflow: hidden;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.12);
  cursor: pointer;
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  transition: transform 0.2s ease;
  height: 100%;
}

.video-card:hover {
  transform: translateY(-6px);
}

.thumbnail-image {
  width: 100%;
  aspect-ratio: 16 / 9;
  object-fit: cover;
  border-radius: 0.5rem;
  margin-bottom: 0.75rem;
}

.title-text {
  font-weight: 600;
  font-size: 1rem;
  line-height: 1.4;
  white-space: normal;
  overflow-wrap: break-word;
  margin: 0.25rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0.1rem 0;
}

.no-videos {
  text-align: center;
  padding: 3rem;
  background-color: #f9fafb;
  border-radius: 0.5rem;
  color: #6b7280;
}
</style>
