<template>
  <div class="search-video-page">
    <!-- 상단 헤더 -->
    <AppHeader />

    <!-- 검색창 -->
    <section class="search-bar-wrapper">
      <VideoSearchBar @search-complete="handleSearchComplete" />
    </section>

    <!-- 검색 결과 -->
    <section class="search-result-wrapper">
      <SearchedList :videos="videos" :keyword="keyword" />
    </section>

    <!-- 하단 푸터 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import Footer from '@/components/Footer.vue'
import VideoSearchBar from '@/components/VideoSearchBar.vue'
import SearchedList from '@/components/SearchedList.vue'

const videos = ref([])
const keyword = ref('')

const handleSearchComplete = ({ results, keyword: searchKeyword }) => {
  videos.value = results
  keyword.value = searchKeyword
}
</script>

<style scoped>
.search-video-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 검색창과 결과 사이 여백 */
.search-bar-wrapper {
  padding-top: 2rem;
  padding-bottom: 1rem;
}

/* 검색 결과 위 여백 */
.search-result-wrapper {
  padding-top: 1rem;
  padding-bottom: 4rem;
  flex-grow: 1;
}
</style>
