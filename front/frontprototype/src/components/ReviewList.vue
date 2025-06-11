<template>
  <div class="review-container">
    <div class="review-header">
      <h2 class="review-title">REVIEWS</h2>
      <button @click="showReviewInsertModal" class="add-review-btn">리뷰 쓰기</button>
    </div>
    <p class="review-count">{{ reviews.length }}개</p>

    <!-- Review Cards -->
    <div class="review-list">
      <div v-for="review in reviews" :key="review.reviewKey" class="review-card">
        <!-- 사용자 이미지 -->
        <div class="user-image">
          <i class="fas fa-user text-2xl"></i>
        </div>

        <!-- 리뷰 내용 -->
        <div class="review-content">
          <p class="review-writer font-bold">{{ review.reviewWriter }}</p>
          <h4 class="review-title-text">{{ review.reviewTitle }}</h4>
          <p class="review-text">{{ review.reviewContent }}</p>
        </div>

        <!-- 수정/삭제 버튼 -->
        <div v-if="review.reviewWriter === username" class="review-actions">
          <button @click="deleteReview(review.reviewKey)" class="delete-btn">삭제</button>
          <button @click="goToUpdate(review)" class="edit-btn">수정</button>
        </div>
      </div>
    </div>

    <!-- 리뷰 작성 모달 -->
    <ReviewInsert v-if="showModal" @close="closeModal" :videoKey="videoKey" />

    <!-- 리뷰 수정 모달 -->
    <ReviewUpdate v-if="showUpdateModal" :reviewKey="selectedReview?.reviewKey" :initialData="selectedReview" @close="showUpdateModal = false" @update="handleUpdate" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import axios from "@/api/axios";
import ReviewInsert from "@/components/ReviewInsert.vue";
import ReviewUpdate from "@/components/ReviewUpdate.vue";

const props = defineProps({
  videoKey: {
    type: Number,
    required: true,
  },
});

const reviews = ref([]);
const username = ref("");
const showModal = ref(false);
const showUpdateModal = ref(false);
const selectedReview = ref(null);

// 리뷰 목록 불러오기
const fetchReviews = async () => {
  try {
    console.log("Fetching reviews for videoKey:", props.videoKey);
    const token = localStorage.getItem("accessToken");
    console.log("Using access token:", token);

    const res = await axios.get(`/review/videoReviews/${props.videoKey}`);
    console.log("Review response:", res.data);
    reviews.value = res.data;
  } catch (err) {
    console.error("리뷰 가져오기 실패:", err);
    if (err.response) {
      console.log("Error status:", err.response.status);
      console.log("Error data:", err.response.data);
      console.log("Request headers:", err.config.headers);
    }
  }
};

// videoKey가 변경될 때마다 리뷰 목록 새로고침
watch(
  () => props.videoKey,
  (newVal) => {
    console.log("VideoKey changed:", newVal);
    if (newVal) {
      fetchReviews();
    }
  },
  { immediate: true }
);

// 로그인한 사용자 이름 가져오기
const fetchUser = () => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    const payload = JSON.parse(atob(token.split(".")[1]));
    username.value = payload.sub || payload.username;
  }
};

// 리뷰 삭제
const deleteReview = async (reviewKey) => {
  try {
    await axios.delete(`/review/delete/${reviewKey}`);
    reviews.value = reviews.value.filter((r) => r.reviewKey !== reviewKey);
  } catch (err) {
    console.error("삭제 실패:", err);
  }
};

// 리뷰 수정 모달 열기
const goToUpdate = (review) => {
  selectedReview.value = review;
  showUpdateModal.value = true;
};

// 리뷰 수정 후 처리
const handleUpdate = () => {
  fetchReviews(); // 리뷰 목록 새로고침
};

// 리뷰 작성 모달 열기
const showReviewInsertModal = () => {
  showModal.value = true;
};

// 모달 닫기
const closeModal = () => {
  showModal.value = false;
  fetchReviews(); // 리뷰 목록 새로고침
};

// 초기화
onMounted(() => {
  fetchUser();
  fetchReviews();
});
</script>

<style scoped>
.review-container {
  margin: 2rem auto;
  position: relative;
  width: 100%;
  max-width: 800px;
  padding: 0 1rem;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.3rem;
  width: 100%;
}

.review-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
  flex-shrink: 0;
}

.review-count {
  font-size: 1rem;
  color: #6b7280;
  margin: 0;
  text-align: left;
  width: 100%;
}

.add-review-btn {
  background-color: #000000;
  color: white;
  font-size: 0.875rem;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  cursor: pointer;
  white-space: nowrap;
  min-width: 100px;
  margin-left: 1rem;
  flex-shrink: 0;
}

.add-review-btn:hover {
  background-color: #1f1f1f;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
  margin-top: 2rem;
}

.review-card {
  width: 100%;
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.user-image {
  width: 4rem;
  height: 4rem;
  min-width: 4rem;
  background-color: #f3f4f6;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  flex-shrink: 0;
}

.review-content {
  flex: 1;
  min-width: 0;
  overflow-wrap: break-word;
  word-wrap: break-word;
}

.review-writer {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.review-title-text {
  font-size: 1.1rem;
  font-weight: 500;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.review-text {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.review-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex-shrink: 0;
}

.delete-btn,
.edit-btn {
  background-color: transparent;
  border: none;
  color: #6b7280;
  font-size: 0.875rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
}

.delete-btn:hover {
  color: #ef4444;
}

.edit-btn:hover {
  color: #3b82f6;
}

/* 모달 스타일 */
:deep(.modal-overlay) {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

:deep(.modal-content) {
  background-color: white;
  padding: 2rem;
  border-radius: 0.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1001;
}
</style>
