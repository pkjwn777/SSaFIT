<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h3 class="modal-title">어떤 타입의 비디오를 업로드 하시나요?</h3>
      <div class="button-group">
        <button @click="selectType('custom')" class="type-btn">Custom</button>
        <button @click="selectType('youtube')" class="type-btn">Youtube</button>
      </div>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(["select-type", "close"]);

const selectType = (type) => {
  emit("select-type", type);
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
  text-align: center;
}

.modal-title {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 2rem;
  color: #1a1a1a;
}

.button-group {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.type-btn {
  padding: 0.75rem 2rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #2d3748;
  color: white;
  border: none;
  width: 150px;
}

.type-btn:hover {
  background-color: #1a202c;
}
</style>
