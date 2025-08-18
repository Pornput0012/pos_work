<script setup>
defineProps({
  name: String
})

import BaseButton from '@/components/BaseButton.vue' // ปรับ path ให้ตรงกับโปรเจกต์ของคุณ
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white p-6 rounded-lg shadow-md w-full max-w-md text-center">
      <h2 class="itbms-message text-lg font-semibold text-gray-800 mb-4">
        Do you want to delete {{ name }} brand?
      </h2>
      <p class="text-sm text-gray-500 mb-6">This action cannot be undone.</p>
      <div class="flex justify-center gap-4">
        <!-- ปุ่ม Delete -->
        <BaseButton
          variant="delete"
          label="Delete"
          customClass="itbms-confirm-button"
          @click="$emit('confirm')"
        />

        <!-- ปุ่ม Cancel -->
        <BaseButton
          variant="cancel"
          label="Cancel"
          customClass="itbms-cancel-button"
          @click="$emit('cancel')"
        />
      </div>
    </div>
  </div>
</template>
