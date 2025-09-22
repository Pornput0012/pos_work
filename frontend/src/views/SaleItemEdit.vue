<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EditSaleItemSuccess from '@/modals/EditSaleItemSuccess.vue'
import BaseButton from '@/components/BaseButton.vue'
import iPhoneNoPhoto from "@/assets/iPhone_15_Black.png";

const route = useRoute()
const router = useRouter()

const originalItem = ref(null)
const form = ref({})
const brands = ref([])
const isSaving = ref(false)

const showSuccessModal = ref(false)
const successMessage = ref('Sale item updated successfully.')

const originalImages = ref([]);
const images = ref([]);
const deletedImageFileNames = ref([]);
const showImage = ref(iPhoneNoPhoto);
const showImageIndex = ref(-1);
const uploadInput = ref(null);
const MAX_SIZE_BYTES = 2 * 1024 * 1024;
const maxImages = 4;

const newImages = ref([]);
const deletedOriginalImages = ref([]);
const reorderedImages = ref([]);

const fetchSaleItem = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v2/sale-items/${route.params.id}`)
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
    
    await loadImages(data.saleItemImages || [])
  } catch (err) {
    console.error(err)
    router.back()
  }
}

const loadImages = async (saleItemImages) => {
  const sortedImages = saleItemImages.sort((a, b) => a.imageViewOrder - b.imageViewOrder)
  
  for (const imgData of sortedImages) {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/sale-items/${imgData.fileName}`)
      if (response.ok) {
        const blob = await response.blob()
        const imageUrl = URL.createObjectURL(blob)
        
        const image = {
          imageName: imgData.fileName,
          src: imageUrl,
          file: null,
          isOriginal: true,
          isDeleted: false,
          originalOrder: imgData.imageViewOrder
        }
        
        images.value.push(image)
        originalImages.value.push({ ...image })
      }
    } catch (err) {
      console.error(`Failed to load image ${imgData.fileName}:`, err)
    }
  }
  
  while (images.value.length < maxImages) {
    images.value.push({
      imageName: "",
      src: null,
      isOriginal: false,
      isDeleted: false,
      isEmpty: true,
    });
  }
  
  const firstActiveImage = images.value.find(img => !img.isDeleted && !img.isEmpty)
  if (firstActiveImage) {
    showImage.value = firstActiveImage.src
    showImageIndex.value = images.value.indexOf(firstActiveImage)
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
})

const trimTextField = (key) => {
  if (typeof form.value[key] === 'string') {
    form.value[key] = form.value[key].trim()
  }
}

const isFormDataChanged = computed(() => {
  if (!originalItem.value) return false
  
  const formKeys = ['brand', 'model', 'price', 'description', 'ramGb', 'screenSizeInch', 'storageGb', 'color', 'quantity']
  
  return formKeys.some(key => {
    if (key === 'brand') {
      return form.value.brand?.id !== originalItem.value.brand?.id
    }
    return form.value[key] !== originalItem.value[key]
  })
})

const isImagesChanged = computed(() => {
  return newImages.value.length > 0 || deletedOriginalImages.value.length > 0 || reorderedImages.value.length > 0
})

const isFormChanged = computed(() => {
  return isFormDataChanged.value || isImagesChanged.value
})

const uploadImage = (event) => {
  const files = Array.from(event.target.files);
  
  if (files.length > 4) {
    alert("Maximum 4 pictures are allowed.");
  }
  
  const activeImagesCount = images.value.filter(
    (img) => !img.isDeleted && !img.isEmpty && img.src
  ).length;
  const availableSlots = maxImages - activeImagesCount;
  
  if (availableSlots <= 0) {
    alert("All 4 image slots are occupied. Please remove some images first.");
    event.target.value = "";
    return;
  }
  
  const filesToProcess = files.slice(0, Math.min(4, availableSlots));
  
  filesToProcess.forEach((file) => {
    if (file.size > MAX_SIZE_BYTES) {
      alert(`File "${file.name}" exceeds the 2MB limit.`);
    } else {
      const reader = new FileReader();
      reader.onload = (e) => {
        const fileName = generateUniqueFileName(file.name);
        const image = {
          imageName: fileName,
          src: e.target.result,
          file,
          isOriginal: false,
          isDeleted: false,
        };
        
        let insertIndex = images.value.findIndex(
          (img) => img.isDeleted || img.isEmpty
        );
        if (insertIndex === -1) {
          images.value.push(image);
          insertIndex = images.value.length - 1;
        } else {
          images.value[insertIndex] = image;
        }
        
        newImages.value.push(image);
        
        if (
          showImageIndex.value === -1 ||
          images.value[showImageIndex.value]?.isDeleted
        ) {
          showImageIndex.value = insertIndex;
          showImage.value = e.target.result;
        }
      };
      reader.readAsDataURL(file);
    }
  });
  event.target.value = "";
};

