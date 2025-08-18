<script setup>
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
    console.warn("การส่งฟอร์มถูกบล็อก: ฟอร์มไม่ถูกต้อง");
    alert("กรุณากรอกข้อมูลให้ครบถ้วนและถูกต้อง");
    return;
  }

  const payload = {
    brand: {
      id: form.brandId,
    },
    model: form.model.trim(),
    price: form.price != null ? Number(form.price) : null,
    description: form.description.trim(),
    ramGb: form.ramGb != null ? Number(form.ramGb) : null,
    screenSizeInch:
      form.screenSizeInch != null ? Number(form.screenSizeInch) : null,
    storageGb: form.storageGb != null ? Number(form.storageGb) : null,
    color: form.color?.trim() || null,
    quantity: form.quantity != null ? Number(form.quantity) : null,
  };

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      const data = await res.json();
      emit("submit", data);
    } else {
      const errorText = await res.text();
      console.error("❌ ข้อผิดพลาดจากเซิร์ฟเวอร์:", errorText);
      alert(`เกิดข้อผิดพลาด (${res.status}): ${errorText}`);
    }
  } catch (err) {
    console.error("🚫 การร้องขอล้มเหลว:", err);
    alert("ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้");
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
