<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SaleItemDetailInfo from '../components/SaleItemDetailInfo.vue'
import DeleteSaleItem from '@/modals/DeleteSaleItem.vue'
import DeleteSaleItemSuccess from '@/modals/DeleteSaleItemSuccess.vue'

const route = useRoute()
const router = useRouter()
const item = ref(null)
const error = ref(null)
const showDeleteModal = ref(false)
const showDeleteSuccessModal = ref(false)
const deleteError = ref(null) // เพิ่มตัวแปรใหม่เพื่อจัดการข้อความ error สำหรับการลบ

const goToSaleItems = () => {
  router.push('/sale-items')
}

const handleEdit = () => {
  router.push(`/sale-items/${route.params.id}/edit`)
}

const handleDelete = () => {
  if (!item.value) return
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items/${route.params.id}`, {
      method: 'DELETE'
    })

    if (res.ok) {
      // เมื่อการลบสำเร็จให้แสดง DeleteSaleItemSuccess Modal
      showDeleteModal.value = false
      showDeleteSuccessModal.value = true
      setTimeout(() => {
        showDeleteSuccessModal.value = false
        goToSaleItems()
        }, 2000)
    } else if (res.status === 404) {
      // หากไม่พบ ID ของสินค้าให้แสดงข้อความ error
      deleteError.value = 'The requested sale item does not exist.'
      setTimeout(() => {
        goToSaleItems()
        }, 2000)
      return
    } else {
      alert(`Failed to delete item: ${res.statusText}`)
    }
  } catch (err) {
    console.error('Delete error:', err)
    alert('Error deleting item.')
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
}

const closeSuccessModal = () => {
  showDeleteSuccessModal.value = false
  goToSaleItems()
}

onMounted(async () => {
  const id = route.params.id
  if (!id) {
    error.value = 'Invalid item ID.'
    return
  }

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items/${id}`)

    if (res.ok) {
      item.value = await res.json()
    } else if (res.status === 404) {
      error.value = 'The requested sale item does not exist.'
      setTimeout(() => {
      router.push("/sale-items")
      }, 2000)
    } else {
      error.value = `Unexpected error: ${res.status} ${res.statusText}`
    }
  } catch (err) {
    error.value = 'Failed to connect to the server.'
    console.error('[SaleItemDetail] Fetch error →', err)
  }
})
</script>

<template>
  <div class="p-6 max-w-2xl mx-auto bg-white rounded-xl shadow relative">
    <!-- Error Message -->
    <div v-if="error" class="itbms-message relative text-center text-red-500 p-4 border border-red-300 bg-red-50 rounded">
      <p>{{ error }}</p>
      <button
        @click="goToSaleItems"
        class="absolute top-2 right-2 text-gray-500 hover:text-red-600 text-lg font-bold"
        aria-label="Close"
      >
        ×
      </button>
    </div>

    <!-- Loading -->
    <div v-else-if="!item" class="text-center text-gray-400">Loading...</div>

    <!-- Item Detail -->
    <div v-else>
      <SaleItemDetailInfo
        :item="item"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>

    <!-- Delete Modal -->
    <DeleteSaleItem
      v-if="showDeleteModal"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />

    <!-- Success Modal -->
    <DeleteSaleItemSuccess 
      v-if="showDeleteSuccessModal"
      @close="closeSuccessModal"
    />

    <!-- แสดงข้อความ error ในกรณีที่ลบไม่สำเร็จ -->
    <div v-if="deleteError" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white p-6 rounded-lg shadow-md w-full max-w-md text-center">
        <h2 class="itbms-message text-lg font-semibold text-gray-800 mb-4">{{ deleteError }}</h2>
        <button @click="cancelDelete" class="mt-4 bg-gray-300 hover:bg-gray-400 text-white px-4 py-2 rounded">
          OK
        </button>
      </div>
    </div>
  </div>
</template>
