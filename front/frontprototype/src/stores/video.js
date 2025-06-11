// src/stores/video.js
import { defineStore } from "pinia";

export const useVideoStore = defineStore("video", {
  state: () => ({
    selectedVideo: null,
  }),
  actions: {
    setVideo(video) {
      this.selectedVideo = video;
    },
    clearVideo() {
      this.selectedVideo = null;
    },
  },
});
