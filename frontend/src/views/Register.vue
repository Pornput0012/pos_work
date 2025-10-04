<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const successMessage = ref(""); 
const showMessage = ref(false); 

const accountType = ref("");

const form = ref({
  nickname: "",
  fullname: "",
  email: "",
  password: "",
  confirmPassword: "",
});


// Seller specific fields
const sellerForm = ref({
  mobile: "",
  bankAccountNo: "",
  bankName: "",
  cardNo: "",
  frontCardPhoto: null,
  backCardPhoto: null,
});

// Preview images
const frontPreview = ref(null);
const backPreview = ref(null);

// Enable Save button based on account type & required fields
const isFormValid = computed(() => {
  if (!accountType.value) return false;

  const commonValid =
    form.value.nickname.trim() &&
    form.value.fullname.trim() &&
    form.value.email.trim() &&
    form.value.password.trim() &&
    form.value.confirmPassword.trim() &&
    form.value.password === form.value.confirmPassword;

  if (accountType.value === "Buyer") {
    return commonValid;
  } else if (accountType.value === "Seller") {
    const sellerValid =
      sellerForm.value.mobile.trim() &&
      sellerForm.value.bankAccountNo.trim() &&
      sellerForm.value.bankName.trim() &&
      sellerForm.value.cardNo.trim() &&
      sellerForm.value.frontCardPhoto &&
      sellerForm.value.backCardPhoto;
    return commonValid && sellerValid;
  }
  return false;
});

// Handle image upload
const handleFileChange = (event, type) => {
  const file = event.target.files[0];
  if (!file) return;
  if (type === "front") {
    sellerForm.value.frontCardPhoto = file;
    frontPreview.value = URL.createObjectURL(file);
  } else {
    sellerForm.value.backCardPhoto = file;
    backPreview.value = URL.createObjectURL(file);
  }
};

const handleSave = async () => {
  try {
    const payload = {
        accountType: accountType.value,
        nickname: form.value.nickname.trim(),
        fullname: form.value.fullname.trim(),
        email: form.value.email.trim(),
        password: form.value.password.trim(),
        confirmPassword: form.value.confirmPassword.trim(),
    };


        if (accountType.value === "Seller") {
        Object.assign(payload, {
        mobile: sellerForm.value.mobile.trim(),
        bankAccountNo: sellerForm.value.bankAccountNo.trim(),
        bankName: sellerForm.value.bankName.trim(),
        cardNo: sellerForm.value.cardNo.trim(),
    });
    }


    const formData = new FormData();
    for (const key in payload) {
      formData.append(key, payload[key]);
    }

    if (accountType.value === "Seller") {
      formData.append("frontCardPhoto", sellerForm.value.frontCardPhoto);
      formData.append("backCardPhoto", sellerForm.value.backCardPhoto);
    }

    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/registers`, {
      method: "POST",
      body: formData,
    });

    if (res.status === 201) {
      successMessage.value = "The user account has been successfully registered.";
      showMessage.value = true;

      setTimeout(() => {
        router.push("/sale-items");
      }, 2000);
    } else {
      successMessage.value = "Registration failed.";
      showMessage.value = true;
    }
  } catch (err) {
    console.error(err);
    successMessage.value = "Error while registering.";
    showMessage.value = true;
  }
};
</script>

<template>
  <div class="max-w-lg mx-auto mt-10 p-8 bg-white border border-gray-200 rounded-2xl shadow-lg">
    <h1 class="text-2xl font-bold mb-6 text-center text-blue-700">
      Register Account
    </h1>

    <!-- Account Type -->
    <label class="block mb-2 font-medium text-gray-700">Account Type</label>
    <select
      v-model="accountType"
      class="border px-3 py-2 rounded-lg w-full mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
    >
      <option value="">-- Select --</option>
      <option value="Buyer">Buyer</option>
      <option value="Seller">Seller</option>
    </select>

    <!-- Common Form Fields -->
    <input
      v-model="form.nickname"
      type="text"
      placeholder="Nickname"
      class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
      v-model="form.fullname"
      type="text"
      placeholder="Fullname"
      class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
      v-model="form.email"
      type="email"
      placeholder="Email"
      class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
      v-model="form.password"
      type="password"
      placeholder="Password"
      class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
      v-model="form.confirmPassword"
      type="password"
      placeholder="Confirm Password"
      class="border px-3 py-2 rounded-lg w-full mb-6 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />

    <!-- Seller Only Fields -->
    <div v-if="accountType === 'Seller'">
    <input
        v-model="sellerForm.mobile"
        type="text"
        placeholder="Mobile"
        class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
        v-model="sellerForm.bankAccountNo"
        type="text"
        placeholder="Bank Account No"
        class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
        v-model="sellerForm.bankName"
        type="text"
        placeholder="Bank Name"
        class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
    <input
        v-model="sellerForm.cardNo"
        type="text"
        placeholder="Card No"
        class="border px-3 py-2 rounded-lg w-full mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
    />

    <div class="mb-3">
        <label class="block mb-1 font-medium text-gray-700">Front-side National Card Photo</label>
        <input type="file" @change="e => handleFileChange(e, 'front')" />
        <img v-if="frontPreview" :src="frontPreview" class="mt-2 max-h-32" />
    </div>
    <div class="mb-6">
        <label class="block mb-1 font-medium text-gray-700">Back-side National Card Photo</label>
        <input type="file" @change="e => handleFileChange(e, 'back')" />
        <img v-if="backPreview" :src="backPreview" class="mt-2 max-h-32" />
    </div>
    </div>


    <!-- Save Button -->
    <button
      class="w-full bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold px-4 py-2 rounded-lg shadow-md transition disabled:opacity-50 hover:from-blue-600 hover:to-indigo-700"
      :disabled="!isFormValid"
      @click="handleSave"
    >
      Save
    </button>

    <!-- Modal / Popup -->
    <div
      v-if="showMessage"
      class="fixed inset-0 bg-black/30 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 max-w-sm w-full text-center shadow-lg">
        <p class="text-lg font-medium">{{ successMessage }}</p>
        <button
          class="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          @click="showMessage = false"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>
