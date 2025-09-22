<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue'
import {
  runValidation,
  validateBrandName,
  validateBrandURL,
  validateBrandOrigin,
} from '@/tools/validate.js'

const router = useRouter()
const route = useRoute()
const brandId = route.params.id

const form = reactive({
  name: '',
  websiteUrl: '',
  isActive: false,
  countryOfOrigin: ''
})

const errors = reactive({
  name: null,
  websiteUrl: null,
  countryOfOrigin: null,
})

const original = ref({})

const showMessage = ref(false)
const message = ref('')
const messageType = ref('') // 'success' or 'error'

const isFormChanged = computed(() => {
  // เปรียบเทียบ form ปัจจุบันกับ original
  return JSON.stringify(form) !== JSON.stringify(original.value)
})

const validateField = (field) => {
  let result = { valid: true, message: null }
  if (field === 'name') {
    result = runValidation(form.name, [validateBrandName])
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
    form[field] = form[field].trim()
  }
  validateField(field)
}

const isFormValid = computed(() => {
  // name ต้องไม่ว่าง และผ่าน validation
  const requiredFields = ['name']
  const requiredFieldsEmpty = requiredFields.some((field) => {
    const value = form[field]
    return value == null || value.toString().trim() === ''
  })

  const hasErrors = Object.values(errors).some((error) => error !== null)
  return !requiredFieldsEmpty && !hasErrors
})

const isSaveDisabled = computed(() => {
  return !isFormValid.value || !isFormChanged.value
})

const fetchBrand = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands/${brandId}`)
    if (!res.ok) throw new Error('Failed to fetch brand')
    const data = await res.json()

    form.name = data.name || ''
    form.websiteUrl = data.websiteUrl || ''
    form.isActive = data.isActive ?? false
    form.countryOfOrigin = data.countryOfOrigin || ''

    // สำรองข้อมูลต้นฉบับ
    original.value = JSON.parse(JSON.stringify(form))

    // validate all fields หลังโหลดข้อมูล
    validateField('name')
    validateField('websiteUrl')
    validateField('countryOfOrigin')
  } catch (err) {
    console.error('❌ Failed to load brand:', err)
    showMessage.value = true
    messageType.value = 'error'
    message.value = 'The brand does not exist.'
  }
}

const handleSave = async () => {
  trimField('name')
  trimField('websiteUrl')
  trimField('countryOfOrigin')

  if (!isFormValid.value) {
    showMessage.value = true
    messageType.value = 'error'
    message.value = 'Please fill in the required fields correctly.'
    return
  }

  const payload = {
    name: form.name,
    websiteUrl: form.websiteUrl || null,
    isActive: form.isActive,
    countryOfOrigin: form.countryOfOrigin || null
  }

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands/${brandId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })

    if (res.ok) {
      showMessage.value = true
      messageType.value = 'success'
      message.value = 'The brand has been updated.'
      setTimeout(() => router.push('/brands'), 1500)
    } else {
      const errText = await res.text()
      showMessage.value = true
      messageType.value = 'error'
      message.value = `Failed to update (${res.status}): ${errText}`
    }
  } catch (err) {
    console.error(err)
    showMessage.value = true
    messageType.value = 'error'
    message.value = 'Network error while updating.'
  }
}

const handleCancel = () => {
  router.push('/brands')
}

onMounted(fetchBrand)
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col items-center p-6">
    <!-- Breadcrumbs -->
    <div class="w-full max-w-4xl mb-4 flex gap-4">
      <div class="mb-4">
        <router-link to="/brands" class="text-blue-600 hover:underline">Brand List</router-link> / Edit Brand
      </div>
    </div>

    <!-- Form Container -->
    <div class="w-full max-w-4xl bg-white border border-gray-300 rounded-xl p-6 shadow-md">
      <h2 class="text-xl font-semibold mb-6">Edit Brand</h2>

      <form @submit.prevent="handleSave" class="grid grid-cols-1 md:grid-cols-1 gap-6">
        <!-- Name -->
        <div>
          <label class="block mb-1 font-medium">
            Name <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.name"
            @blur="trimField('name')"
            class="itbms-name w-full border border-gray-400 rounded p-2"
            required
            maxlength="30"
            :class="{ 'border-red-500': errors.name }"
          />
          <p v-if="errors.name" class="text-sm text-red-500 mt-1">{{ errors.name }}</p>
        </div>

        <!-- Website URL -->
        <div>
          <label class="block mb-1 font-medium">Website URL</label>
          <input
            v-model="form.websiteUrl"
            @blur="trimField('websiteUrl')"
            class="itbms-websiteUrl w-full border border-gray-400 rounded p-2"
            type="url"
            maxlength="40"
            :class="{ 'border-red-500': errors.websiteUrl }"
          />
          <p v-if="errors.websiteUrl" class="text-sm text-red-500 mt-1">{{ errors.websiteUrl }}</p>
        </div>

        <!-- isActive Checkbox -->
        <div class="mt-6">
          <label for="isActive" class="block mb-1 font-medium">Active</label>
          <div class="flex items-center gap-2">
            <input
              id="isActive"
              type="checkbox"
              class="itbms-isActive h-5 w-5 text-green-600 border-gray-300 rounded focus:ring-green-500"
              v-model="form.isActive"
            />
            <span class="text-sm text-gray-700"></span>
          </div>
        </div>

        <!-- Country -->
        <div>
          <label class="block mb-1 font-medium">Country Of Origin</label>
          <input
            v-model="form.countryOfOrigin"
            @blur="trimField('countryOfOrigin')"
            class="itbms-countryOfOrigin w-full border border-gray-400 rounded p-2"
            maxlength="80"
            :class="{ 'border-red-500': errors.countryOfOrigin }"
          />
          <p v-if="errors.countryOfOrigin" class="text-sm text-red-500 mt-1">{{ errors.countryOfOrigin }}</p>
        </div>

        <!-- Save / Cancel Buttons -->
        <div class="col-span-full flex gap-4 mt-4">
          <BaseButton
            type="submit"
            :disabled="isSaveDisabled"
            variant="save"
            label="Save"
            customClass="itbms-save-button px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition"
          />
          <BaseButton
            type="button"
            variant="cancel"
            label="Cancel"
            class="itbms-cancel-button"
            @click="handleCancel"
          />
        </div>
      </form>

      <div
        v-if="showMessage"
        :class="messageType === 'success' ? 'itbms-message text-green-600' : 'itbms-message text-red-600'"
        class="mt-4"
      >
        {{ message }}
      </div>
    </div>
  </div>
</template>
