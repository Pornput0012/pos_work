<script setup>
import { ref, watch } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import iPhoneNoPhoto from "@/assets/iPhone_15_Black.png"

const props = defineProps({
  item: Object
})
const emit = defineEmits(['edit', 'delete'])

// state รูป
const images = ref([])
const showImage = ref(iPhoneNoPhoto)
const showImageIndex = ref(-1)
const maxImages = 4

function onEdit() {
  emit('edit')
}

function onDelete() {
  emit('delete')
}

// โหลดรูปจาก backend
const loadImages = async (saleItemImages) => {
  images.value = []

  const sortedImages = saleItemImages.sort((a, b) => a.imageViewOrder - b.imageViewOrder)
  for (const imgData of sortedImages) {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/sale-items/${imgData.fileName}`)
      if (response.ok) {
        const blob = await response.blob()
        const imageUrl = URL.createObjectURL(blob)
        images.value.push({ src: imageUrl, fileName: imgData.fileName })
      }
    } catch (err) {
      console.error(`❌ Failed to load image ${imgData.fileName}:`, err)
    }
  }

  // เติม slot ว่าง
  while (images.value.length < maxImages) {
    images.value.push({ src: null, isEmpty: true })
  }

  // ตั้ง preview เป็นรูปแรก
  const firstActive = images.value.find(img => img.src)
  if (firstActive) {
    showImage.value = firstActive.src
    showImageIndex.value = images.value.indexOf(firstActive)
  } else {
    showImage.value = iPhoneNoPhoto
    showImageIndex.value = -1
  }
}

// คอยดู item ที่ส่งเข้ามา
watch(
  () => props.item,
  (newVal) => {
    if (newVal?.saleItemImages) {
      loadImages(newVal.saleItemImages)
    }
  },
  { immediate: true }
)
</script>

<template>
  <div class="itbms-row space-y-6">
    <!-- Main section -->
    <div class="flex flex-col md:flex-row gap-6">
      <!-- รูป -->
      <div class="flex-1 flex flex-col items-center gap-4">
        <!-- รูปหลัก -->
        <div class="border-2 rounded-2xl w-64 h-64 flex items-center justify-center">
          <img :src="showImage" alt="main" class="w-full h-full object-contain rounded-2xl" />
        </div>

        <!-- Thumbnails -->
        <div class="flex gap-2 mt-4 w-full justify-center items-center overflow-auto flex-wrap">
          <div v-for="(image, index) in images" :key="index" class="relative flex flex-col items-center">
            <!-- ถ้าเป็น slot ว่าง -->
            <div
              v-if="image.isEmpty"
              class="max-h-16 h-16 w-12 border-2 border-dashed border-gray-300 rounded-xl flex items-center justify-center bg-gray-50"
            >
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
            </div>

            <!-- ถ้ามีรูป -->
            <img
              v-else
              class="max-h-16 h-16 w-12 border object-cover rounded-xl cursor-pointer"
              :src="image.src"
              :class="showImageIndex === index ? 'border-2 border-blue-600' : 'hover:border-blue-500'"
              @click="showImage = image.src; showImageIndex = index"
            />
          </div>
        </div>
      </div>

      <!-- รายละเอียด -->
      <div class="flex-1 space-y-2">
        <div class="itbms-model text-2xl font-bold">{{ item.model }}</div>
        <div class="itbms-brand text-gray-600">{{ item.brandName }}</div>
        <div class="itbms-description">{{ item.description }}</div>

        <div class="grid grid-cols-2 gap-4 mt-4 text-sm">
          <div><strong>RAM: </strong><span class="itbms-ramGb">{{ item.ramGb || '-' }}</span> GB</div>
          <div><strong>Storage: </strong><span class="itbms-storageGb">{{ item.storageGb || '-' }}</span> GB</div>
          <div><strong>Screen Size: </strong><span class="itbms-screenSizeInch">{{ item.screenSizeInch || '-' }}</span> Inches</div>
          <div><strong>Color: </strong><span class="itbms-color">{{ item.color || '-' }}</span></div>
          <div><strong>Available quantity: </strong><span class="itbms-quantity">{{ item.quantity }}</span> units</div>
          <div><strong>Price: </strong><span class="itbms-price">{{ item.price?.toLocaleString() }}</span> Baht</div>
        </div>
      </div>
    </div>

    <!-- ปุ่ม -->
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
  </div>
</template>
