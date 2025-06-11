<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h3 class="modal-title">리뷰 수정</h3>

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
        <button @click="submitReview" class="submit-btn">수정</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "@/api/axios";

// props로 reviewKey와 initialData 받기
const props = defineProps({
  reviewKey: {
    type: Number,
    required: true,
  },
  initialData: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["close", "update"]);

// 리뷰 데이터 상태 관리
const form = ref({
  reviewTitle: props.initialData.reviewTitle || "",
  reviewContent: props.initialData.reviewContent || "",
});

// 리뷰 수정 제출 함수
const submitReview = async () => {
  try {
    await axios.put(`/review/update/${props.reviewKey}`, {
      reviewTitle: form.value.reviewTitle,
      reviewContent: form.value.reviewContent,
    });

    alert("리뷰가 수정되었습니다.");
    emit("update"); // 부모 컴포넌트에 업데이트 알림
    closeModal();
  } catch (err) {
    console.error("리뷰 수정 실패:", err);
    alert("리뷰 수정에 실패했습니다.");
  }
};

// 모달 닫기 함수
const closeModal = () => {
  emit("close");
};
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
