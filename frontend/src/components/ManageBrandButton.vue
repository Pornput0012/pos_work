<template>
  <button
    @click="router.push('/brands')"
    class="itbms-manage-brand bg-green-600 hover:bg-green-700 text-white font-semibold px-4 py-2 rounded shadow"
  >
    Manage Brand
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>
