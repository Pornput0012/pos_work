<script setup>
import iPhoneNoPhoto from '@/assets/iPhone_15_Black.png'
// ...existing code...
// --- Image Upload Logic ---
const originalImages = ref([])
const images = ref([])
const deletedImageFileNames = ref([])
const showImage = ref(iPhoneNoPhoto)
const showImageIndex = ref(-1)
const uploadInput = ref(null)
const MAX_SIZE_BYTES = 2 * 1024 * 1024
const maxImages = 4

const uploadImage = (event) => {
  const files = Array.from(event.target.files)
  const activeImagesCount = images.value.filter(img => !img.isDeleted).length
  const availableSlots = maxImages - activeImagesCount
  if (availableSlots === 0) {
    alert('All 4 image slots are occupied. Please remove some images first.')
    event.target.value = ""
    return
  }
  let filesToProcess = files.slice(0, availableSlots)
  filesToProcess.forEach((file, i) => {
    if (file.size > MAX_SIZE_BYTES) {
      alert(`File "${file.name}" exceeds the 2MB limit.`)
    } else {
      const reader = new FileReader()
      reader.onload = (e) => {
        const fileName = generateUniqueFileName(file.name)
        const image = {
          imageName: fileName,
          src: e.target.result,
          file,
          isOriginal: false,
          isDeleted: false,
        }
        let insertIndex = images.value.findIndex(img => img.isDeleted)
        if (insertIndex === -1) {
          images.value.push(image)
          insertIndex = images.value.length - 1
        } else {
          images.value[insertIndex] = image
        }
        if (showImageIndex.value === -1 || images.value[showImageIndex.value]?.isDeleted) {
          showImageIndex.value = insertIndex
          showImage.value = e.target.result
        }
      }
      reader.readAsDataURL(file)
    }
  })
  event.target.value = ""
}

const generateUniqueFileName = (originalName) => {
  const isDuplicate = images.value.some(img => img.imageName === originalName)
  if (!isDuplicate) return originalName
  const nameParts = originalName.split(".")
  const baseName = nameParts.slice(0, -1).join(".") || nameParts[0]
  const ext = nameParts.length > 1 ? "." + nameParts[nameParts.length - 1] : ""
  let suffix = 1
  let newName = `${baseName}(${suffix})${ext}`
  while (images.value.some(img => img.imageName === newName)) {
    suffix++
    newName = `${baseName}(${suffix})${ext}`
  }
  return newName
}

const swapUpImage = (i) => {
  if (i > 0) {
    let temp = images.value[i]
    images.value[i] = images.value[i - 1]
    images.value[i - 1] = temp
    if (showImageIndex.value === i) {
      showImageIndex.value = i - 1
    } else if (showImageIndex.value === i - 1) {
      showImageIndex.value = i
    }
    if (i - 1 === 0) {
      showImage.value = images.value[0].src
    }
  }
}

const swapDownImage = (i) => {
  if (i < images.value.length - 1) {
    if (showImageIndex.value === i) {
      showImageIndex.value = i + 1
    } else if (showImageIndex.value === i + 1) {
      showImageIndex.value = i
    }
    if (i === 0) {
      showImage.value = images.value[1].src
    }
    let temp = images.value[i]
    images.value[i] = images.value[i + 1]
    images.value[i + 1] = temp
  }
}

const removeImage = (index) => {
  const imageToRemove = images.value[index]
  if (imageToRemove.isOriginal && imageToRemove.imageName) {
    deletedImageFileNames.value.push(imageToRemove.imageName)
  }
  images.value[index] = {
    imageName: "",
    src: null,
    isOriginal: false,
    isDeleted: true,
    originalPosition: index + 1,
  }
  if (index === showImageIndex.value) {
    let nextValidImageIndex = -1
    for (let i = 0; i < images.value.length; i++) {
      if (!images.value[i].isDeleted) {
        nextValidImageIndex = i
        break
      }
    }
    if (nextValidImageIndex !== -1) {
      showImageIndex.value = nextValidImageIndex
      showImage.value = images.value[nextValidImageIndex].src
    } else {
      showImage.value = iPhoneNoPhoto
      showImageIndex.value = -1
    }
  }
}
import { ref, watch, onMounted, computed, reactive } from "vue";
import fetchUtil from "@/libs/fetchUtil.js";
import BaseButton from "@/components/BaseButton.vue";
import {
  runValidation,
  validateModel,
  validateBrandSelected,
  validateDescription,
  validatePrice,
  validateQuantity,
  validateRamSize,
  validateStorageSize,
  validateScreenSize,
  validateColor,
} from "@/tools/validate.js";

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({
      brandId: "",
      model: "",
      price: null,
      description: "",
      ramGb: null,
      screenSizeInch: null,
      storageGb: null,
      color: "",
      quantity: null,
    }),
  },
  submitLabel: {
    type: String,
    default: "บันทึก",
  },
});

