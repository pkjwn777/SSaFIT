<template>
  <div class="search-result-container">
    <!-- 검색어 문구 -->
    <h2 class="search-title">"{{ keyword }}" 검색 결과</h2>

    <!-- 비디오 카드 목록 -->
    <div v-if="videos.length > 0" class="video-grid">
      <div v-for="video in videos" :key="video.videoKey" class="video-card" @click="goToVideo(video)">
        <img v-if="video.videoType?.toUpperCase() === 'Y'" :src="getThumbnailUrl(video.videoLink)" alt="thumbnail" class="thumbnail" />
        <img v-else-if="video.videoType?.toUpperCase() === 'S'" :src="getS3ThumbnailUrl(video.videoLink)" alt="thumbnail" class="thumbnail" />
        <img v-else src="@/assets/User.jpg" alt="thumbnail" class="thumbnail" />
        <div class="video-info">
          <p class="video-title">{{ video.videoTitle }}</p>
          <p class="video-meta">{{ video.videoChannelName }}</p>
          <p class="video-meta">{{ video.videoViewCnt.toLocaleString() }} views</p>
        </div>
      </div>
    </div>

    <div v-else class="no-result">검색 결과가 없습니다.</div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useVideoStore } from "@/stores/video";

const props = defineProps({
  videos: {
    type: Array,
    required: true,
  },
  keyword: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const videoStore = useVideoStore();

const getThumbnailUrl = (videoLink) => {
  if (!videoLink) return "";
  const match = videoLink.match(/(?:v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/);
  const videoId = match ? match[1] : "";
  return videoId ? `https://img.youtube.com/vi/${videoId}/0.jpg` : "";
};

const getS3ThumbnailUrl = (link) => {
  if (!link) return "";
  const baseUrl = link.substring(0, link.lastIndexOf("/") + 1);
  return `${baseUrl}thumbnail/${link.split("/").pop().split(".")[0]}.jpg`;
};

const goToVideo = (video) => {
  videoStore.setVideo(video);
  router.push({
    name: "VideoPlayer",
    params: { videoKey: video.videoKey },
  });
};
</script>

<style scoped>
.search-result-container {
  max-width: 1200px;
  margin: 2.5rem auto;
  padding: 0 1.5rem;
}

.search-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.video-card {
  background-color: white;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s ease;
  cursor: pointer;
}

.video-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.thumbnail {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.video-info {
  padding: 0.75rem;
}

.video-title {
  font-size: 0.9rem;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0.25rem;
}

.video-meta {
  font-size: 0.75rem;
  color: #6b7280;
  margin: 0.1rem 0;
}

.no-result {
  text-align: center;
  color: #9ca3af;
  margin-top: 5rem;
}
</style>
