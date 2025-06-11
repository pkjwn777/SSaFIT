<template>
  <div class="page-container">
    <!-- 헤더 -->
    <AppHeader />

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
      <!-- 사용자 프로필 섹션 -->
      <section class="profile-section">
        <div class="profile-container">
          <!-- 왼쪽: 사용자 정보 -->
          <div class="user-info">
            <div class="user-header">
              <h1 class="user-id">@{{ userInfo.userId }}</h1>
            </div>
            <div class="follow-stats">
              <span class="stat-item">팔로워 {{ userInfo.followerCount || 0 }}</span>
              <span class="stat-divider">•</span>
              <span class="stat-item">팔로잉 {{ userInfo.followingCount || 0 }}</span>
              <button v-if="auth.userKey !== props.userKey" :class="['follow-button', { following: isFollowing }]" @click="handleFollow">
                {{ isFollowing ? "팔로우 취소" : "팔로우" }}
              </button>
            </div>
            <div class="user-motto">
              <p class="motto-date">2024년 1월 23일 기준</p>
              <p class="motto-text">"어깨 운동 홀릭"</p>
              <p class="motto-update">17일 후 갱신</p>
            </div>
          </div>

          <!-- 오른쪽: 프로필 이미지 -->
          <div class="profile-image">
            <img :src="profileImage" alt="프로필 이미지" class="profile-img" />
          </div>
        </div>
      </section>

      <!-- 기록 섹션 -->
      <section class="records-section">
        <div class="records-container">
          <!-- 일간 기록 -->
          <div class="record-box daily-record">
            <h3 class="record-type">일간 기록</h3>
            <DailyLog ref="dailyLogRef" :userKey="userKey" :is-current-user="false" />
          </div>
        </div>
      </section>

      <!-- 비디오 섹션 -->
      <section class="video-section">
        <h2 class="section-title">@{{ userInfo.userId }}님의 운동 영상</h2>
        <div class="video-carousel-container">
          <VideoCarousel :userKey="props.userKey" :title="''" />
        </div>
      </section>
    </main>

    <!-- 푸터 -->
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import VideoCarousel from "@/components/VideoCarousel.vue";
import axios from "axios";
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/Footer.vue";
import DailyLog from "@/components/DailyLog.vue";

const props = defineProps({
  userKey: {
    type: String,
    required: true,
  },
});

const route = useRoute();
const auth = useAuthStore();

const userInfo = ref({});
const userVideos = ref([]);
const isFollowing = ref(false);
const profileImage = ref("https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg");

const fetchProfileImage = async () => {
  try {
    const response = await axios.get(`/user/profile/${props.userKey}`);
    profileImage.value = response.data;
  } catch (error) {
    console.error("프로필 이미지 로드 실패:", error);
    profileImage.value = "https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg";
  }
};

const fetchUserInfo = async () => {
  try {
    const res = await axios.get(`/user/searchUser/${props.userKey}`);
    console.log("사용자 정보 응답:", res.data);
    const { user } = res.data;
    userInfo.value = {
      userId: user.userId,
    };
    await fetchProfileImage();
  } catch (err) {
    console.error("사용자 정보 로드 실패:", err);
  }
};

const fetchFollowCounts = async () => {
  try {
    const [followerRes, followingRes] = await Promise.all([axios.get(`/user/follower`), axios.get(`/user/following`)]);

    userInfo.value.followerCount = followerRes.data.length;
    userInfo.value.followingCount = followingRes.data.length;
  } catch (error) {
    console.error("팔로워/팔로잉 정보 로드 실패:", error);
    userInfo.value.followerCount = 0;
    userInfo.value.followingCount = 0;
  }
};

const fetchUserVideos = async () => {
  try {
    const res = await axios.get(`/video/searchList/${props.userKey}`);
    console.log("비디오 목록 응답:", res.data);
    userVideos.value = res.data;
  } catch (err) {
    console.error("비디오 로드 실패:", err);
  }
};

const checkFollowStatus = async () => {
  try {
    await axios.get(`/user/${auth.userKey}/isFollow/${props.userKey}`);
    isFollowing.value = true;
  } catch (err) {
    if (err.response?.status === 404) {
      isFollowing.value = false;
    }
  }
};

const handleFollow = async () => {
  if (!auth.userKey) {
    alert("로그인이 필요합니다.");
    return;
  }

  try {
    if (isFollowing.value) {
      // 언팔로우
      await axios.delete(`/user/${auth.userKey}/unfollow/${props.userKey}`);
      isFollowing.value = false;
      userInfo.value.followerCount--;
      alert("팔로우가 취소되었습니다.");
    } else {
      // 팔로우
      await axios.get(`/user/${auth.userKey}/follow/${props.userKey}`);
      isFollowing.value = true;
      userInfo.value.followerCount++;
      alert("팔로우 완료!");
    }
  } catch (err) {
    console.error("팔로우/언팔로우 실패:", err);
    if (err.response?.status === 400) {
      alert(err.response.data);
    } else {
      alert("작업 실패");
    }
  }
};

onMounted(async () => {
  await Promise.all([fetchUserInfo(), fetchUserVideos(), checkFollowStatus(), fetchFollowCounts()]);
});
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: white;
}

.main-content {
  flex: 1;
  max-width: 1024px;
  margin: 0 auto;
  width: 100%;
  padding: 0 2rem;
}

.profile-section {
  margin-top: 3rem;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 3rem;
}

.profile-container {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  gap: 4rem;
  min-height: 360px;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 0.2rem;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 0;
  width: fit-content;
}

.user-id {
  font-size: 2rem;
  font-weight: 900;
  color: #000;
  margin: 0;
}

.follow-stats {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  font-size: 0.9rem;
  color: #4a5568;
  font-weight: 500;
}

.stat-divider {
  color: #cbd5e0;
  margin: 0 0.25rem;
}

.follow-button {
  padding: 0.5rem 1.5rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #f3f4f6;
  color: #000;
  border: none;
}

.follow-button.following {
  background-color: #000;
  color: white;
}

.follow-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-motto {
  margin-top: 0.2rem;
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 2.5px solid #000;
  border-radius: 50%;
  padding: 2rem;
  text-align: center;
  background-color: white;
  width: 360px;
  aspect-ratio: 1.8 / 1;
  transform: scale(1, 0.7);
}

.motto-date,
.motto-text,
.motto-update {
  transform: scale(1, 1.43); /* 텍스트 비율 복원 */
}

.motto-date {
  font-size: 1rem;
  color: #333;
  margin-bottom: 0.3rem;
  font-weight: 500;
}

.motto-text {
  font-size: 2.4rem;
  font-weight: 900;
  margin: 0.3rem 0;
  color: #000;
  white-space: nowrap;
}

.motto-update {
  font-size: 0.9rem;
  color: #000;
  margin-top: 0.3rem;
  font-weight: 700;
}

.profile-image {
  width: 360px;
  height: 360px;
  border-radius: 0.5rem;
  overflow: hidden;
  background-color: #f7fafc;
  flex-shrink: 0;
}

.profile-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-section {
  padding: 3rem 0;
  width: 100%;
}

.section-title {
  font-size: 2rem;
  font-weight: 900;
  margin-bottom: 2rem;
  text-align: center;
}

.video-carousel-container {
  width: 100%;
  margin: 0 auto;
}

.no-videos {
  text-align: center;
  padding: 3rem;
  background-color: #f9fafb;
  border-radius: 0.5rem;
  color: #6b7280;
}
</style>
