<template>
  <div class="daily-log">
    <div class="chart-container">
      <div class="chart-header">
        <button @click="prevDay" class="nav-button">&lt;</button>
        <h3 class="date-text">{{ formatDisplayDate(selectedDate) }}</h3>
        <button @click="nextDay" class="nav-button">&gt;</button>
      </div>

      <div class="chart-wrapper">
        <Doughnut v-if="chartData.datasets[0].data.some((value) => value > 0)" :data="chartData" :options="chartOptions" />
        <div class="calories-center">
          <div v-if="isCurrentUser" class="target-calories">
            <div v-if="!showTargetInput" class="target-display">
              <span>{{ targetCalories }}kcal</span>
              <button type="button" @click="toggleTargetInput" class="set-target-btn">+</button>
            </div>
            <div v-else class="target-input-container">
              <input type="number" min="0" v-model="newTargetCalories" class="target-input" placeholder="목표 칼로리 입력" />
              <button type="button" @click="handleSaveTarget" class="save-btn">저장</button>
              <button type="button" @click="cancelTargetEdit" class="cancel-btn">취소</button>
            </div>
          </div>
          <div class="calories-value">{{ totalCalories }}kcal</div>
          <div v-if="targetCalories > 0" class="calories-percentage">{{ calculateAchievementRate }}%</div>
        </div>
      </div>

      <div class="chart-legend">
        <div v-for="(label, index) in chartData.labels" :key="label" class="legend-item" v-show="chartData.datasets[0].data[index] > 0">
          <span class="legend-color" :style="{ backgroundColor: chartData.datasets[0].backgroundColor[index] }"></span>
          <span class="legend-label">{{ label }}</span>
          <span class="legend-value">{{ calculatePercentage(chartData.datasets[0].data[index]) }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "vue-chartjs";
import axios from "@/api/axios";
import { useAuthStore } from "@/stores/auth";

ChartJS.register(ArcElement, Tooltip, Legend);

const props = defineProps({
  userKey: {
    type: [String, Number],
    required: true,
  },
  isCurrentUser: {
    type: Boolean,
    required: true,
  },
});

const auth = useAuthStore();

// 상태 변수들
const selectedDate = ref(new Date());
const logData = ref(null);
const totalCalories = ref(0);
const targetCalories = ref(0);
const showTargetInput = ref(false);
const newTargetCalories = ref("");

// 차트 데이터
const chartData = ref({
  labels: ["가슴", "어깨", "등", "하체", "팔", "복부", "유산소"],
  datasets: [
    {
      data: [0, 0, 0, 0, 0, 0, 0],
      backgroundColor: [
        "#FF9F40", // 가슴 - 주황
        "#FFD8D0", // 어깨 - 연한 분홍
        "#FFE135", // 등 - 노랑
        "#36A2EB", // 하체 - 파랑
        "#FF6384", // 팔 - 빨강
        "#4BC0C0", // 복근 - 청록
        "#9966FF", // 유산소 - 보라
      ],
      borderWidth: 0,
      cutout: "85%",
      borderRadius: 20,
      spacing: 5,
      hoverOffset: 4,
    },
  ],
});

// 차트 옵션
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      enabled: true,
      callbacks: {
        label: (context) => {
          const value = context.raw;
          const percentage = calculatePercentage(value);
          return `${context.label}: ${percentage}% (${value}kcal)`;
        },
      },
    },
  },
  animation: {
    animateRotate: true,
    animateScale: true,
  },
  layout: {
    padding: 20,
  },
};

// 운동 부위별 칼로리 매핑
const partMapping = {
  CARDIO: 6, // 유산소
  CHEST: 0, // 가슴
  SHOULDER: 1, // 어깨
  BACK: 2, // 등
  LEGS: 3, // 하체
  ARM: 4, // 팔
  CORE: 5, // 복근
};

