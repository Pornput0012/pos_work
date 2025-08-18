<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue'
import DeleteBrand from '@/modals/DeleteBrand.vue'
import DeleteBrandSuccess from '@/modals/DeleteBrandSuccess.vue'
import DeleteBrandError from '@/modals/DeleteBrandError.vue'

const router = useRouter()

const brands = ref([])
const showDeleteModal = ref(false)
const brandToDelete = ref(null)
const brandToDeleteName = ref('')

const showMessageModal = ref(false)
const messageModalText = ref('')
const showErrorModal = ref(false)

const showWarningModal = ref(false)
const deleteTargetId = ref(null)
const deleteTargetName = ref(null)

function closeErrorModal() {
  showErrorModal.value = false
}

const fetchBrands = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands`)
    if (!res.ok) throw new Error('Failed to fetch brands')
    brands.value = await res.json()
  } catch (err) {
    console.error('Error fetching brands:', err)
    messageModalText.value = 'An error has occurred, the brand does not exist.'
    showMessageModal.value = true
  }
}

onMounted(fetchBrands)

const handleEdit = (id) => {
  router.push(`/brands/${id}/edit`)
}

const confirmDelete = async (brand) => {
  deleteTargetId.value = brand.id
  deleteTargetName.value = brand.name
  brandToDeleteName.value = brand.name

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items`)
    const saleItems = await res.json()

    const hasSaleItem = saleItems?.some((item) => item.brandName === brand.name)
    
    if (hasSaleItem) {
      showWarningModal.value = true
    } else {
      brandToDelete.value = brand.id
      brandToDeleteName.value = brand.name
      showDeleteModal.value = true
    }
  } catch (error) {
    console.error('Failed to fetch sale items:', error)
  }
}

const warningCancel = () => {
  showWarningModal.value = false
}

const deleteBrand = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands/${brandToDelete.value}`, {
      method: 'DELETE'
    })
    if (!res.ok) {
      if (res.status === 400) {
        showDeleteModal.value = false
        showErrorModal.value = true
        return
      }
      throw new Error('An error has occurred, the brand does not exist.')
    }

    brands.value = brands.value.filter(b => b.id !== brandToDelete.value)
    messageModalText.value = 'The brand has been deleted.'
  } catch (err) {
    console.error('Error deleting brand:', err)
    messageModalText.value = 'An error has occurred, the brand does not exist.'
  } finally {
    showDeleteModal.value = false
    brandToDelete.value = null
    brandToDeleteName.value = ''
    showMessageModal.value = true
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  brandToDelete.value = null
  brandToDeleteName.value = ''
}

const closeMessageModal = () => {
  showMessageModal.value = false
  showErrorModal.value = false
  messageModalText.value = ''
}

const goToAddBrand = () => {
  router.push('/brands/add')
}
const goToSaleItems = () => {
  router.push('/sale-items')
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header Buttons -->
    <div class="flex gap-4 mb-6">
      <BaseButton
        customClass="itbms-item-list bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded"
        @click="goToSaleItems"
      >
        Sale Item List
      </BaseButton>

      <BaseButton
        customClass="itbms-add-button bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
        @click="goToAddBrand"
      >
        Add Brand
      </BaseButton>
    </div>

    <!-- Brands Table -->
    <div class="overflow-x-auto rounded-lg shadow border border-gray-800 bg-white">
      <table class="w-full table-auto text-sm text-black">
        <thead class="bg-blue-800 text-white border-b border-gray-800">
          <tr>
            <th class="itbms-id text-left px-4 py-3 font-semibold">Id</th>
            <th class="itbms-name text-left px-4 py-3 font-semibold">Name</th>
            <th class="itbms-action text-center px-4 py-3 font-semibold">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="brands.length === 0" class="text-center text-gray-500">
            <td colspan="3" class="px-4 py-6">no brand</td>
          </tr>
          <tr v-for="brand in brands" :key="brand.id" class="itbms-row border-b border-gray-200 hover:bg-gray-100">
            <td class="itbms-id px-4 py-3">{{ brand.id }}</td>
            <td class="itbms-name px-4 py-3">{{ brand.name }}</td>
            <td class="itbms-isAction px-4 py-3 text-center flex justify-center gap-2">
              <BaseButton
                customClass="itbms-edit-button bg-blue-500 hover:bg-blue-600 text-white px-2 py-1 rounded"
                @click="handleEdit(brand.id)"
              >
                E
              </BaseButton>

              <BaseButton
                customClass="itbms-delete-button bg-red-500 hover:bg-red-600 text-white px-2 py-1 rounded"
                @click="confirmDelete(brand)"
              >
                D
              </BaseButton>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <DeleteBrand
      v-if="showDeleteModal"
      :name="brandToDeleteName"
      @confirm="deleteBrand"
      @cancel="cancelDelete"
    />

    <DeleteBrandSuccess
      v-if="showMessageModal"
      :message="messageModalText"
      @close="closeMessageModal"
      class="itbms-message"
    />

    <DeleteBrandError
      v-if="showWarningModal"
      :name="brandToDeleteName"
      @close="warningCancel"
      class="itbms-message"
    />
  </div>
</template>
