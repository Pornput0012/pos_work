<!-- DeleteBrandError.vue -->
<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full text-center">
      <p class="itbms-message text-red-700 font-semibold mb-4">
        Delete {{ name }} is not allowed.<br>
        There are sale items with {{ name }} brand.
      </p>
      <BaseButton
        class="itbms-cancel-button bg-blue-600 hover:bg-blue-700 text-white w-full"
        @click="$emit('close')"
      >
        Cancel
      </BaseButton>
    </div>
  </div>
</template>

<script setup>
defineProps(['name'])
defineEmits(['close'])
import BaseButton from '@/components/BaseButton.vue'
</script>
