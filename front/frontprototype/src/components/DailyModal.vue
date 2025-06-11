<template>
  <div v-if="show" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>{{ formatDate(date) }} 운동 기록</h2>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>

      <div class="modal-body">
        <!-- 차트 -->
        <div class="chart-wrapper">
          <canvas ref="pieChart"></canvas>
          <div class="calories-center">
            <div class="calories-label">소모 칼로리</div>
            <div class="calories-value">{{ totalCalories }}kcal</div>
          </div>
          <div class="chart-legend">
            <div v-for="(data, label) in exerciseData" :key="label" class="legend-item">
              <span class="legend-color" :style="{ backgroundColor: data.color }"></span>
              <span class="legend-label">{{ label }}</span>
              <span class="legend-value">{{ calculatePercentage(data.value) }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import Chart from "chart.js/auto";
import axios from "@/api/axios";

const props = defineProps({
  show: {
    type: Boolean,
    required: true,
  },
  date: {
    type: String,
    required: true,
  },
  userKey: {
    type: [String, Number],
    required: true,
  },
});

const emit = defineEmits(["close"]);

const pieChart = ref(null);
const chartInstance = ref(null);
const totalCalories = ref(0);
const exerciseData = ref({});
const logData = ref(null);

const chartColors = [
  "#FF6384", // 팔 - 빨강
  "#FFD8D0", // 전신 - 연한 분홍
  "#FFE135", // 등 - 노랑
  "#36A2EB", // 하체 - 파랑
  "#FF9F40", // 가슴 - 주황
  "#4BC0C0", // 복부 - 청록
  "#9966FF", // 유산소 - 보라
];

const partMapping = {
  ARM: 0, // 팔
  TOTAL: 1, // 전신
  BACK: 2, // 등
  LEGS: 3, // 하체
  CHEST: 4, // 가슴
  CORE: 5, // 복부
  CARDIO: 6, // 유산소
};

const partNames = ["팔", "전신", "등", "하체", "가슴", "복부", "유산소"];

const closeModal = () => {
  emit("close");
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
};

const calculatePercentage = (value) => {
  if (!totalCalories.value || value === 0) return 0;
  return ((value / totalCalories.value) * 100).toFixed(1);
};

const fetchDailyData = async () => {
  try {
    const response = await axios.get(`/log/day/${props.userKey}?date=${props.date}`);
    logData.value = response.data;
    console.log("Fetched data:", logData.value);

    // 부위별 칼로리 배열 초기화
    const partCalories = new Array(7).fill(0);

    // 운동 기록 데이터를 부위별로 합산
    if (Array.isArray(logData.value)) {
      logData.value.forEach((log) => {
        // log_FitPartName이 소문자로 올 수 있으므로 대문자로 변환
        const partName = (log.log_FitPartName || "").toUpperCase();
        console.log("Processing log:", partName);

        // CORE와 CARDIO를 위한 특별 처리
        let partIndex;
        if (partName === "CORE" || partName === "CARDIO") {
          partIndex = partMapping[partName];
        } else {
          partIndex = partMapping[partName];
        }

        console.log("Part index:", partIndex, "for part:", partName);

        if (partIndex !== undefined) {
          const calories = Number(log.totalKcal);
          if (!isNaN(calories)) {
            partCalories[partIndex] += calories;
          }
        }
      });

      // 총 칼로리 계산
      totalCalories.value = partCalories.reduce((a, b) => a + b, 0);
      console.log("Part calories array:", partCalories);

      // exerciseData 업데이트
      const tempData = {};
      partNames.forEach((name, index) => {
        if (partCalories[index] > 0) {
          tempData[name] = {
            value: partCalories[index],
            color: chartColors[index],
          };
        }
      });
      exerciseData.value = tempData;
      console.log("Exercise data:", exerciseData.value);

      // 차트 업데이트
      await updateChart(partCalories);
    }
  } catch (error) {
    console.error("일일 운동 기록 조회 실패:", error);
    totalCalories.value = 0;
    exerciseData.value = {};
  }
};

const updateChart = async (partCalories) => {
  try {
    // 기존 차트 제거
    if (chartInstance.value) {
      chartInstance.value.destroy();
    }

    // canvas 컨텍스트 가져오기
    const ctx = pieChart.value.getContext("2d");
    if (!ctx) {
      console.error("Failed to get canvas context");
      return;
    }

    // 0이 아닌 데이터만 필터링
    const nonZeroData = partCalories
      .map((value, index) => ({
        value,
        label: partNames[index],
        color: chartColors[index],
      }))
      .filter((item) => item.value > 0);

    // exerciseData 업데이트 (범례 표시용)
    const tempData = {};
    nonZeroData.forEach((item) => {
      tempData[item.label] = {
        value: item.value,
        color: item.color,
      };
    });
    exerciseData.value = tempData;

    console.log("Creating new chart with data:", {
      labels: nonZeroData.map((item) => item.label),
      data: nonZeroData.map((item) => item.value),
      colors: nonZeroData.map((item) => item.color),
    });

    // 새로운 차트 생성
    chartInstance.value = new Chart(ctx, {
      type: "doughnut",
      data: {
        labels: nonZeroData.map((item) => item.label),
        datasets: [
          {
            data: nonZeroData.map((item) => item.value),
            backgroundColor: nonZeroData.map((item) => item.color),
            borderWidth: 0,
            cutout: "85%",
            borderRadius: 20,
            spacing: 5,
            hoverOffset: 4,
          },
        ],
      },
      options: {
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
      },
    });

    console.log("Chart created successfully");
  } catch (error) {
    console.error("차트 생성 실패:", error);
  }
};

watch(
  () => props.show,
  (newVal) => {
    if (newVal) {
      fetchDailyData();
    }
  }
);

onMounted(() => {
  if (props.show) {
    fetchDailyData();
  }
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
  border-radius: 16px;
  padding: 2rem;
  width: 90%;
  max-width: 600px;
  min-height: 700px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.5rem;
  color: #666;
}

.close-button:hover {
  color: #000;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.chart-wrapper {
  width: 100%;
  max-width: 400px;
  height: 400px;
  margin: 0 auto;
  position: relative;
}

.chart-wrapper canvas {
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

.calories-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.2rem;
  transform: translateY(-1rem);
}

.calories-value {
  font-size: 2.2rem;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1;
  margin: 0;
  transform: translateY(-0.5rem);
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
</style>
