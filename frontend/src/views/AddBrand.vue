<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue'  // เปลี่ยนมาใช้ BaseButton
import {
  runValidation,
  validateBrandName,
  validateBrandURL,
  validateBrandOrigin,
} from '@/tools/validate.js'

const router = useRouter()

const form = reactive({
  brandName: '',
  websiteUrl: '',
  countryOfOrigin: '',
  isActive: true,
})

const errors = reactive({
  brandName: null,
  websiteUrl: null,
  countryOfOrigin: null,
})

const showMessage = ref(false)
const message = ref('')
const messageType = ref('') // 'success' or 'error'
const showModal = ref(false)
const isLoading = ref(false)

const isFormValid = computed(() => {
  const requiredFields = ['brandName']
  const requiredFieldsEmpty = requiredFields.some((field) => {
    const value = form[field]
    return value == null || value.toString().trim() === ''
  })

  const hasErrors = Object.values(errors).some((error) => error !== null)
  return !requiredFieldsEmpty && !hasErrors
})

const validateField = (field) => {
  let result = { valid: true, message: null }
  if (field === 'brandName') {
    result = runValidation(form.brandName, [validateBrandName])
  } else if (field === 'websiteUrl') {
    result = runValidation(form.websiteUrl, [validateBrandURL])
  } else if (field === 'countryOfOrigin') {
    result = runValidation(form.countryOfOrigin, [validateBrandOrigin])
  }

  errors[field] = result.message
  return result.valid
}

const trimField = (field) => {
  if (typeof form[field] === 'string') {
    form[field] = form[field].trim() || ''
  }
  validateField(field)
}

const handleSubmit = async () => {
  // Validate all fields before submission
  const isValid = [
    validateField('brandName'),
    validateField('websiteUrl'),
    validateField('countryOfOrigin'),
  ].every((valid) => valid)

  if (!isValid || !isFormValid.value) {
    console.warn('Form submission blocked: Invalid form')
    alert('กรุณากรอกข้อมูลให้ครบถ้วนและถูกต้อง')
    return
  }

  const payload = {
    name: form.brandName.trim(),
    websiteUrl: form.websiteUrl?.trim() || null,
    isActive: form.isActive,
    countryOfOrigin: form.countryOfOrigin?.trim() || null,
  }

  try {
    isLoading.value = true
    const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })

    if (response.status === 201) {
      showModal.value = true
    } else {
      const errorData = await response.json()
      showMessage.value = true
      messageType.value = 'error'
      message.value =
        response.status === 400
          ? 'The brand has already existed.'
          : `The brand could not be added (${response.status}): ${errorData.message || 'Unknown error'}`
    }
  } catch (err) {
    console.error('🚫 Request failed:', err)
    showMessage.value = true
    messageType.value = 'error'
    message.value = 'The brand could not be added (Network error)'
  } finally {
    isLoading.value = false
  }
}

const closeModal = () => {
  showModal.value = false
  router.push('/brands')
}

const handleCancel = () => {
  router.push('/brands')
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col items-center p-6">
    <!-- Top Nav Links -->
    <div class="w-full max-w-4xl mb-4">
      <a href="/sale-items" class="text-blue-600 hover:underline">Sale Item List</a>
      /
      <a href="/brands" class="text-blue-600 hover:underline">Brand List</a> /
      New Brand
    </div>

    <!-- Form Container -->
    <div v-if="showMessage" class="text-red-500 text-center mb-4">
      {{ message }}
    </div>
    <div v-else-if="!isLoading" class="w-full max-w-4xl">
      <form @submit.prevent="handleSubmit" class="p-6 bg-white rounded-lg shadow-md">
        <h2 class="text-xl font-semibold mb-6">New Brand</h2>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Brand Name -->
          <div>
            <label for="brandName" class="block font-medium mb-1">
              Name <span class="text-red-500">*</span>
            </label>
            <input
              id="brandName"
              v-model="form.brandName"
              @blur="trimField('brandName')"
              maxlength="30"
              required
              class="itbms-name w-full border rounded p-2"
              :class="{ 'border-red-500': errors.brandName }"
            />
            <p v-if="errors.brandName" class="text-sm text-red-500 mt-1">
              {{ errors.brandName }}
            </p>
          </div>

          <!-- Website URL -->
          <div>
            <label for="websiteUrl" class="block font-medium mb-1">Website URL</label>
            <input
              id="websiteUrl"
              v-model="form.websiteUrl"
              @blur="trimField('websiteUrl')"
              class="itbms-websiteUrl w-full border rounded p-2"
              :class="{ 'border-red-500': errors.websiteUrl }"
            />
            <p v-if="errors.websiteUrl" class="text-sm text-red-500 mt-1">
              {{ errors.websiteUrl }}
            </p>
          </div>

          <!-- Country of Origin -->
          <div>
            <label for="countryOfOrigin" class="block font-medium mb-1">Country of Origin</label>
            <input
              id="countryOfOrigin"
              v-model="form.countryOfOrigin"
              @blur="trimField('countryOfOrigin')"
              maxlength="81"
              class="itbms-countryOfOrigin w-full border rounded p-2"
              :class="{ 'border-red-500': errors.countryOfOrigin }"
            />
            <p v-if="errors.countryOfOrigin" class="text-sm text-red-500 mt-1">
              {{ errors.countryOfOrigin }}
            </p>
          </div>

          <!-- Active Checkbox -->
          <div>
            <label for="isActive" class="block font-medium mb-1">Active</label>
            <input
              id="isActive"
              type="checkbox"
              v-model="form.isActive"
              class="itbms-isActive h-5 w-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
          </div>
        </div>

        <!-- Buttons -->
        <div class="mt-6 flex gap-2">
          <BaseButton
            :disabled="!isFormValid"
            variant="save"
            label="Save"
             customClass="itbms-save-button px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition"
            type="submit"
          />
          <BaseButton
            variant="cancel"
            label="Cancel"
            customClass="itbms-cancel-button"
            @click="handleCancel"
          />
        </div>
      </form>
    </div>

    <!-- Success Modal -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-2xl p-6 w-full max-w-md shadow-lg text-center">
        <h2 class="text-xl font-semibold mb-4">Success</h2>
        <p class="itbms-message mb-6 text-gray-700">The brand has been added.</p>
        <p class="itbms-name text-xl font-bold mb-6 text-green-700">
          {{ form.brandName }}
        </p>
        <BaseButton
          variant="primary"
          label="OK"
          @click="closeModal"
          customClass="px-4 py-2 bg-blue-700 text-white rounded hover:bg-blue-800 transition"
        />
      </div>
    </div>

    <div v-if="isLoading" class="text-center text-gray-400">กำลังโหลด...</div>
  </div>
</template>