const generateUniqueFileName = (originalName) => {
  const isDuplicate = images.value.some(
    (img) => img.imageName === originalName
  );
  if (!isDuplicate) return originalName;
  const nameParts = originalName.split(".");
  const baseName = nameParts.slice(0, -1).join(".") || nameParts[0];
  const ext = nameParts.length > 1 ? "." + nameParts[nameParts.length - 1] : "";
  let suffix = 1;
  let newName = `${baseName}(${suffix})${ext}`;
  while (images.value.some((img) => img.imageName === newName)) {
    suffix++;
    newName = `${baseName}(${suffix})${ext}`;
  }
  return newName;
};

const swapUpImage = (i) => {
  if (i <= 0) return;
  const temp = images.value[i];
  images.value[i] = images.value[i - 1];
  images.value[i - 1] = temp;

  if (showImageIndex.value === i) {
    showImageIndex.value = i - 1;
  } else if (showImageIndex.value === i - 1) {
    showImageIndex.value = i;
  }
  
  updateReorderedImages();
};

const swapDownImage = (i) => {
  const activeIdx = activeImages.value.indexOf(images.value[i]);
  if (activeIdx === -1) return;

  if (activeImages.value.length > 1) {
    const nextActiveImage = activeImages.value[activeIdx + 1];
    if (!nextActiveImage) return;
    const nextIndex = images.value.indexOf(nextActiveImage);
    const temp = images.value[i];
    images.value[i] = images.value[nextIndex];
    images.value[nextIndex] = temp;

    if (showImageIndex.value === i) {
      showImageIndex.value = nextIndex;
    } else if (showImageIndex.value === nextIndex) {
      showImageIndex.value = i;
    }
    updateReorderedImages();
    return;
  }

  if (images.value.length < maxImages) {
    while (images.value.length < maxImages) {
      images.value.push({
        imageName: "",
        src: null,
        isOriginal: false,
        isDeleted: false,
        isEmpty: true,
      });
    }
  }

  const nextIndex = i + 1;
  if (nextIndex >= images.value.length) return;

  const temp = images.value[i];
  images.value[i] = images.value[nextIndex];
  images.value[nextIndex] = temp;

  if (showImageIndex.value === i) {
    showImageIndex.value = nextIndex;
  } else if (showImageIndex.value === nextIndex) {
    showImageIndex.value = i;
  }
  
  updateReorderedImages();
};

const removeImage = (index) => {
  const imageToRemove = images.value[index];
  
  if (imageToRemove.isOriginal && imageToRemove.imageName) {
    deletedOriginalImages.value.push(imageToRemove.imageName);
  } else if (!imageToRemove.isOriginal && imageToRemove.file) {
    const newImageIndex = newImages.value.findIndex(img => img.imageName === imageToRemove.imageName);
    if (newImageIndex !== -1) {
      newImages.value.splice(newImageIndex, 1);
    }
  }
  
  images.value[index] = {
    imageName: "",
    src: null,
    isOriginal: false,
    isDeleted: true,
    originalPosition: index + 1,
  };
  
  if (index === showImageIndex.value) {
    let nextValidImageIndex = -1;
    for (let i = 0; i < images.value.length; i++) {
      if (!images.value[i].isDeleted && !images.value[i].isEmpty) {
        nextValidImageIndex = i;
        break;
      }
    }
    if (nextValidImageIndex !== -1) {
      showImageIndex.value = nextValidImageIndex;
      showImage.value = images.value[nextValidImageIndex].src;
    } else {
      showImage.value = iPhoneNoPhoto;
      showImageIndex.value = -1;
    }
  }
  
  updateReorderedImages();
};

