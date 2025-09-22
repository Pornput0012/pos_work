<script setup>
import mainImage from '@/assets/Test.jpg' // ตรวจสอบ path ให้ถูกต้อง
import BaseButton from '@/components/BaseButton.vue' // ปรับ path ตามโปรเจกต์

defineProps({
  updatedItem: Object
})

defineEmits(['close'])
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white p-6 rounded-lg shadow-lg text-left max-w-3xl w-full space-y-6">
      <h2 class="itbms-message text-xl font-bold text-green-700">The sale item has been updated.</h2>

      <!-- ปุ่มกลับ -->
      <div class="flex justify-center">
        <BaseButton
          variant="save"
          class="itbms-home-button mt-4 px-6 py-5 rounded-xl"
          @click="$emit('close')"
        >
          กลับสู่หน้ารายการสินค้า
        </BaseButton>
      </div>
    </div>
  </div>
</template>
