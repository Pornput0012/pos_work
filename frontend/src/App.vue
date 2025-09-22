<template>
  <div class="min-h-screen bg-gray-500 text-gray-900">
    <!-- ใช้คอมโพเนนต์ Navbar -->
    <Navbar />

    <!-- Router View -->
    <main class="p-4">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Navbar from './components/NavBar.vue'
</script>

<style scoped></style>
