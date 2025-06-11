<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h3 class="modal-title">리뷰 작성</h3>

      <!-- 리뷰 제목 -->
      <div class="form-group">
        <label>리뷰 제목</label>
        <input v-model="form.reviewTitle" type="text" placeholder="리뷰 제목을 입력하세요" />
      </div>

      <!-- 리뷰 내용 -->
      <div class="form-group">
        <label>리뷰 내용</label>
        <textarea v-model="form.reviewContent" rows="4" placeholder="리뷰 내용을 입력하세요"></textarea>
      </div>

      <div class="button-group">
        <button @click="closeModal" class="cancel-btn">취소</button>
        <button @click="submitReview" class="submit-btn">제출</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

// props로 videoKey 받기
const props = defineProps({
  videoKey: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(["close"]);

// 리뷰 데이터 상태 관리
const form = ref({
  reviewTitle: "",
  reviewContent: "",
  writer: "", // 로그인한 사용자 ID로 고정
});

// 사용자 정보 가져오기
const fetchUser = () => {
  const token = localStorage.getItem("access");
  if (token) {
    const payload = JSON.parse(atob(token.split(".")[1]));
    form.value.writer = payload.sub || payload.username; // 로그인한 사용자의 userId
  }
};

// 리뷰 제출 함수
const submitReview = async () => {
  try {
    const reviewData = {
      ...form.value,
      videoKey: props.videoKey,
    };

    await axios.post(`/review/insertReview/${props.videoKey}`, reviewData);
    alert("리뷰가 작성되었습니다.");
    closeModal(); // 모달 닫기
  } catch (err) {
    console.error("리뷰 작성 실패:", err);
    alert("리뷰 작성에 실패했습니다.");
  }
};

// 모달 닫기 함수
const closeModal = () => {
  emit("close"); // 부모 컴포넌트에서 `close` 이벤트 처리
};

// 페이지 로드 시 사용자 정보 가져오기
fetchUser();
</script>

<style scoped>
.modal-overlay {
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

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #1a1a1a;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #4a5568;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 1rem;
  background-color: white;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-btn,
.submit-btn {
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background-color: #e2e8f0;
  color: #4a5568;
  border: none;
}

.submit-btn {
  background-color: #4299e1;
  color: white;
  border: none;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.submit-btn:hover {
  background-color: #3182ce;
}
</style>