const updateReorderedImages = () => {
  reorderedImages.value = images.value
    .filter(img => !img.isDeleted && !img.isEmpty)
    .map((img, index) => ({
      fileName: img.imageName,
      newOrder: index + 1
    }));
};

const getActiveIndex = (image) => {
  return activeImages.value.indexOf(image);
};

const activeImages = computed(() =>
  images.value.filter((img) => !img.isDeleted && !img.isEmpty)
);

const createGeneralFormData = () => {
  const formData = new FormData();
  
  formData.append("brandId", form.value.brand?.id || "");
  formData.append("model", form.value.model?.trim() || "");
  formData.append("price", form.value.price != null ? Number(form.value.price) : "");
  formData.append("description", form.value.description?.trim() || "");
  formData.append("ramGb", form.value.ramGb != null ? Number(form.value.ramGb) : "");
  formData.append("screenSizeInch", form.value.screenSizeInch != null ? Number(form.value.screenSizeInch) : "");
  formData.append("storageGb", form.value.storageGb != null ? Number(form.value.storageGb) : "");
  formData.append("color", form.value.color?.trim() || "");
  formData.append("quantity", form.value.quantity != null ? Number(form.value.quantity) : "");
  
  return formData;
}

const createImageFormData = () => {
  const formData = new FormData();
  
  
  const activeNewImages = newImages.value.filter(img => {
    const currentImage = images.value.find(current => current.imageName === img.imageName && !current.isDeleted);
    return currentImage;
  });
  
  activeNewImages.forEach((img, idx) => {
    const currentIndex = images.value.findIndex(current => current.imageName === img.imageName && !current.isDeleted);
    const newOrder = activeImages.value.indexOf(images.value[currentIndex]) + 1;
    
    const splitFileName = img.imageName.split(".");
    const ext = splitFileName.pop();
    const joinFileName = splitFileName.join(".");
    const fileName = `${joinFileName}.${newOrder}.${ext}`;
    
    formData.append("newImages", img.file, fileName);
  });
  
  if (deletedOriginalImages.value.length > 0) {
    formData.append("removeFileNames", deletedOriginalImages.value.join(","));
  }
  
  const orderImageStrings = [];
  images.value.forEach((img, index) => {
    if (!img.isDeleted && !img.isEmpty && img.isOriginal) {
      const newOrder = activeImages.value.indexOf(img) + 1;
      if (img.originalOrder !== newOrder) {
        orderImageStrings.push(`${img.imageName},${newOrder}`);
      }
    }
  });
  
  if (orderImageStrings.length > 0) {
    formData.append("orderImages", orderImageStrings.join("|"));
  }
  
  return formData;
}

const createCombinedFormData = () => {
  const formData = createGeneralFormData();
  const imageFormData = createImageFormData();
  
  for (const pair of imageFormData.entries()) {
    formData.append(pair[0], pair[1]);
  }
  
  return formData;
}