const emit = defineEmits(["update:modelValue", "submit", "cancel"]);

const brands = ref([]);
const form = reactive({ ...props.modelValue });
const errors = reactive({
  brandId: null,
  model: null,
  description: null,
  price: null,
  quantity: null,
  ramGb: null,
  storageGb: null,
  screenSizeInch: null,
  color: null,
});
const btnNotAvailable = ref(true);
const isLoading = ref(true);
const errorMessage = ref(null);

onMounted(async () => {
  try {
    const res = await fetchUtil("/v1/brands");
    brands.value = res.sort((a, b) => a.name.localeCompare(b.name));
    isLoading.value = false;
  } catch (err) {
    console.error("ไม่สามารถโหลดแบรนด์ได้:", err);
    errorMessage.value = "ไม่สามารถโหลดข้อมูลแบรนด์ได้";
    isLoading.value = false;
  }
});

watch(
  () => props.modelValue,
  (newVal) => {
    if (JSON.stringify(newVal) !== JSON.stringify(form)) {
      Object.assign(form, { ...newVal });
    }
  },
  { deep: true }
);

watch(
  form,
  (newVal) => {
    emit("update:modelValue", { ...newVal });
  },
  { deep: true }
);

const isFormValid = computed(() => {
  const requiredFields = [
    "model",
    "price",
    "description",
    "brandId",
    "quantity",
  ];
  const requiredFieldsEmpty = requiredFields.some((field) => {
    const value = form[field];
    return value == null || value.toString().trim() === "";
  });

  const hasErrors = Object.values(errors).some((error) => error !== null);
  return !requiredFieldsEmpty && form.brandId && !hasErrors;
});

const validateField = (field) => {
  let result = { valid: true, message: null };
  if (field === "model") {
    result = runValidation(form.model, [validateModel]);
  } else if (field === "description") {
    result = runValidation(form.description, [validateDescription]);
  } else if (field === "price") {
    result = runValidation(form.price, [validatePrice]);
  } else if (field === "brandId") {
    result = runValidation(form.brandId, [validateBrandSelected]);
  } else if (field === "quantity") {
    result = runValidation(form.quantity, [validateQuantity]);
  } else if (field === "screenSizeInch") {
    result = runValidation(form.screenSizeInch, [validateScreenSize]);
  } else if (field === "color") {
    result = runValidation(form.color, [validateColor]);
  } else if (field === "storageGb") {
    result = runValidation(form.storageGb, [validateStorageSize]);
  } else if (field === "ramGb") {
    result = runValidation(form.ramGb, [validateRamSize]);
  }

  errors[field] = result.message;
  return result.valid;
};

const trimField = (field) => {
  if (typeof form[field] === "string") {
    form[field] = form[field].trim() || "";
  }
  validateField(field);
};

