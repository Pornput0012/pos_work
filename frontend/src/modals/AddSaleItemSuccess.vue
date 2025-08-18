<script setup>
import { useRouter } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue' // ปรับ path ให้ตรงกับโปรเจกต์ของคุณ

const emit = defineEmits(['close'])

const router = useRouter()

const goToList = () => {
  emit('close')
  router.push('/sale-items/list')
}
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white p-6 rounded-lg shadow-lg text-center max-w-sm w-full">
      <h2 class="text-xl font-bold mb-4 text-green-700">✅</h2>
      <p class="itbms-message mb-4">The sale item has been successfully added.</p>

      <!-- ใช้ BaseButton แทนปุ่ม -->
      <BaseButton
        variant="save"
        label="กลับไปหน้ารายการขาย"
        @click="goToList"
      />
    </div>
  </div>
</template>
