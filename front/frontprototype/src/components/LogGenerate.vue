<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h3 class="modal-title">운동 기록 작성</h3>

      <form @submit.prevent="handleSubmit" class="form-content">
        <!-- 운동 부위 선택 -->
        <div class="form-group">
          <label>운동 부위</label>
          <select v-model="form.part" class="form-select">
            <option value="ARM">팔</option>
            <option value="BACK">등</option>
            <option value="CHEST">가슴</option>
            <option value="CORE">복근</option>
            <option value="SHOULDER">어깨</option>
            <option value="LEGS">하체</option>
            <option value="CARDIO">유산소</option>
          </select>
        </div>

        <!-- 소모 칼로리 -->
        <div class="form-group">
          <label>소모 칼로리 (kcal)</label>
          <input v-model.number="form.calories" type="number" class="form-input" min="0" required placeholder="예: 150" />
        </div>

        <!-- 운동 시간 -->
        <div class="form-group">
          <label>운동 시간 (분)</label>
          <input v-model.number="form.time" type="number" class="form-input" min="1" required placeholder="예: 30" />
        </div>

        <!-- 버튼 그룹 -->
        <div class="button-group">
          <button type="button" @click="$emit('close')" class="cancel-btn">취소</button>
          <button type="submit" class="submit-btn">등록</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "@/api/axios";
import { useAuthStore } from "@/stores/auth";

const emit = defineEmits(["close", "created"]);
const auth = useAuthStore();

const form = ref({
  part: "ARM",
  time: null,
  calories: null,
});

const handleSubmit = async () => {
  try {
    console.log("폼 데이터:", form.value);

    // 입력값 검증
    if (!form.value.part || !form.value.time || !form.value.calories) {
      alert("모든 필드를 입력해주세요.");
      return;
    }

    // 요청 데이터
    const requestData = {
      user_key: auth.userKey,
      logFitPartName: String(form.value.part),
      logWorkOutTime: Number(form.value.time),
      logKcal: Number(form.value.calories),
    };

    console.log("서버로 보내는 데이터:", requestData);

    const response = await axios.post("/log/insert", requestData);
    console.log("운동 기록 생성 성공:", response.data);

    alert("운동 기록이 성공적으로 저장되었습니다.");
    emit("close");
    emit("created");
  } catch (error) {
    console.error("운동 기록 생성 실패:", error);
    if (error.response) {
      console.log("에러 상태:", error.response.status);
      console.log("에러 데이터:", error.response.data);
    }
    alert("운동 기록 생성에 실패했습니다. 입력값을 확인해주세요.");
  }
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
  max-width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #1a1a1a;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #4a5568;
}

.form-input,
.form-select {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 1rem;
  width: 100%;
  box-sizing: border-box;
  height: 45px;
  background-color: white;
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1em;
  padding-right: 2.5rem;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #4a5568;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.cancel-btn,
.submit-btn {
  padding: 0.75rem 1.5rem;
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
  background-color: #000;
  color: white;
  border: none;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.submit-btn:hover {
  opacity: 0.9;
}
</style>
