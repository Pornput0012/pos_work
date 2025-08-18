<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EditSaleItemSuccess from '@/modals/EditSaleItemSuccess.vue'
import BaseButton from '@/components/BaseButton.vue'

const route = useRoute()
const router = useRouter()

const originalItem = ref(null)
const form = ref({})
const brands = ref([])
const isSaving = ref(false)

const showSuccessModal = ref(false)
const successMessage = ref('Sale item updated successfully.')

const fetchSaleItem = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items/${route.params.id}`)
    if (res.status === 404) {
      router.back()
      return
    }
    const data = await res.json()
    await fetchBrands()
    const brandObj = brands.value.find(b => b.name === data.brandName)
    const filledData = { ...data, brand: brandObj }
    form.value = filledData
    originalItem.value = JSON.parse(JSON.stringify(filledData))
  } catch (err) {
    console.error(err)
    router.back()
  }
}

const fetchBrands = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands`)
    brands.value = (await res.json()).sort((a, b) => a.name.localeCompare(b.name))
  } catch (err) {
    console.error('Error loading brands:', err)
  }
}

onMounted(async () => {
  await fetchSaleItem()
  await fetchBrands()
  form.value.brand = brands.value.find(b => b.name === form.value.brandName)
})

const trimTextField = (key) => {
  if (typeof form.value[key] === 'string') {
    form.value[key] = form.value[key].trim()
  }
}

const isFormChanged = computed(() => {
  return JSON.stringify(form.value) !== JSON.stringify(originalItem.value)
})

const handleSave = async () => {
  isSaving.value = true
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items/${route.params.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value),
    })

    if (res.status === 404) {
      router.back()
      return
    }

    await res.json()
    showSuccessModal.value = true
  } catch (err) {
    console.error(err)
  } finally {
    isSaving.value = false
  }
}

const handleCancel = () => {
  router.back()
}

const closeModalAndRedirect = () => {
  showSuccessModal.value = false
  router.push(`/sale-items/${route.params.id}`)
}
</script>

<template>
  <div class="max-w-5xl mx-auto p-6 bg-white shadow rounded-xl space-y-6">
    <!-- Breadcrumbs -->
    <nav class="text-sm text-gray-600 space-x-2 mb-4">
      <router-link to="/sale-items" class="text-blue-600 hover:underline" id="itbms-home-button">Home</router-link>
      <span>/</span>
      <router-link :to="`/sale-items/${route.params.id}`" class="text-blue-600 hover:underline" id="itbms-back-button">
        {{ originalItem?.model ? `${originalItem.model} ${originalItem.storageGb}GB ${originalItem.color}` : 'Loading...' }}
      </router-link>
    </nav>

    <!-- Main Content Grid: Image + Form -->
    <div v-if="form" class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="md:col-span-1">
        <img
          src="../assets/Test.jpg"
          alt="Product Image"
          class="w-full h-auto rounded-xl border object-cover"
        />
      </div>

      <div class="md:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-4">
        <label>
          Brand
          <select v-model="form.brand" class="w-full border p-2 rounded">
            <option disabled value="">Select brand</option>
            <option class="itbms-brand" v-for="b in brands" :key="b.id" :value="b">{{ b.name }}</option>
          </select>
        </label>

        <label>
          Model
          <input v-model="form.model" @blur="trimTextField('model')" class="itbms-model w-full border p-2 rounded" />
        </label>

        <label>
          Price (Baht)
          <input type="number" v-model.number="form.price" class="itbms-price w-full border p-2 rounded" />
        </label>

        <label class="sm:col-span-2">
          Description
          <textarea v-model="form.description" @blur="trimTextField('description')" class="itbms-description w-full border p-2 rounded" />
        </label>

        <label>
          RAM (GB)
          <input type="number" v-model.number="form.ramGb" class="itbms-ramGb w-full border p-2 rounded" />
        </label>

        <label>
          Screen Size (inches)
          <input type="number" v-model.number="form.screenSizeInch" class="itbms-screenSizeInch w-full border p-2 rounded" />
        </label>

        <label>
          Storage (GB)
          <input type="number" v-model.number="form.storageGb" class="itbms-storageGb w-full border p-2 rounded" />
        </label>

        <label>
          Color
          <input v-model="form.color" @blur="trimTextField('color')" class="itbms-color w-full border p-2 rounded" />
        </label>

        <label>
          Quantity
          <input type="number" v-model.number="form.quantity" class="itbms-quantity w-full border p-2 rounded" />
        </label>
      </div>
    </div>

    <!-- Buttons -->
    <div class="flex justify-end gap-4">
      <BaseButton
        label="Save"
        :disabled="!isFormChanged || isSaving"
        @click="handleSave"
        customClass="itbms-save-button px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition"
      />
      <BaseButton
        label="Cancel"
        @click="handleCancel"
        customClass="itbms-cancel-button px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400 transition"
      />
    </div>

    <!-- Success Modal -->
    <EditSaleItemSuccess
      v-if="showSuccessModal"
      :updatedItem="form"
      @close="closeModalAndRedirect"
    />
  </div>
</template>
