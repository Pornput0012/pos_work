<template>
  <div class="min-h-screen bg-gray-500 flex items-center justify-center p-4 overflow-hidden">
    <div
      class="max-w-7xl w-full flex flex-col lg:flex-row items-center lg:items-stretch bg-white rounded-xl shadow-lg overflow-hidden relative"
    >
      <!-- ภาพสไลด์ -->
      <div class="w-full lg:w-1/2 h-[450px] relative overflow-hidden bg-white">
        <img
          :src="currentImage"
          class="object-contain w-full h-full transition-opacity duration-1000 ease-in-out rounded-t-xl lg:rounded-t-none lg:rounded-l-xl"
          alt="Sale item"
        />

        <!-- ปุ่มเลื่อนซ้าย -->
        <button
          @click="prevImage"
          class="absolute top-1/2 left-3 transform -translate-y-1/2 text-4xl font-bold text-blue-950 bg-white/80 hover:bg-white rounded-full w-12 h-12 flex items-center justify-center shadow transition"
        >
          ‹
        </button>

        <!-- ปุ่มเลื่อนขวา -->
        <button
          @click="nextImage"
          class="absolute top-1/2 right-3 transform -translate-y-1/2 text-4xl font-bold text-blue-950 bg-white/80 hover:bg-white rounded-full w-12 h-12 flex items-center justify-center shadow transition"
        >
          ›
        </button>
      </div>

      <!-- ข้อความ + ปุ่ม -->
      <div
        class="w-full lg:w-1/2 p-8 flex flex-col justify-center items-center text-center lg:text-left lg:items-start bg-blue-950 text-white"
      >
        <h1 class="text-4xl font-bold mb-4">Welcome to ITB MShop</h1>
        <p class="mb-6 text-lg">Discover exclusive mobile deals and premium phones.</p>
        <router-link
          to="/sale-items"
          class="bg-white text-blue-950 font-semibold px-6 py-3 rounded-xl shadow-md hover:bg-gray-200 transition duration-300"
        >
          SHOP NOW
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import iPhone15Black from '@/assets/iPhone_15_Black.png'
import iPhone15Blue from '@/assets/iPhone_15_Blue.png'
import iPhone15Green from '@/assets/iPhone_15_Green.png'
import iPhone15Pink from '@/assets/iPhone_15_Pink.png'
import iPhone15Yellow from '@/assets/iPhone_15_Yellow.png'

const images = [
  iPhone15Black,
  iPhone15Blue,
  iPhone15Green,
  iPhone15Pink,
  iPhone15Yellow,
]

const currentImage = ref(images[0])
let index = 0
let intervalId = null

function showImage(i) {
  index = (i + images.length) % images.length
  currentImage.value = images[index]
}

function nextImage() {
  showImage(index + 1)
}

function prevImage() {
  showImage(index - 1)
}

onMounted(() => {
  intervalId = setInterval(() => {
    nextImage()
  }, 3000)
})

onBeforeUnmount(() => {
  clearInterval(intervalId)
})
</script>

<style scoped></style>
