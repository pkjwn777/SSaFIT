<template>
  <div class="user-list-container">
    <!-- 검색 결과 문구 -->
    <h2 class="search-result-title">"{{ keyword }}" 검색 결과</h2>

    <!-- 사용자 카드 리스트 -->
    <div class="user-list">
      <div v-for="user in users" :key="user.userKey" class="user-card" @click="navigateToUserPage(user.userKey)">
        <div class="user-info">
          <img :src="profileImages[user.userKey]" alt="profile" class="user-avatar" />
          <div class="user-details">
            <p class="user-id">{{ user.userId }}</p>
            <p class="user-description">Body text for whatever you'd like to say. Add main takeaway points, quotes, anecdotes, or even a very very short story.</p>
          </div>
        </div>
        <button
          v-if="user.userKey !== auth.userKey"
          :class="[
            'follow-button',
            {
              'unfollow-button': followStatus[user.userKey],
            },
          ]"
          @click.stop="followUser(user.userKey)"
        >
          {{ followStatus[user.userKey] ? "팔로우 취소" : "팔로우" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();
const props = defineProps({
  users: Array,
  keyword: String,
});

const auth = useAuthStore();
const followStatus = ref({});
const profileImages = ref({});

const fetchProfileImage = async (userKey) => {
  try {
    const response = await axios.get(`/user/profile/${userKey}`);
    profileImages.value[userKey] = response.data;
  } catch (error) {
    console.error(`프로필 이미지 로드 실패 (userKey: ${userKey}):`, error);
    profileImages.value[userKey] = "https://ssafy-ssafit.s3.us-east-1.amazonaws.com/userProfile/main.jpg";
  }
};

const fetchAllProfileImages = async () => {
  if (!props.users) return;

  for (const user of props.users) {
    await fetchProfileImage(user.userKey);
  }
};

const checkFollowStatus = async (userKey) => {
  if (!auth.userKey) return;

  try {
    await axios.get(`/user/${auth.userKey}/isFollow/${userKey}`);
    followStatus.value[userKey] = true;
  } catch (err) {
    if (err.response?.status === 404) {
      followStatus.value[userKey] = false;
    }
  }
};

const checkAllFollowStatus = async () => {
  if (!props.users) return;

  for (const user of props.users) {
    await checkFollowStatus(user.userKey);
  }
};

const followUser = async (followingKey) => {
  const followerKey = auth.userKey;
  if (!followerKey) {
    alert("로그인이 필요합니다.");
    return;
  }

  try {
    if (followStatus.value[followingKey]) {
      // 언팔로우
      await axios.delete(`/user/${followerKey}/unfollow/${followingKey}`);
      followStatus.value[followingKey] = false;
      alert("팔로우가 취소되었습니다.");
    } else {
      // 팔로우
      await axios.get(`/user/${followerKey}/follow/${followingKey}`);
      followStatus.value[followingKey] = true;
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

const navigateToUserPage = (userKey) => {
  if (userKey === auth.userKey) {
    router.push("/mypage");
  } else {
    router.push(`/user/${userKey}`);
  }
};

watch(
  () => props.users,
  async (newUsers) => {
    if (newUsers) {
      await Promise.all([checkAllFollowStatus(), fetchAllProfileImages()]);
    }
  },
  { immediate: true }
);

onMounted(async () => {
  await Promise.all([checkAllFollowStatus(), fetchAllProfileImages()]);
});
</script>

<style scoped>
.user-list-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 1rem;
}

.search-result-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 1.5rem;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  transition: all 0.2s ease;
  cursor: pointer;
}

.user-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transform: translateY(-1px);
  background-color: #f9fafb;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex: 1;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-id {
  font-size: 1.1rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 0.5rem;
}

.user-description {
  font-size: 0.9rem;
  color: #6b7280;
  line-height: 1.4;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.follow-button {
  padding: 0.5rem 1.5rem;
  background-color: #f3f4f6;
  color: #000;
  border: none;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.follow-button:hover {
  background-color: #e5e7eb;
}

.unfollow-button {
  background-color: #000;
  color: white;
}

.unfollow-button:hover {
  background-color: #333;
}
</style>
