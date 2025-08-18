<script setup>
import mainImage from '@/assets/Test.jpg' // ปรับ path ให้ถูกต้อง
import BaseButton from '@/components/BaseButton.vue'

defineProps({
  item: Object
})

const emit = defineEmits(['edit', 'delete'])

function onEdit() {
  emit('edit')
}

function onDelete() {
  emit('delete')
}
</script>

<template>
  <div class="itbms-row space-y-6">
    <!-- Main section: รูปซ้าย / รายละเอียดขวา -->
    <div class="flex flex-col md:flex-row gap-6">
      <!-- รูปหลัก -->
      <div class="flex-1">
        <img :src="mainImage" alt="main" class="w-full rounded" />
      </div>

      <!-- รายละเอียด -->
      <div class="flex-1 space-y-2">
        <div class="itbms-model text-2xl font-bold">{{ item.model }}</div>
        <div class="itbms-brand text-gray-600">{{ item.brandName }}</div>
        <div class="itbms-description">{{ item.description }}</div>

        <div class="grid grid-cols-2 gap-4 mt-4 text-sm">
          <div><strong>RAM: </strong><span class="itbms-ramGb">{{ item.ramGb ? `${item.ramGb}` : '-' }}</span><span class="itbms-ramGb-unit">GB</span></div>
          <div><strong>Storage: </strong><span class="itbms-storageGb">{{ item.storageGb ? `${item.storageGb}` : '-' }}</span><span class="itbms-storageGb-unit">GB</span></div>
          <div><strong>Screen Size: </strong><span class="itbms-screenSizeInch">{{ item.screenSizeInch ? `${item.screenSizeInch}` : '-' }}</span><span class="itbms-screenSizeInch-unit">Inches</span></div>
          <div><strong>Color: </strong><span class="itbms-color">{{ item.color || '-' }}</span></div>
          <div><strong>Available quantity: </strong><span class="itbms-quantity">{{ item.quantity }}</span><span class="itbms-quantity-unit"> units</span></div>
          <div><strong>Price: </strong><span class="itbms-price">{{ item.price.toLocaleString() }}</span><span class="itbms-price-unit"> Baht</span></div>
        </div>
      </div>
    </div>

    <!-- ปุ่ม Edit / Delete -->
    <div class="flex justify-center gap-4 mt-4">
      <BaseButton
        label="✏️ Edit"
        variant="edit"
                customClass="itbms-edit-button bg-blue-500 hover:bg-blue-600 text-white px-2 py-1 rounded"
        
        @click="onEdit"
      />
      <BaseButton
        label="🗑️ Delete"
        variant="delete"
                customClass="itbms-delete-button bg-red-500 hover:bg-red-600 text-white px-2 py-1 rounded"

        @click="onDelete"
      />
    </div>

    <!-- รูปเล็ก 4 รูปด้านล่าง -->
    <div class="flex justify-center gap-4">
      <img :src="mainImage" alt="thumb1" class="w-20 h-20 object-cover rounded" />
      <img :src="mainImage" alt="thumb2" class="w-20 h-20 object-cover rounded" />
      <img :src="mainImage" alt="thumb3" class="w-20 h-20 object-cover rounded" />
      <img :src="mainImage" alt="thumb4" class="w-20 h-20 object-cover rounded" />
    </div>
  </div>
</template>