const handleSubmit = async () => {
  if (!isFormValid.value) {
    alert("กรุณากรอกข้อมูลให้ครบถ้วนและถูกต้อง")
    return
  }
  if (form.screenSizeInch != null && form.screenSizeInch > 99.99) {
    alert("ขนาดหน้าจอสูงสุดที่รองรับคือ 99.99 นิ้ว")
    return
  }
  // สร้าง FormData
  const formData = new FormData()
  formData.append("brandId", form.brandId)
  formData.append("model", form.model.trim())
  formData.append("price", form.price != null ? Number(form.price) : "")
  formData.append("description", form.description.trim())
  formData.append("ramGb", form.ramGb != null ? Number(form.ramGb) : "")
  formData.append("screenSizeInch", form.screenSizeInch != null ? Number(form.screenSizeInch) : "")
  formData.append("storageGb", form.storageGb != null ? Number(form.storageGb) : "")
  formData.append("color", form.color?.trim() || "")
  formData.append("quantity", form.quantity != null ? Number(form.quantity) : "")
  // แนบรูปภาพ
  images.value.filter(img => !img.isDeleted && img.file).forEach((img, idx) => {
    const splitFileName = img.imageName.split('.')
    const ext = splitFileName.pop()
    const joinFileName = splitFileName.join('.')
    const fileName = `${joinFileName}.${idx + 1}.${ext}`
    formData.append("images", img.file, fileName)
  })
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v2/sale-items`, {
      method: "POST",
      body: formData,
    })
    if (res.ok) {
      const data = await res.json()
      emit("submit", data)
    } else {
      const errorText = await res.text()
      alert(`เกิดข้อผิดพลาด (${res.status}): ${errorText}`)
    }
  } catch (err) {
    alert("ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้")
  }
};
</script>

<template>
  <div>
    <div v-if="errorMessage" class="text-red-500 text-center mb-4">
      {{ errorMessage }}
    </div>
    <div v-else-if="!isLoading">
      <form
        @submit.prevent="handleSubmit"
        class="p-6 bg-white rounded-lg shadow-md"
      >
        <div class="mb-4">
          <a href="/" class="text-blue-600 hover:underline">หน้าแรก</a> /
          สินค้าใหม่
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Brand -->
          <div>
            <label for="brand" class="block font-medium mb-1">
              แบรนด์ <span class="text-red-500">*</span>
            </label>
            <select
              id="brand"
              v-model="form.brandId"
              @blur="trimField('brandId')"
              required
              class="itbms-brand w-full border rounded p-2"
              :class="{ 'border-red-500': errors.brandId }"
            >
              <option disabled value="">กรุณาเลือกแบรนด์</option>
              <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                {{ brand.name }}
              </option>
            </select>
            <p v-if="errors.brandId" class="text-sm text-red-500 mt-1">
              {{ errors.brandId }}
            </p>
          </div>

          <!-- Model -->
          <div>
            <label for="model" class="block font-medium mb-1">
              รุ่น <span class="text-red-500">*</span>
            </label>
            <input
              id="model"
              v-model="form.model"
              @blur="trimField('model')"
              maxlength="61"
              required
              class="itbms-model w-full border rounded p-2"
              :class="{ 'border-red-500': errors.model }"
            />
            <p v-if="errors.model" class="text-sm text-red-500 mt-1">
              {{ errors.model }}
            </p>
          </div>

          <!-- Price -->
          <div>
            <label for="price" class="block font-medium mb-1">
              ราคา (บาท) <span class="text-red-500">*</span>
            </label>
            <input
              id="price"
              type="number"
              v-model.number="form.price"
              @blur="trimField('price')"
              required
              class="itbms-price w-full border rounded p-2"
              :class="{ 'border-red-500': errors.price }"
            />
            <p v-if="errors.price" class="text-sm text-red-500 mt-1">
              {{ errors.price }}
            </p>
          </div>

          <!-- Description -->
          <div class="md:col-span-2">
            <label for="description" class="block font-medium mb-1">
              คำอธิบาย <span class="text-red-500">*</span>
            </label>
            <textarea
              id="description"
              v-model="form.description"
              @blur="trimField('description')"
              required
              class="itbms-description w-full border rounded p-2"
              :class="{ 'border-red-500': errors.description }"
            ></textarea>
            <p v-if="errors.description" class="text-sm text-red-500 mt-1">
              {{ errors.description }}
            </p>
          </div>

          <!-- RAM -->
          <div>
            <label for="ram" class="block font-medium mb-1">RAM (GB)</label>
            <input
              id="ram"
              type="number"
              v-model.number="form.ramGb"
              @blur="trimField('ramGb')"
              class="itbms-ramGb w-full border rounded p-2"
              :class="{ 'border-red-500': errors.ramGb }"
            />
            <p v-if="errors.ramGb" class="text-sm text-red-500 mt-1">
              {{ errors.ramGb }}
            </p>
          </div>

          <!-- Screen Size -->
          <div>
            <label for="screen" class="block font-medium mb-1"
              >ขนาดหน้าจอ (นิ้ว)</label
            >
            <input
              id="screen"
              type="number"
              step="0.1"
              v-model.number="form.screenSizeInch"
              @blur="trimField('screenSizeInch')"
              class="itbms-screenSizeInch w-full border rounded p-2"
              :class="{ 'border-red-500': errors.screenSizeInch }"
            />
            <p v-if="errors.screenSizeInch" class="text-sm text-red-500 mt-1">
              {{ errors.screenSizeInch }}
            </p>
          </div>

          <!-- Storage -->
          <div>
            <label for="storage" class="block font-medium mb-1"
              >ความจุ (GB)</label
            >
            <input
              id="storage"
              type="number"
              v-model.number="form.storageGb"
              @blur="trimField('storageGb')"
              class="itbms-storageGb w-full border rounded p-2"
              :class="{ 'border-red-500': errors.storageGb }"
            />
            <p v-if="errors.storageGb" class="text-sm text-red-500 mt-1">
              {{ errors.storageGb }}
            </p>
          </div>

          <!-- Color -->
          <div>
            <label for="color" class="block font-medium mb-1">สี</label>
            <input
              id="color"
              v-model="form.color"
              @blur="trimField('color')"
              class="itbms-color w-full border rounded p-2"
              :class="{ 'border-red-500': errors.color }"
            />
            <p v-if="errors.color" class="text-sm text-red-500 mt-1">
              {{ errors.color }}
            </p>
          </div>

          <!-- Quantity -->
          <div>
            <label for="quantity" class="block font-medium mb-1">จำนวน</label>
            <input
              id="quantity"
              type="number"
              v-model.number="form.quantity"
              @blur="trimField('quantity')"
              class="itbms-quantity w-full border rounded p-2"
              :class="{ 'border-red-500': errors.quantity }"
            />
            <p v-if="errors.quantity" class="text-sm text-red-500 mt-1">
              {{ errors.quantity }}
            </p>
          </div>
        </div>

        <!-- Image Upload Section -->
        <div class="mb-8">
          <div class="flex flex-col items-center gap-4">
            <div class="border-2 rounded-2xl w-64 h-64 flex items-center justify-center">
              <img :src="showImage" class="w-full h-full object-contain rounded-2xl" alt="Preview" />
            </div>
            <div class="flex gap-4 mt-4 w-full justify-center items-center overflow-auto">
              <div v-for="(image, index) in images" :key="`image-${index}`" class="relative flex flex-col items-center">
                <div v-if="image.isDeleted" class="max-h-20 h-20 w-14 border-2 border-dashed border-gray-300 rounded-2xl flex items-center justify-center bg-gray-50 opacity-60">
                  <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </div>
                <img v-else class="max-h-20 h-20 w-14 border object-cover rounded-2xl cursor-pointer" :src="image.src" alt="" :class="showImageIndex == index ? 'border-2 border-blue-600 hover:border-black' : 'hover:border-blue-500'" @click="showImage = image.src; showImageIndex = index;" />
                <span class="text-xs mt-1" :class="image.isDeleted ? 'text-gray-400 line-through' : 'text-gray-600'">{{ index + 1 }}</span>
                <div class="text-xs mt-1">
                  <span v-if="image.isDeleted" class="text-gray-500 font-semibold">Deleted</span>
                  <span v-else-if="image.isOriginal" class="text-green-600 font-semibold">Original</span>
                  <span v-else class="text-blue-600 font-semibold">New</span>
                </div>
                <div class="flex gap-1 mt-1">
                  <button v-if="!image.isDeleted && index > 0" class="p-1 bg-gray-100 rounded hover:bg-gray-200" @click="swapUpImage(index)">
                    <img class="w-3 h-3" src="/src/assets/bxs--up-arrow.svg" alt="Up" />
                  </button>
                  <button v-if="!image.isDeleted && index < images.length - 1" class="p-1 bg-gray-100 rounded hover:bg-gray-200" @click="swapDownImage(index)">
                    <img class="w-3 h-3" src="/src/assets/bxs--down-arrow.svg" alt="Down" />
                  </button>
                  <button v-if="!image.isDeleted" class="text-red-500 cursor-pointer hover:text-red-700 hover:bg-red-100 p-1 rounded" @click="removeImage(index)">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
              <div v-for="i in Math.max(0, 4 - images.length)" :key="`empty-${i}`" class="relative flex flex-col items-center">
                <div class="max-h-20 h-20 w-14 border-2 border-dashed border-gray-300 rounded-2xl flex items-center justify-center bg-gray-50">
                  <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                </div>
                <span class="text-xs text-gray-400 mt-1">{{ images.length + i }}</span>
                <div class="text-xs mt-1"><span class="text-gray-400">Empty</span></div>
              </div>
            </div>
            <div class="flex flex-col items-center gap-2 mt-2">
              <button type="button" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer" @click="uploadInput.click()">Upload Images</button>
              <p class="text-sm text-gray-500">{{ images.filter(img => !img.isDeleted).length }}/4 images uploaded</p>
            </div>
            <input id="file-upload" type="file" class="hidden" ref="uploadInput" multiple accept="image/*" @change="uploadImage" />
          </div>
        </div>
        <div class="mt-6 flex gap-2">
          <BaseButton
            variant="save"
            :label="submitLabel"
            :disabled="!isFormValid"
            customClass="itbms-sale-item-add"
            @click="handleSubmit"
          />
          <BaseButton
            variant="cancel"
            label="cancel"
            customClass="itbms-cancel-button"
            @click="$emit('cancel')"
          />
        </div>
      </form>
    </div>
    <div v-else class="text-center text-gray-400">กำลังโหลดข้อมูลสินค้า...</div>
  </div>
</template>
