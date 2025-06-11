<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h3 class="modal-title">YouTube 비디오 업로드</h3>

      <div class="form-group">
        <label>YouTube URL</label>
        <input v-model="form.videoLink" type="text" placeholder="YouTube 영상 URL을 입력하세요" />
        <p class="url-hint">* YouTube 영상 URL을 입력해주세요. (예: https://www.youtube.com/watch?v=xxxx)</p>
      </div>

      <div class="form-group">
        <label>운동 부위</label>
        <select v-model="form.videoFitPartName">
          <option value="">선택하세요</option>
          <option value="팔">팔</option>
          <option value="가슴">가슴</option>
          <option value="복부">복부</option>
          <option value="하체">하체</option>
          <option value="전신">전신</option>
          <option value="유산소">유산소</option>
          <option value="등">등</option>
        </select>
      </div>

      <div class="button-group">
        <button @click="closeModal" class="cancel-btn">취소</button>
        <button @click="submitVideo" class="submit-btn">업로드</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "@/api/axios";

const emit = defineEmits(["close"]);

const form = ref({
  videoLink: "",
  videoFitPartName: "",
  videoType: "Y",
});

const submitVideo = async () => {
  if (!form.value.videoLink.trim()) {
    alert("YouTube URL을 입력해주세요.");
    return;
  }

  if (!form.value.videoFitPartName) {
    alert("운동 부위를 선택해주세요.");
    return;
  }

  try {
    const token = localStorage.getItem("accessToken");
    await axios.post("/youtube/upload", form.value, {
      headers: {
        access: token,
      },
    });
    alert("비디오가 업로드되었습니다.");
    closeModal();
  } catch (err) {
    console.error("비디오 업로드 실패:", err);
    alert("비디오 업로드에 실패했습니다.");
  }
};

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
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 1rem;
  background-color: white;
}

.url-hint {
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #718096;
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