const handleSave = async () => {
  isSaving.value = true
  try {
    let endpoint = `${import.meta.env.VITE_API_URL}/v2/sale-items/${route.params.id}`;
    let formData;
    
    if (isFormDataChanged.value && isImagesChanged.value) {
      formData = createCombinedFormData();
    } else if (isImagesChanged.value && !isFormDataChanged.value) {
      endpoint = `${import.meta.env.VITE_API_URL}/v2/sale-items/${route.params.id}/images`;
      formData = createImageFormData();
    } else if (isFormDataChanged.value && !isImagesChanged.value) {
      formData = createGeneralFormData();
    } else {
      return;
    }

    const res = await fetch(endpoint, {
      method: 'PUT',
      body: formData,
    })

    if (res.status === 404) {
      router.back()
      return
    }

    await res.json()
    showSuccessModal.value = true
  } catch (err) {
    console.error(err)
    alert("เกิดข้อผิดพลาดในการบันทึกข้อมูล")
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
      <!-- Image Preview Section -->
      <div class="md:col-span-1">
        <div class="flex flex-col items-center gap-4">
          <!-- Preview ภาพใหญ่ -->
          <div class="border-2 rounded-2xl w-64 h-64 flex items-center justify-center">
            <img
              :src="showImage"
              class="w-full h-full object-contain rounded-2xl"
              alt="Preview"
            />
          </div>

          <!-- Thumbnails -->
          <div class="flex gap-2 mt-4 w-full justify-center items-center overflow-auto flex-wrap">
            <div
              v-for="(image, index) in images"
              :key="`image-${index}`"
              class="relative flex flex-col items-center"
            >
              <!-- Deleted Placeholder -->
              <div
                v-if="image.isDeleted"
                class="max-h-16 h-16 w-12 border-2 border-dashed border-gray-300 rounded-xl flex items-center justify-center bg-gray-50 opacity-60"
              >
                <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </div>
              
              <!-- Empty Placeholder -->
              <div
                v-else-if="image.isEmpty"
                class="max-h-16 h-16 w-12 border-2 border-dashed border-gray-300 rounded-xl flex items-center justify-center bg-gray-50"
              >
                <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
              </div>

              <!-- Thumbnail ปกติ -->
              <img
                v-else
                class="max-h-16 h-16 w-12 border object-cover rounded-xl cursor-pointer"
                :src="image.src"
                alt=""
                :class="
                  showImageIndex === index
                    ? 'border-2 border-blue-600 hover:border-black'
                    : 'hover:border-blue-500'
                "
                @click.stop="
                  showImage = image.src;
                  showImageIndex = index;
                "
              />

              <!-- Order Number -->
              <span class="text-xs mt-1" :class="image.isDeleted ? 'text-gray-400 line-through' : image.isEmpty ? 'text-gray-400' : 'text-gray-600'">
                {{ index + 1 }}
              </span>

              <!-- Status -->
              <div class="text-xs mt-1">
                <span v-if="image.isDeleted" class="text-gray-500 font-semibold">Deleted</span>
                <span v-else-if="image.isEmpty" class="text-gray-400">Empty</span>
                <span v-else-if="image.isOriginal" class="text-green-600 font-semibold">Original</span>
                <span v-else class="text-blue-600 font-semibold">New</span>
              </div>

              <!-- Control Buttons -->
              <div class="flex gap-1 mt-1" v-if="!image.isDeleted && !image.isEmpty">
                <button
                  type="button"
                  class="p-1 bg-gray-100 rounded hover:bg-gray-200 disabled:opacity-40 disabled:cursor-not-allowed"
                  :disabled="index === 0"
                  @click.stop="swapUpImage(index)"
                >
                  ↑
                </button>
                <button
                  type="button"
                  class="p-1 bg-gray-100 rounded hover:bg-gray-200 disabled:opacity-40 disabled:cursor-not-allowed"
                  :disabled="index === maxImages - 1"
                  @click.stop="swapDownImage(index)"
                >
                  ↓
                </button>
                <button
                  type="button"
                  class="text-red-500 cursor-pointer hover:text-red-700 hover:bg-red-100 p-1 rounded"
                  @click.stop="removeImage(index)"
                >
                  ×
                </button>
              </div>
            </div>

            <!-- Empty Slots -->
            <div
              v-for="i in Math.max(0, 4 - images.length)"
              :key="`empty-${i}`"
              class="relative flex flex-col items-center"
            >
              <div class="max-h-16 h-16 w-12 border-2 border-dashed border-gray-300 rounded-xl flex items-center justify-center bg-gray-50">
                <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
              </div>
              <span class="text-xs text-gray-400 mt-1">{{ images.length + i }}</span>
              <div class="text-xs mt-1">
                <span class="text-gray-400">Empty</span>
              </div>
            </div>
          </div>

          <!-- Upload Button -->
          <div class="flex flex-col items-center gap-2 mt-2">
            <button
              type="button"
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer text-sm"
              @click="uploadInput.click()"
            >
              Upload Images
            </button>
            <p class="text-sm text-gray-500">
              {{ activeImages.length }}/4 images
            </p>
          </div>

          <!-- Hidden Input -->
          <input
            id="file-upload"
            type="file"
            class="hidden"
            ref="uploadInput"
            multiple
            accept="image/*"
            @change="uploadImage"
          />
        </div>
      </div>

      <!-- Form Section -->
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