// 유틸리티 함수들
const formatApiDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const formatDisplayDate = (date) => {
  const year = date.getFullYear().toString().slice(-2);
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}.${month}.${day}`;
};

const calculatePercentage = (value) => {
  if (!totalCalories.value || value === 0) return 0;
  return ((value / totalCalories.value) * 100).toFixed(1);
};

// 목표 칼로리 관련 함수들
const loadTargetCalories = () => {
  console.log("목표 칼로리 로드:", {
    isCurrentUser: props.isCurrentUser,
    userId: auth.userId,
  });

  if (props.isCurrentUser && auth.userId) {
    const saved = localStorage.getItem(`targetCalories_${auth.userId}`);
    targetCalories.value = saved ? Number(saved) : 0;
    console.log("로드된 목표 칼로리:", targetCalories.value);
  }
};

const handleSaveTarget = () => {
  console.log("저장 시도:", {
    newValue: newTargetCalories.value,
    userId: auth.userId,
  });

  const value = Number(newTargetCalories.value);
  if (!value || value < 0) {
    alert("올바른 칼로리 값을 입력해주세요.");
    return;
  }

  if (auth.userId) {
    targetCalories.value = value;
    localStorage.setItem(`targetCalories_${auth.userId}`, value.toString());
    showTargetInput.value = false;
    newTargetCalories.value = "";
    console.log("저장 완료:", value);
  } else {
    console.error("userId가 없음");
    alert("로그인이 필요합니다.");
  }
};

const toggleTargetInput = () => {
  console.log("토글 클릭");
  showTargetInput.value = true;
};

const cancelTargetEdit = () => {
  showTargetInput.value = false;
  newTargetCalories.value = "";
};

// 날짜 이동 함수들
const prevDay = () => {
  const newDate = new Date(selectedDate.value);
  newDate.setDate(newDate.getDate() - 1);
  selectedDate.value = newDate;
};

const nextDay = () => {
  const newDate = new Date(selectedDate.value);
  newDate.setDate(newDate.getDate() + 1);
  selectedDate.value = newDate;
};

// 데이터 가져오기
const fetchDailyLog = async () => {
  try {
    const formattedDate = formatApiDate(selectedDate.value);
    console.log("요청 URL:", `/log/day/${props.userKey}?date=${formattedDate}`);

    const response = await axios.get(`/log/day/${props.userKey}?date=${formattedDate}`);
    console.log("API 응답 데이터:", response.data);

    logData.value = response.data;

    // 부위별 칼로리 배열 초기화
    const partCalories = new Array(7).fill(0);

    // 운동 기록 데이터를 부위별로 합산
    if (Array.isArray(logData.value)) {
      logData.value.forEach((log) => {
        const partName = log.log_FitPartName.toUpperCase();
        const partIndex = partMapping[partName];

        if (partIndex !== undefined) {
          const calories = Number(log.totalKcal);
          if (!isNaN(calories)) {
            partCalories[partIndex] += calories;
          }
        }
      });

      // 총 칼로리 계산
      totalCalories.value = partCalories.reduce((a, b) => a + b, 0);

      // 차트 데이터 업데이트
      chartData.value = {
        ...chartData.value,
        datasets: [
          {
            ...chartData.value.datasets[0],
            data: partCalories,
          },
        ],
      };
    }
  } catch (error) {
    console.error("일간 운동 기록 조회 실패:", error);
    // 에러 시 데이터 초기화
    chartData.value.datasets[0].data = new Array(7).fill(0);
    totalCalories.value = 0;
  }
};

// Computed 속성
const calculateAchievementRate = computed(() => {
  if (targetCalories.value <= 0) return 0;
  return Math.round((totalCalories.value / targetCalories.value) * 100);
});

const isToday = computed(() => {
  const today = new Date();
  return selectedDate.value.toDateString() === today.toDateString();
});

// Watchers
watch(selectedDate, () => {
  fetchDailyLog();
});

watch(
  () => auth.userId,
  (newUserId) => {
    console.log("userId 변경 감지:", newUserId);
    loadTargetCalories();
  },
  { immediate: true }
);

// Lifecycle hooks
onMounted(() => {
  loadTargetCalories();
  fetchDailyLog();
});
</script>

<style scoped>
.daily-log {
  background: white;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.chart-container {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.date-text {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.nav-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #666;
  cursor: pointer;
  padding: 0.5rem;
  transition: color 0.2s;
}

.nav-button:hover {
  color: #000;
}

.chart-wrapper {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: 400px;
  margin: 0 auto;
}

.chart-wrapper canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100% !important;
  height: 100% !important;
}

.calories-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  width: 100%;
  max-width: 200px;
}

.target-calories {
  margin-bottom: 0;
  font-size: 0.9rem;
  color: #666;
  position: absolute;
  top: -30px;
  left: 55%;
  transform: translateX(-50%);
  white-space: nowrap;
}

.target-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.calories-value {
  font-size: 2.2rem;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1;
  margin: 0;
}

.calories-percentage {
  font-size: 1.1rem;
  font-weight: 600;
  color: #4a5568;
  position: absolute;
  bottom: -25px;
  left: 50%;
  transform: translateX(-50%);
}

.chart-legend {
  margin-top: 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-label {
  flex-grow: 1;
  color: #4a5568;
}

.legend-value {
  font-weight: 600;
  color: #1a1a1a;
}

.set-target-btn {
  background: none;
  border: 1px solid #4bc0c0;
  color: #4bc0c0;
  font-size: 1.2rem;
  cursor: pointer;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  transition: all 0.2s;
}

.set-target-btn:hover {
  background: #4bc0c0;
  color: white;
}

.target-input-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  justify-content: center;
}

.target-input {
  width: 100px;
  padding: 0.3rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  text-align: center;
}

.save-btn {
  background: #4bc0c0;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.3rem 0.8rem;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.save-btn:hover {
  background: #3aa9a9;
}

.cancel-btn {
  background: #f3f4f6;
  color: #666;
  border: none;
  border-radius: 4px;
  padding: 0.3rem 0.8rem;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #e5e7eb;
  color: #333;
}
</style>
