<script setup>
defineEmits(['close'])
import BaseButton from '@/components/BaseButton.vue' // ปรับ path ให้ตรงกับโปรเจกต์คุณ
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white p-6 rounded-lg shadow-md w-full max-w-md text-center">
      <h2 class="itbms-message text-lg font-semibold text-green-700 mb-4">
        The sale item has been deleted.
      </h2>
      <BaseButton
        variant="save"
        class="mt-4"
        @click="$emit('close')"
      >
        OK
      </BaseButton>
    </div>
  </div>
</template>
