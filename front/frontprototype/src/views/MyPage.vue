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
              <h1 class="user-id">@{{ userId }}</h1>
              <div class="user-buttons">
                <button @click="showUpdateModal = true" class="action-button">프로필 수정</button>
                <button @click="showTypeSelectModal" class="action-button">비디오 업로드</button>
              </div>
            </div>
            <div class="follow-stats">
              <span class="stat-item">팔로워 {{ followerCount }}</span>
              <span class="stat-divider">•</span>
              <span class="stat-item">팔로잉 {{ followingCount }}</span>
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
        <div class="records-title-container">
          <h2 class="records-title">@{{ userId }}님의 기록</h2>
          <button class="add-button" @click="showLogGenerateModal = true">+</button>
        </div>
        <div class="records-container">
          <!-- 일간 기록 -->
          <div class="record-box daily-record">
            <h3 class="record-type">일간 기록</h3>
            <DailyLog ref="dailyLogRef" :userKey="auth.userKey" :is-current-user="true" />
          </div>

          <!-- 월간 기록 -->
          <div class="record-box monthly-record">
            <h3 class="record-type">월간 기록</h3>
            <MonthlyLog :userKey="auth.userKey" />
          </div>
        </div>
      </section>
    </main>

    <!-- 타입 선택 모달 -->
    <TypeSelect v-if="showTypeSelect" @select-type="handleTypeSelect" @close="showTypeSelect = false" />
    <!-- 유튜브 업로드 모달 -->
    <YouTubeUpload v-if="showYouTubeUpload" @close="showYouTubeUpload = false" />
    <!-- S3 업로드 모달 -->
    <S3Upload v-if="showS3Upload" @close="showS3Upload = false" />

    <!-- 유저 업데이트 모달 -->
    <UserUpdate
      v-if="showUpdateModal"
      :initial-user-info="{
        userId: userId,
        userEmail: userEmail,
      }"
      @close="showUpdateModal = false"
      @update="handleProfileUpdate"
    />

    <!-- 운동 기록 생성 모달 -->
    <LogGenerate v-if="showLogGenerateModal" @close="showLogGenerateModal = false" @created="handleLogCreated" />

    <!-- 푸터 -->
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import axios from "@/api/axios";
import { useAuthStore } from "@/stores/auth";

import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/Footer.vue";
import TypeSelect from "@/components/TypeSelect.vue";
import YouTubeUpload from "@/components/YouTubeUpload.vue";
import S3Upload from "@/components/S3Upload.vue";
import UserUpdate from "@/components/UserUpdate.vue";
import DailyLog from "@/components/DailyLog.vue";
import LogGenerate from "@/components/LogGenerate.vue";
import MonthlyLog from "@/components/MonthlyLog.vue";

const userId = ref("");
const followerCount = ref(0);
const followingCount = ref(0);
const router = useRouter();
const auth = useAuthStore();
const profileImage = ref("https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg");

// 모달 상태 관리
const showTypeSelect = ref(false);
const showYouTubeUpload = ref(false);
const showS3Upload = ref(false);
const showUpdateModal = ref(false);
const showLogGenerateModal = ref(false);
const userEmail = ref("");

const dailyLogRef = ref(null);

// 타입 선택 모달 표시
const showTypeSelectModal = () => {
  showTypeSelect.value = true;
};

// 타입 선택 처리
const handleTypeSelect = (type) => {
  showTypeSelect.value = false;
  if (type === "youtube") {
    showYouTubeUpload.value = true;
  } else if (type === "custom") {
    showS3Upload.value = true;
  }
};

const handleLogCreated = async () => {
  // DailyLog 컴포넌트 갱신
  if (dailyLogRef.value) {
    await nextTick();
    if (typeof dailyLogRef.value.fetchDailyLog === "function") {
      dailyLogRef.value.fetchDailyLog();
    } else {
      console.warn("fetchDailyLog is not available");
      // 페이지 새로고침으로 대체
      window.location.reload();
    }
  }
};

const fetchProfileImage = async () => {
  try {
    const response = await axios.get(`/user/profile/${auth.userKey}`);
    profileImage.value = response.data;
  } catch (error) {
    console.error("프로필 이미지 로드 실패:", error);
    profileImage.value = "https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg";
  }
};

const handleProfileUpdate = async () => {
  showUpdateModal.value = false;
  await fetchProfileImage(); // 프로필 수정 후 이미지 갱신
};

const fetchFollowCounts = async () => {
  try {
    const [followerRes, followingRes] = await Promise.all([axios.get("/user/follower"), axios.get("/user/following")]);

    followerCount.value = followerRes.data.length;
    followingCount.value = followingRes.data.length;
  } catch (error) {
    console.error("팔로워/팔로잉 정보 로드 실패:", error);
  }
};

onMounted(async () => {
  const userKey = auth.userKey;

  if (!userKey) {
    console.warn("❗ userKey 없음. 로그인 필요");
    router.push("/login");
    return;
  }

  try {
    const res = await axios.get(`/user/searchUser/${userKey}`);
    const { isSelf, user } = res.data;

    if (!isSelf) {
      router.push(`/user/${user.userKey}`);
    } else {
      userId.value = user.userId;
      userEmail.value = user.email;
      await Promise.all([fetchProfileImage(), fetchFollowCounts()]);
    }
  } catch (e) {
    console.error("❌ 사용자 정보 로드 실패:", e);
    router.push("/login");
  }
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

.user-buttons {
  display: flex;
  gap: 0.5rem;
}

.action-button {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  background-color: white;
  font-size: 0.875rem;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button:hover {
  background-color: #f8fafc;
  border-color: #cbd5e0;
}

.follow-stats {
  display: flex;
  align-items: center;
  gap: 0.5rem;
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

.records-section {
  padding: 3rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.records-title-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.records-title {
  font-size: 3rem;
  font-weight: 900;
  margin: 0;
  text-align: center;
}

.add-button {
  background: none;
  border: none;
  color: black;
  font-size: 2rem;
  line-height: 1;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 0.5rem;
}

.add-button:hover {
  opacity: 0.7;
}

.records-container {
  display: flex;
  gap: 2rem;
  width: 100%;
  justify-content: center;
  align-items: stretch;
  position: relative;
}

.records-container::after {
  content: "";
  position: absolute;
  left: 50%;
  top: 4rem;
  bottom: 0;
  width: 3px;
  background-color: #000;
  transform: translateX(-50%);
}

.record-box {
  flex: 1;
  max-width: 480px;
  border-radius: 0.5rem;
  padding: 1.5rem;
  min-height: 300px;
  position: relative;
  z-index: 1;
}

.record-type {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  text-align: center;
}
</style>
