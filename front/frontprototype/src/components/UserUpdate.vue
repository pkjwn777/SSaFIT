<template>
  <div class="modal-overlay" @click="handleClose">
    <div class="modal-content" @click.stop>
      <h3 class="modal-title">프로필 수정</h3>

      <div class="form-group">
        <label>프로필 이미지</label>
        <div class="profile-image-container">
          <img :src="previewImage || currentProfileImage" alt="프로필 이미지" class="profile-preview" />
          <input type="file" @change="handleImageChange" accept="image/jpeg,image/png" class="file-input" ref="fileInput" />
        </div>
      </div>

      <div class="form-group">
        <label>이메일</label>
        <input v-model="userForm.userEmail" type="email" placeholder="이메일을 입력하세요" class="form-input" />
      </div>

      <div class="button-group">
        <button @click.stop="handleClose" class="cancel-button">취소</button>
        <button @click.stop="handleSubmit" class="submit-button">수정</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "@/api/axios";
import { useAuthStore } from "@/stores/auth";

const props = defineProps({
  initialUserInfo: {
    type: Object,
    required: true,
  },
});

const auth = useAuthStore();
const emit = defineEmits(["close", "update"]);

const userForm = ref({
  userEmail: props.initialUserInfo?.userEmail || "",
  userId: props.initialUserInfo?.userId || "",
  userKey: auth.userKey,
});

const fileInput = ref(null);
const selectedImage = ref(null);
const previewImage = ref(null);
const currentProfileImage = ref("https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg");

const handleImageChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert("이미지 크기는 5MB를 초과할 수 없습니다.");
      if (fileInput.value) {
        fileInput.value.value = "";
      }
      return;
    }
    selectedImage.value = file;
    previewImage.value = URL.createObjectURL(file);
  }
};

const handleClose = () => {
  if (previewImage.value) {
    URL.revokeObjectURL(previewImage.value);
  }
  emit("close");
};

const handleSubmit = async () => {
  try {
    const formData = new FormData();

    // User 객체를 JSON Blob으로 변환하여 추가
    formData.append(
      "user",
      new Blob(
        [
          JSON.stringify({
            userEmail: userForm.value.userEmail,
            userId: userForm.value.userId,
            userKey: userForm.value.userKey,
          }),
        ],
        {
          type: "application/json",
        }
      )
    );

    // 이미지가 선택된 경우에만 추가
    if (selectedImage.value) {
      formData.append("profileImage", selectedImage.value);
    }

    const response = await axios.put(`/user/update/${auth.userKey}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    if (response.data === "수정 완료") {
      alert("프로필이 수정되었습니다.");
      emit("update");
      handleClose();
    }
  } catch (error) {
    console.error("프로필 수정 실패:", error);
    alert(error.response?.data || "프로필 수정에 실패했습니다.");
  }
};

const fetchProfileImage = async () => {
  try {
    const response = await axios.get(`/user/profile/${auth.userKey}`);
    currentProfileImage.value = response.data;
  } catch (err) {
    console.error("프로필 이미지 로드 실패:", err);
  }
};

onMounted(() => {
  fetchProfileImage();
});
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
  border-radius: 0.5rem;
  width: 100%;
  max-width: 500px;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.profile-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.profile-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.file-input {
  width: 100%;
  max-width: 300px;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
  font-size: 1rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-button,
.submit-button {
  flex: 1;
  padding: 0.75rem;
  border-radius: 0.375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-button {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #e5e7eb;
}

.submit-button {
  background-color: #000;
  color: white;
  border: none;
}

.cancel-button:hover {
  background-color: #e5e7eb;
}

.submit-button:hover {
  background-color: #1a1a1a;
}
</style>
