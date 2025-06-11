<template>
  <div class="monthly-log">
    <div class="calendar-container">
      <!-- 헤더 영역 -->
      <div class="calendar-header">
        <div class="month-selector">
          <span>{{ formatDisplayMonth }}</span>
          <div class="month-controls">
            <button class="nav-button" @click="prevMonth">&lt;</button>
            <button class="nav-button" @click="nextMonth">&gt;</button>
          </div>
        </div>
      </div>

      <!-- 달력 그리드 -->
      <div class="calendar-grid">
        <!-- 요일 헤더 -->
        <div class="weekday-header">
          <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="weekday">
            {{ day }}
          </div>
        </div>

        <!-- 날짜 그리드 -->
        <div class="dates-grid">
          <div
            v-for="date in calendarDates"
            :key="date.fullDate"
            :class="['date-cell', { 'current-month': date.currentMonth }, { 'has-workout': hasWorkout(date.fullDate) }]"
            @click="showDailyModal(date.fullDate)"
            :style="{ cursor: 'pointer' }"
          >
            {{ date.day }}
          </div>
        </div>
      </div>
    </div>

    <!-- 일일 기록 모달 -->
    <DailyModal :show="showModal" :date="selectedDate" :userKey="userKey" @close="closeModal" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import axios from "@/api/axios";
import DailyModal from "./DailyModal.vue";

const props = defineProps({
  userKey: {
    type: [String, Number],
    required: true,
  },
});

// 한국 시간으로 변환하는 유틸리티 함수
const getKoreaDate = (date = new Date()) => {
  const utc = date.getTime() + date.getTimezoneOffset() * 60 * 1000;
  return new Date(utc + 9 * 60 * 60 * 1000);
};

const currentDate = ref(getKoreaDate());
const workoutDates = ref([]);

// 날짜 포맷 함수
const formatMonth = (date) => {
  const koreaDate = getKoreaDate(date);
  return `${koreaDate.getFullYear()}-${String(koreaDate.getMonth() + 1).padStart(2, "0")}`;
};

// 표시용 월 포맷
const formatDisplayMonth = computed(() => {
  const koreaDate = getKoreaDate(currentDate.value);
  const year = koreaDate.getFullYear();
  const month = koreaDate.getMonth() + 1;
  return `${year}년 ${month}월`;
});

// 달력 데이터 생성
const calendarDates = computed(() => {
  const koreaDate = getKoreaDate(currentDate.value);
  const year = koreaDate.getFullYear();
  const month = koreaDate.getMonth();
  const firstDay = getKoreaDate(new Date(year, month, 1));
  const lastDay = getKoreaDate(new Date(year, month + 1, 0));

  const dates = [];
  const firstDayOfWeek = firstDay.getDay();

  // 이전 달의 날짜들
  for (let i = 0; i < firstDayOfWeek; i++) {
    const date = getKoreaDate(new Date(year, month, -i));
    dates.unshift({
      day: date.getDate(),
      fullDate: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`,
      currentMonth: false,
    });
  }

  // 현재 달의 날짜들
  for (let i = 1; i <= lastDay.getDate(); i++) {
    const date = getKoreaDate(new Date(year, month, i));
    dates.push({
      day: i,
      fullDate: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`,
      currentMonth: true,
    });
  }

  // 다음 달의 날짜들
  const remainingDays = 42 - dates.length; // 6주 달력을 위해
  for (let i = 1; i <= remainingDays; i++) {
    const date = getKoreaDate(new Date(year, month + 1, i));
    dates.push({
      day: date.getDate(),
      fullDate: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`,
      currentMonth: false,
    });
  }

  return dates;
});

// 운동 기록 존재 여부 확인
const hasWorkout = (date) => {
  console.log("검사하는 날짜:", date);
  const result = workoutDates.value.some((workoutDate) => {
    const parsedDate = workoutDate.split(" ")[0];
    console.log("비교:", date, parsedDate, date === parsedDate);
    return date === parsedDate;
  });
  console.log("결과:", result);
  return result;
};

// 월간 운동 기록 조회
const fetchMonthlyWorkouts = async () => {
  try {
    const month = formatMonth(currentDate.value);
    console.log("요청하는 월:", month);
    const response = await axios.get(`/log/month/${props.userKey}?month=${month}`);
    console.log("서버 응답 전체:", response.data);
    workoutDates.value = response.data;
  } catch (error) {
    console.error("월간 운동 기록 조회 실패:", error);
    workoutDates.value = [];
  }
};

// 이전/다음 월 이동
const prevMonth = () => {
  const koreaDate = getKoreaDate(currentDate.value);
  currentDate.value = new Date(koreaDate.getFullYear(), koreaDate.getMonth() - 1);
};

const nextMonth = () => {
  const koreaDate = getKoreaDate(currentDate.value);
  currentDate.value = new Date(koreaDate.getFullYear(), koreaDate.getMonth() + 1);
};

// 월 변경 시 데이터 재조회
watch(currentDate, () => {
  fetchMonthlyWorkouts();
});

// 컴포넌트 마운트 시 초기 데이터 조회
onMounted(() => {
  fetchMonthlyWorkouts();
});

const showModal = ref(false);
const selectedDate = ref("");

const showDailyModal = (date) => {
  selectedDate.value = date;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedDate.value = "";
};
</script>

<style scoped>
.monthly-log {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 1rem;
}

.calendar-container {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  width: 100%;
  max-width: 480px;
}

.calendar-header {
  margin-bottom: 1.5rem;
}

.month-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.2rem;
  font-weight: 600;
}

.month-controls {
  display: flex;
  gap: 0.5rem;
}

.nav-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  font-size: 1.2rem;
  color: #666;
}

.nav-button:hover {
  color: #000;
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 0.5rem;
}

.weekday {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
  padding: 0.5rem 0;
}

.dates-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.date-cell {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  border-radius: 50%;
  cursor: pointer;
  color: #ccc;
  position: relative;
}

.current-month {
  color: #000;
}

.has-workout {
  background-color: #e8f5e9;
  color: #2e7d32 !important;
  font-weight: 600;
}

.date-cell:hover:not(.has-workout) {
  background-color: #f5f5f5;
}
</style>
