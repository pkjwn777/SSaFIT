<template>
  <div class="min-h-screen flex flex-col">
    <!-- 상단 헤더 -->
    <AppHeader />

    <!-- 사용자 검색바 -->
    <UserSearchBar @result="handleResult" />

    <!-- 검색 결과 -->
    <div v-if="searched" class="max-w-4xl mx-auto w-full px-4 py-8">
      <UserList :users="users" :keyword="keyword" />
    </div>

    <!-- 하단 푸터 -->
    <Footer class="mt-auto" />
  </div>
</template>

<script setup>
import { ref } from "vue";
import AppHeader from "@/components/AppHeader.vue";
import UserSearchBar from "@/components/UserSearchBar.vue";
import UserList from "@/components/UserList.vue";
import Footer from "@/components/Footer.vue";

const users = ref([]);
const keyword = ref("");
const searched = ref(false);

const handleResult = ({ keyword: searchWord, users: userData }) => {
  keyword.value = searchWord;
  users.value = userData;
  searched.value = true;
};
</script>

<style scoped>
/* 전체 페이지 가운데 정렬 및 여백 조정 */
</style>
