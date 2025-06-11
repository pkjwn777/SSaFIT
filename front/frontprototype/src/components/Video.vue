<template>
  <div class="video-container">
    <!-- 🎥 영상 재생 -->
    <div class="video-player">
      <!-- 유튜브 영상일 경우 -->
      <iframe
        v-if="video.videoType === 'Y'"
        class="video-frame"
        :src="youtubeEmbedUrl"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen
      ></iframe>

      <!-- S3 영상일 경우 -->
      <video v-else class="video-frame" controls :src="video.videoLink"></video>
    </div>

    <!-- 📄 비디오 정보 -->
    <div class="video-info">
      <h2 class="video-title">{{ video.videoTitle }}</h2>

      <div class="channel-info">
        <div class="channel-profile">
          <i class="fas fa-user-circle channel-icon"></i>
          <div class="channel-details">
            <div class="channel-name">{{ video.videoChannelName }}</div>
            <div class="follower-count">Follower 999+</div>
          </div>
        </div>
        <div class="video-actions">
          <div class="workout-info">
            <span class="part-name">{{ video.videoFitPartName.toUpperCase() }}</span>
            <span class="kcal">{{ formattedKcal }}kcal</span>
            <span class="view-count">조회수 {{ video.videoViewCnt.toLocaleString() }}</span>
          </div>
          <div class="action-buttons">
            <i class="far fa-bookmark"></i>
            <i class="far fa-star"></i>
          </div>
        </div>
      </div>

      <div class="video-metadata">
        <div class="metadata-text">
          {{ video.videoCreatedAt }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  video: {
    type: Object,
    required: true,
  },
});

const youtubeId = computed(() => {
  const match = props.video.videoLink.match(/(?:v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/);
  return match ? match[1] : "";
});

const youtubeEmbedUrl = computed(() => (youtubeId.value ? `https://www.youtube.com/embed/${youtubeId.value}` : ""));

const formattedKcal = computed(() => {
  return props.video.videoKcal || 0;
});
</script>

<style scoped>
.video-container {
  width: 100%;
  max-width: 1024px;
  margin: 0 auto;
  background-color: transparent;
}

.video-player {
  width: 100%;
  aspect-ratio: 16 / 9;
  background-color: black;
  border-radius: 1rem;
  overflow: hidden;
  margin-bottom: 1.5rem;
}

.video-frame {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-info {
  padding: 0 2rem;
}

.video-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 1.5rem;
}

.channel-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.channel-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.channel-icon {
  font-size: 2.5rem;
  color: #4a5568;
}

.channel-details {
  display: flex;
  flex-direction: column;
}

.channel-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
}

.follower-count {
  font-size: 0.875rem;
  color: #718096;
}

.video-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.75rem;
}

.workout-info {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.part-name,
.kcal,
.view-count {
  font-size: 0.875rem;
  font-weight: 700;
  color: #4a5568;
}

.part-name::after,
.kcal::after {
  content: "•";
  margin-left: 1rem;
  color: #cbd5e0;
}

.action-buttons {
  display: flex;
  gap: 1.25rem;
}

.action-buttons i {
  font-size: 1.25rem;
  color: #4a5568;
  cursor: pointer;
  transition: color 0.2s;
}

.action-buttons i:hover {
  color: #2d3748;
}

.video-metadata {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.metadata-text {
  font-size: 1rem;
  color: #4a5568;
  line-height: 1.5;
}
</style>
