<script setup>
defineProps(['message'])
defineEmits(['close'])
import BaseButton from '@/components/BaseButton.vue'  // ปรับ path ให้ตรงกับโปรเจกต์คุณ
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
      <p class="text-gray-800 mb-4">{{ message }}</p>
      <BaseButton
        variant="primary"
        class="w-full"
        @click="$emit('close')"
      >
        OK
      </BaseButton>
    </div>
  </div>
</template>
