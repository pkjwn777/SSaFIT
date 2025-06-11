<template>
  <div class="profile-container">
    <div class="profile-header">
      <div class="profile-image-container">
        <img :src="profileImage" alt="프로필 이미지" class="profile-image" />
      </div>
      <div class="profile-info">
        <h2 class="username">{{ user.userId }}</h2>
        <p class="email">{{ user.userEmail }}</p>
        <div class="follow-info">
          <span>팔로워 {{ followerCount }}</span>
          <span>팔로잉 {{ followingCount }}</span>
        </div>
        <div class="action-buttons">
          <button v-if="isCurrentUser" @click="showUpdateModal" class="edit-button">프로필 수정</button>
          <button v-else @click="toggleFollow" :class="['follow-button', { following: isFollowing }]">
            {{ isFollowing ? "팔로잉" : "팔로우" }}
          </button>
        </div>
      </div>
    </div>

    <UserUpdate v-if="showModal" @close="closeModal" @update="handleProfileUpdate" />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axios from "@/api/axios";
import { useAuthStore } from "@/stores/auth";
import UserUpdate from "./UserUpdate.vue";

const props = defineProps({
  user: {
    type: Object,
    required: true,
  },
});

const auth = useAuthStore();
const showModal = ref(false);
const profileImage = ref("https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg");
const followerCount = ref(0);
const followingCount = ref(0);
const isFollowing = ref(false);

const isCurrentUser = computed(() => {
  return auth.userKey === props.user.userKey;
});

const fetchProfileImage = async () => {
  try {
    const res = await axios.get(`/user/profile/${props.user.userKey}`);
    profileImage.value = res.data;
  } catch (err) {
    console.error("프로필 이미지 로드 실패:", err);
  }
};

const showUpdateModal = () => {
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleProfileUpdate = async () => {
  await fetchProfileImage();
  closeModal();
};

const fetchFollowCounts = async () => {
  try {
    const followersRes = await axios.get(`/follow/followerList/${props.user.userKey}`);
    const followingRes = await axios.get(`/follow/followingList/${props.user.userKey}`);
    followerCount.value = followersRes.data.length;
    followingCount.value = followingRes.data.length;
  } catch (err) {
    console.error("팔로우 정보 로드 실패:", err);
  }
};

const checkFollowStatus = async () => {
  if (!isCurrentUser.value) {
    try {
      await axios.get(`/follow/check/${auth.userKey}/${props.user.userKey}`);
      isFollowing.value = true;
    } catch (err) {
      if (err.response?.status === 404) {
        isFollowing.value = false;
      }
    }
  }
};

const toggleFollow = async () => {
  try {
    if (isFollowing.value) {
      await axios.delete(`/follow/unfollow/${auth.userKey}/${props.user.userKey}`);
    } else {
      await axios.post(`/follow/following/${auth.userKey}/${props.user.userKey}`);
    }
    isFollowing.value = !isFollowing.value;
    await fetchFollowCounts();
  } catch (err) {
    console.error("팔로우 상태 변경 실패:", err);
  }
};

onMounted(async () => {
  await Promise.all([fetchProfileImage(), fetchFollowCounts(), checkFollowStatus()]);
});
</script>

<style scoped>
.profile-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  gap: 2rem;
  align-items: center;
  margin-bottom: 2rem;
}

.profile-image-container {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.profile-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info {
  flex: 1;
}

.username {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.email {
  color: #666;
  margin-bottom: 1rem;
}

.follow-info {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.follow-info span {
  font-weight: 500;
}

.edit-button {
  padding: 0.5rem 2rem;
  border-radius: 0.375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #000;
  color: white;
  border: none;
}

.edit-button:hover {
  background-color: #1a1a1a;
}

.action-buttons {
  margin-top: 1rem;
}

.follow-button {
  padding: 0.5rem 2rem;
  border-radius: 0.375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #000;
  color: white;
  border: none;
}

.follow-button.following {
  background-color: #e5e7eb;
  color: #4b5563;
}

.follow-button:hover {
  opacity: 0.9;
}
</style>
