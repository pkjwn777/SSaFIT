<template>
  <div class="search-bar-container">
    <div class="search-top">
      <input type="text" v-model="keyword" placeholder="검색어를 입력하세요" class="search-input" />
      <button @click="onSearch" class="search-button">Search</button>
      <button @click="toggleOptions" class="option-button">Option</button>
    </div>

    <div class="search-options">
      <button v-for="(label, key) in searchFields" :key="key" @click="selectedField = key" :class="['option-item', selectedField === key ? 'active' : '']">
        {{ label }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "@/main.js";

const emit = defineEmits(["search-complete"]);

const keyword = ref("");
const selectedField = ref("videoTitle");

const searchFields = {
  videoTitle: "제목",
  videoChannelName: "채널명",
  videoFitPartName: "부위",
};

const onSearch = async () => {
  const video = {
    videoTitle: null,
    videoChannelName: null,
    videoFitPartName: null,
    videoKey: 0,
    userKey: 0,
    videoLink: null,
    videoViewCnt: 0,
    videoType: null,
    reviewList: [],
  };

  video[selectedField.value] = keyword.value;

  try {
    const res = await axios.post("/video/searchList", video);
    emit("search-complete", {
      results: res.data,
      keyword: keyword.value,
    });
  } catch (err) {
    console.error("검색 실패:", err);
  }
};

const toggleOptions = () => {
  alert("옵션 기능은 준비 중입니다");
};
</script>

<style scoped>
.search-bar-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1.5rem;
}
.search-top {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}
.search-input {
  flex: 1;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  border: 1px solid #d1d5db;
  background-color: #f0f9ff;
}
.search-button,
.option-button {
  padding: 0.5rem 1rem;
  background-color: black;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
}
.search-options {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}
.option-item {
  padding: 0.3rem 0.9rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  background-color: white;
  color: black;
  cursor: pointer;
}
.option-item.active {
  background-color: black;
  color: white;
}
</style>
