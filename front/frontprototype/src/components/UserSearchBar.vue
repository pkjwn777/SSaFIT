<template>
  <div class="search-bar-container">
    <div class="search-top">
      <input type="text" v-model="input" placeholder="검색할 유저의 ID를 입력하세요" class="search-input" @keyup.enter="emitSearch" />
      <button @click="emitSearch" class="search-button">Search</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

const emit = defineEmits(["result"]);
const input = ref("");

const emitSearch = async () => {
  if (input.value.trim() !== "") {
    try {
      const res = await axios.get(`/user/searchList/${encodeURIComponent(input.value.trim())}`);
      emit("result", {
        keyword: input.value.trim(),
        users: res.data,
      });
    } catch (err) {
      console.error("검색 실패:", err);
      emit("result", { keyword: input.value.trim(), users: [] });
    }
  }
};
</script>

<style scoped>
.search-bar-container {
  width: 100%;
  max-width: 800px;
  margin: 1rem auto;
  padding: 0 1rem;
}

.search-top {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  width: 100%;
}

.search-input {
  flex: 1;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  border: 1px solid #d1d5db;
  background-color: #f0f9ff;
  font-size: 0.875rem;
  transition: all 0.2s ease;
  min-width: 300px;
}

.search-input:focus {
  outline: none;
  border-color: #000;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-button {
  padding: 0.5rem 1.5rem;
  background-color: black;
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s ease;
  min-width: 100px;
}

.search-button:hover {
  background-color: #333;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-button:active {
  background-color: #000;
  transform: translateY(0);
}
</style>
