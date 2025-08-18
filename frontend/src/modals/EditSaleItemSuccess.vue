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

      <!-- รูปซ้าย / รายละเอียดขวา -->
      <div class="flex flex-col md:flex-row gap-6">
        <!-- รูปหลัก -->
        <div class="flex-1">
          <img :src="mainImage" alt="main" class="w-full rounded" />
        </div>

        <!-- รายละเอียด -->
        <div class="flex-1 space-y-2 text-sm">
          <div class="itbms-model text-2xl font-bold">{{ updatedItem.model }}</div>
          <div class="itbms-brandName text-gray-600">{{ updatedItem.name }}</div>
          <div class="itbms-description">{{ updatedItem.description }}</div>

          <div class="grid grid-cols-2 gap-4 mt-4">
            <div class="itbms-ramGb"><strong>RAM:</strong> {{ updatedItem.ramGb || '-' }} GB</div>
            <div class="itbms-storageGb"><strong>Storage:</strong> {{ updatedItem.storageGb || '-' }} GB</div>
            <div class="itbms-screenSizeInch"><strong>Screen Size:</strong> {{ updatedItem.screenSizeInch || '-' }} inches</div>
            <div class="itbms-color"><strong>Color:</strong> {{ updatedItem.color || '-' }}</div>
            <div class="itbms-quantity"><strong>Quantity:</strong> {{ updatedItem.quantity }} units</div>
            <div class="itbms-price"><strong>Price:</strong> {{ updatedItem.price.toLocaleString() }} Baht</div>
          </div>
        </div>
      </div>

      <!-- Thumbnail 4 รูป -->
      <div class="flex justify-center gap-4">
        <img :src="mainImage" alt="thumb1" class="w-20 h-20 object-cover rounded" />
        <img :src="mainImage" alt="thumb2" class="w-20 h-20 object-cover rounded" />
        <img :src="mainImage" alt="thumb3" class="w-20 h-20 object-cover rounded" />
        <img :src="mainImage" alt="thumb4" class="w-20 h-20 object-cover rounded" />
      </div>

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
