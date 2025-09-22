<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import SaleItemForm from '@/components/SaleItemForm.vue'
import AddSaleItemSuccess from '@/modals/AddSaleItemSuccess.vue'

const router = useRouter()
const showSuccessModal = ref(false)

const saleItem = ref({
  brandId: null,
  model: '',
  price: null,
  description: '',
  ramGb: null,
  screenSizeInch: null,
  storageGb: null,
  color: '',
  quantity: null
})

const goToSaleItems = () => {
  router.push('/sale-items/list')
}

const handleCancel = () => {
  router.push('/sale-items')
}

const handleSubmitSuccess = () => {
  showSuccessModal.value = true
  setTimeout(() => {
        goToSaleItems()
        }, 2000)
}

const closeModalAndRedirect = () => {
  showSuccessModal.value = false
  router.push('/sale-items')
}
</script>

<template>
  <div class="p-6 max-w-3xl mx-auto bg-white rounded-xl shadow">
    <h2 class="text-2xl font-bold mb-4">Add New Sale Item</h2>

    <SaleItemForm
      v-model="saleItem"
      @submit="handleSubmitSuccess"
      @cancel="handleCancel"
    />

    <AddSaleItemSuccess
      v-if="showSuccessModal"
      message="เพิ่มรายการขายเรียบร้อยแล้ว"
      @close="closeModalAndRedirect"
    />
  </div>
</template>
