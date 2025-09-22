<template>
  <button
    @click="goToAddItem"
    class="itbms-sale-item-add bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded shadow"
  >
    Add Sale Item
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToAddItem = () => {
  router.push('/sale-items/add')
}
</script>