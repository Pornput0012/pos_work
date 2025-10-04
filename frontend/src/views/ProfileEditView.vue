<script setup>
import { useUserStore } from "@/stores/userStore";
import { computed, onMounted, ref, watch, watchEffect } from "vue";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const userInfo = computed(() => {
  return userStore.getUserInfo();
});

const userInfoLocal = ref(null);
const router = useRouter();
watchEffect(async () => {
  try {
    if (userInfo.value) {
      const headers = {
        "Content-Type": "application/json",
      };
      const accessToken = localStorage.getItem("access_token");
      if (accessToken) {
        headers["Authorization"] = `Bearer ${accessToken}`;
      }

      const res = await fetch(
        `${import.meta.env.VITE_API_URL}/v2/users/${userInfo.value.id}`,
        {
          method: "GET",
          headers,
        }
      );
      if (res.ok) {
        const data = await res.json();
        userInfoLocal.value = data;
        console.log(data);
        // buyer
        // {
        //   email: "pornput.sood@mail.kmutt.ac.th";
        //   fullName: "Pornput";
        //   id: 1;
        //   nickName: "Most";
        //   userType: "Buyer";
        // }

        //seller
        // {
        //   backCardPhotoPath: "uploads/cards/1759507883458_ภาพถ่ายหน้าจอ 2568-09-26 เวลา 14.18.02.png";
        //   bankAccount: "1111111110222";
        //   bankName: "Bangkok Bank";
        //   cardNo: "0841234567";
        //   email: "ananya.husen@mail.kmutt.ac.th";
        //   frontCardPhotoPath: "uploads/cards/1759507883455_ภาพถ่ายหน้าจอ 2568-10-01 เวลา 21.51.01.png";
        //   fullName: "ananya";
        //   id: 2;
        //   nickName: "fern";
        //   userType: "Seller";
        // }
        return;
      }
      throw new Error("fetch User Fail");
    }
  } catch (error) {
    console.log(error);
  }
});

// Form data and validation
const userBody = ref({
  nickName: "",
  fullName: "",
});

const originalData = ref({
  nickName: "",
  fullName: "",
});

const errors = ref({
  nickName: "",
  fullName: "",
});

// Watch for userInfoLocal changes to populate form
watchEffect(() => {
  if (userInfoLocal.value) {
    userBody.value = {
      nickName: userInfoLocal.value.nickName || "",
      fullName: userInfoLocal.value.fullName || "",
    };
    originalData.value = {
      nickName: userInfoLocal.value.nickName || "",
      fullName: userInfoLocal.value.fullName || "",
    };
  }
});

// Validation functions
const validateNickName = (value) => {
  if (!value || value.trim() === "") {
    return "ชื่อเล่นเป็นข้อมูลที่จำเป็น";
  }
  return "";
};

const validateFullName = (value) => {
  if (!value || value.trim() === "") {
    return "ชื่อ-นามสกุลเป็นข้อมูลที่จำเป็น";
  }
  if (value.trim().length < 4) {
    return "ชื่อ-นามสกุลต้องมีอย่างน้อย 4 ตัวอักษร";
  }
  if (value.trim().length > 40) {
    return "ชื่อ-นามสกุลต้องไม่เกิน 40 ตัวอักษร";
  }
  return "";
};

// Validate all fields
const validateForm = () => {
  errors.value.nickName = validateNickName(userBody.value.nickName);
  errors.value.fullName = validateFullName(userBody.value.fullName);

  return !errors.value.nickName && !errors.value.fullName;
};

// Check if form has been modified
const isFormModified = computed(() => {
  return (
    userBody.value.nickName !== originalData.value.nickName ||
    userBody.value.fullName !== originalData.value.fullName
  );
});

// Check if save button should be enabled
const isSaveEnabled = computed(() => {
  const isValid = validateForm();
  const isModified = isFormModified.value;
  return isValid && isModified;
});

// Handle input changes with validation
const handleNickNameChange = (event) => {
  userBody.value.nickName = event.target.value;
  errors.value.nickName = validateNickName(event.target.value);
};

const handleFullNameChange = (event) => {
  userBody.value.fullName = event.target.value;
  errors.value.fullName = validateFullName(event.target.value);
};

const handleUpdateUser = async () => {
  if (!isSaveEnabled.value) return;

  try {
    const headers = {
      "Content-Type": "application/json",
    };
    const accessToken = localStorage.getItem("access_token");
    if (accessToken) {
      headers["Authorization"] = `Bearer ${accessToken}`;
    }

    const res = await fetch(
      `${import.meta.env.VITE_API_URL}/v2/users/${userInfo.value.id}`,
      {
        method: "PUT",
        headers,
        body: JSON.stringify({
          nickName: userBody.value.nickName.trim(),
          fullName: userBody.value.fullName.trim(),
        }),
      }
    );

    if (res.ok) {
      originalData.value = { ...userBody.value };
      localStorage.setItem("noti_success_edit_profile", "true");
      router.push({ name: "profile" });
    } else {
      throw new Error("Failed to update user");
    }
  } catch (err) {
    console.error("Error updating user:", err);
    alert("เกิดข้อผิดพลาดในการบันทึกข้อมูล");
  }
};

// Function to mask sensitive data (show last 3 digits before the last character)
const maskData = (data) => {
  if (!data) return "";
  const str = data.toString();
  if (str.length <= 4) return str;

  const lastChar = str.slice(-1);
  const threeDigits = str.slice(-4, -1);
  const maskedPart = "x".repeat(str.length - 4);

  return maskedPart + threeDigits + "x";
};

// Function to get image URL
const getImageUrl = (path) => {
  if (!path) return "";
  const filename = path.split("/").pop();
  return `${import.meta.env.VITE_API_URL}/v2/users/images/${filename}`;
};
</script>

<template>
  <div
    class="min-h-screen bg-gradient-to-br from-purple-100 via-pink-50 to-indigo-100 py-12 px-4 relative overflow-hidden"
  >
    <!-- Animated Background Elements -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div
        class="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-br from-pink-300/20 to-purple-300/20 rounded-full blur-3xl animate-pulse"
      ></div>
      <div
        class="absolute -bottom-40 -left-40 w-80 h-80 bg-gradient-to-tr from-blue-300/20 to-teal-300/20 rounded-full blur-3xl animate-pulse delay-1000"
      ></div>
      <div
        class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-gradient-to-r from-yellow-200/10 to-orange-200/10 rounded-full blur-3xl animate-pulse delay-2000"
      ></div>
    </div>

    <div class="max-w-5xl mx-auto relative z-10">
      <!-- Loading State -->
      <div v-if="!userInfoLocal" class="flex justify-center items-center h-96">
        <div class="text-center">
          <div class="relative">
            <span
              class="loading loading-spinner loading-lg text-transparent bg-gradient-to-r from-purple-500 to-pink-500 bg-clip-text"
            ></span>
            <div
              class="absolute inset-0 loading loading-spinner loading-lg text-purple-300 animate-pulse"
            ></div>
          </div>
          <p class="mt-4 text-gray-600 font-medium">กำลังโหลดข้อมูล...</p>
        </div>
      </div>

      <!-- User Profile Card -->
      <div v-else class="space-y-8">
        <!-- Header Card with Rainbow Gradient -->
        <div
          class="bg-gradient-to-r from-violet-500 via-purple-500 to-pink-500 rounded-3xl p-8 transform hover:scale-[1.02] transition-all duration-300 relative overflow-hidden"
        >
          <!-- Animated Background Pattern -->
          <div
            class="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent -skew-x-12 transform translate-x-full animate-shimmer"
          ></div>

          <div class="relative z-10">
            <div class="flex flex-col md:flex-row items-center gap-8">
              <div class="avatar placeholder relative">
                <!-- Animated Ring -->
                <div
                  class="absolute inset-0 rounded-full bg-gradient-to-r from-yellow-400 via-pink-400 to-purple-400 animate-spin p-1"
                  style="animation-duration: 3s"
                ></div>
                <div
                  class="bg-white text-transparent bg-gradient-to-r from-violet-600 to-pink-600 bg-clip-text rounded-full w-28 h-28 flex items-center justify-center relative z-10"
                >
                  <span class="text-5xl font-bold">{{
                    userInfoLocal.nickName?.charAt(0).toUpperCase()
                  }}</span>
                </div>
              </div>
              <div class="text-center md:text-left flex-1">
                <div
                  class="flex flex-col md:flex-row md:items-center md:justify-between mb-4"
                >
                  <h1 class="text-xl font-bold text-white mb-4 md:mb-0">
                    แก้ไขโปรไฟล์
                  </h1>
                </div>
                <div
                  class="flex flex-col md:flex-row md:items-center md:justify-between mb-4"
                >
                  <h1 class="text-5xl font-bold text-white mb-4 md:mb-0">
                    {{ userInfoLocal.nickName }}
                  </h1>
                </div>
                <div
                  class="flex items-center gap-3 justify-center md:justify-start flex-wrap"
                >
                  <div
                    class="badge itbms-type badge-lg bg-gradient-to-r from-yellow-400 to-orange-400 text-white border-0 px-4 py-2 rounded-full"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-4 w-4 mr-2"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                    {{ userInfoLocal.userType }}
                  </div>
                  <div
                    class="badge badge-lg bg-gradient-to-r from-emerald-400 to-teal-400 text-white border-0 px-4 py-2 rounded-full"
                  >
                    ✨ Verified
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Basic Information Card -->
        <div
          class="bg-gradient-to-br from-white to-blue-50 rounded-3xl p-8 transition-all duration-300 border border-blue-100 hover:border-blue-200"
        >
          <div class="mb-8">
            <div class="flex items-center gap-4 mb-6">
              <div
                class="p-4 bg-gradient-to-br from-blue-400 to-cyan-500 rounded-2xl"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-8 w-8 text-white"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <div>
                <h2
                  class="text-4xl font-bold bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent"
                >
                  ข้อมูลทั่วไป
                </h2>
                <p class="text-base text-gray-500 mt-1">
                  ข้อมูลพื้นฐานของผู้ใช้
                </p>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div class="form-control group">
                <label class="label pb-3">
                  <span
                    class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-purple-600 transition-colors"
                  >
                    <div
                      class="p-3 bg-gradient-to-br from-purple-400 to-pink-400 rounded-xl"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-white"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                        />
                      </svg>
                    </div>
                    ชื่อเล่น
                  </span>
                </label>
                <input
                  type="text"
                  v-model="userBody.nickName"
                  @input="handleNickNameChange"
                  :class="[
                    'input itbms-nickname bg-gradient-to-r from-purple-50 to-pink-50 border-2 focus:bg-white transition-all duration-300 text-xl font-medium py-4 px-6 rounded-2xl',
                    errors.nickName
                      ? 'border-red-400 focus:border-red-500'
                      : 'border-purple-200 focus:border-purple-400',
                  ]"
                  placeholder="กรุณากรอกชื่อเล่น"
                />
                <div
                  v-if="errors.nickName"
                  class="text-red-500 text-sm mt-1 ml-2"
                >
                  {{ errors.nickName }}
                </div>
              </div>

              <div class="form-control group">
                <label class="label pb-3">
                  <span
                    class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-emerald-600 transition-colors"
                  >
                    <div
                      class="p-3 bg-gradient-to-br from-emerald-400 to-teal-400 rounded-xl"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-white"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                        />
                      </svg>
                    </div>
                    อีเมล
                  </span>
                </label>
                <input
                  type="email"
                  :value="userInfoLocal.email"
                  class="input itbms-email bg-gradient-to-r from-emerald-50 to-teal-50 border-2 border-emerald-200 focus:border-emerald-400 focus:bg-white transition-all duration-300 text-xl font-medium py-4 px-6 rounded-2xl opacity-60 cursor-not-allowed"
                  readonly
                  disabled
                />
                <div class="text-gray-500 text-sm mt-1 ml-2">
                  อีเมลไม่สามารถแก้ไขได้
                </div>
              </div>

              <div class="form-control md:col-span-2 group">
                <label class="label pb-3">
                  <span
                    class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-orange-600 transition-colors"
                  >
                    <div
                      class="p-3 bg-gradient-to-br from-orange-400 to-red-400 rounded-xl"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-white"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                        />
                      </svg>
                    </div>
                    ชื่อ-นามสกุล
                  </span>
                </label>
                <input
                  type="text"
                  v-model="userBody.fullName"
                  @input="handleFullNameChange"
                  :class="[
                    'input bg-gradient-to-r itbms-fullname from-orange-50 to-red-50 border-2 focus:bg-white transition-all duration-300 text-xl font-medium py-4 px-6 rounded-2xl',
                    errors.fullName
                      ? 'border-red-400 focus:border-red-500'
                      : 'border-orange-200 focus:border-orange-400',
                  ]"
                  placeholder="กรุณากรอกชื่อ-นามสกุล"
                  maxlength="40"
                />
                <div
                  v-if="errors.fullName"
                  class="text-red-500 text-sm mt-1 ml-2"
                >
                  {{ errors.fullName }}
                </div>
                <div class="text-gray-500 text-sm mt-1 ml-2">
                  {{ userBody.fullName.length }}/40 ตัวอักษร (ต้องมีอย่างน้อย 4
                  ตัวอักษร)
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Seller Additional Information -->
        <div v-if="userInfoLocal.userType === 'Seller'" class="space-y-6">
          <!-- Banking Information Card -->
          <div
            class="bg-gradient-to-br from-white to-green-50 rounded-3xl p-8 transition-all duration-300 border border-green-100 hover:border-green-200"
          >
            <div class="mb-8">
              <div class="flex items-center gap-4 mb-6">
                <div
                  class="p-4 bg-gradient-to-br from-green-400 to-emerald-500 rounded-2xl"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-8 w-8 text-white"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"
                    />
                  </svg>
                </div>
                <div>
                  <h2
                    class="text-4xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent"
                  >
                    ข้อมูลผู้ขาย
                  </h2>
                  <p class="text-base text-gray-500 mt-1">
                    ข้อมูลธนาคารและการติดต่อ
                  </p>
                </div>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div class="form-control group">
                  <label class="label pb-3">
                    <span
                      class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-pink-600 transition-colors"
                    >
                      <div
                        class="p-3 bg-gradient-to-br from-pink-400 to-rose-400 rounded-xl"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-5 w-5 text-white"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                          />
                        </svg>
                      </div>
                      เบอร์โทรศัพท์
                    </span>
                  </label>
                  <div class="relative">
                    <input
                      type="text"
                      :value="maskData(userInfoLocal.mobile)"
                      class="input itbms-mobile bg-gradient-to-r from-pink-50 to-rose-50 border-2 border-pink-200 focus:border-pink-400 focus:bg-white font-mono text-xl tracking-wider transition-all duration-300 pr-16 py-4 px-6 rounded-2xl opacity-60 cursor-not-allowed"
                      readonly
                      disabled
                    />
                    <div class="text-gray-500 text-sm mt-1 ml-2">
                      เบอร์โทรศัพท์ไม่สามารถแก้ไขได้
                    </div>
                    <div
                      class="absolute right-4 top-1/2 -translate-y-1/2 p-2 bg-gradient-to-r from-pink-400 to-rose-400 rounded-xl"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-white"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                        />
                      </svg>
                    </div>
                  </div>
                </div>

                <div class="form-control group">
                  <label class="label pb-3">
                    <span
                      class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-indigo-600 transition-colors"
                    >
                      <div
                        class="p-3 bg-gradient-to-br from-indigo-400 to-blue-400 rounded-xl"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-5 w-5 text-white"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z"
                          />
                        </svg>
                      </div>
                      ธนาคาร
                    </span>
                  </label>
                  <input
                    type="text"
                    :value="userInfoLocal.bankName"
                    class="input itbms-bankName bg-gradient-to-r from-indigo-50 to-blue-50 border-2 border-indigo-200 focus:border-indigo-400 focus:bg-white transition-all duration-300 text-xl font-medium py-4 px-6 rounded-2xl opacity-60 cursor-not-allowed"
                    readonly
                    disabled
                  />
                  <div class="text-gray-500 text-sm mt-1 ml-2">
                    ธนาคารไม่สามารถแก้ไขได้
                  </div>
                </div>

                <div class="form-control group">
                  <label class="label pb-3">
                    <span
                      class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-yellow-600 transition-colors"
                    >
                      <div
                        class="p-3 bg-gradient-to-br from-yellow-400 to-amber-400 rounded-xl"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-5 w-5 text-white"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"
                          />
                        </svg>
                      </div>
                      เลขบัญชีธนาคาร
                    </span>
                  </label>
                  <div class="relative">
                    <input
                      type="text"
                      :value="maskData(userInfoLocal.bankAccount)"
                      class="input itbms-bankAccount bg-gradient-to-r from-yellow-50 to-amber-50 border-2 border-yellow-200 focus:border-yellow-400 focus:bg-white font-mono text-xl tracking-wider transition-all duration-300 pr-16 py-4 px-6 rounded-2xl opacity-60 cursor-not-allowed"
                      readonly
                      disabled
                    />
                    <div class="text-gray-500 text-sm mt-1 ml-2">
                      เลขบัญชีธนาคารไม่สามารถแก้ไขได้
                    </div>
                    <div
                      class="absolute right-4 top-1/2 -translate-y-1/2 p-2 bg-gradient-to-r from-yellow-400 to-amber-400 rounded-xl"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-white"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                        />
                      </svg>
                    </div>
                  </div>
                </div>

                <div class="form-control group">
                  <label class="label pb-3">
                    <span
                      class="label-text font-semibold text-lg flex items-center gap-3 text-gray-700 group-hover:text-red-600 transition-colors"
                    >
                      <div
                        class="p-3 bg-gradient-to-br from-red-400 to-pink-400 rounded-xl"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-5 w-5 text-white"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M10 6H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V8a2 2 0 00-2-2h-5m-4 0V5a2 2 0 114 0v1m-4 0a2 2 0 104 0m-5 8a2 2 0 100-4 2 2 0 000 4zm0 0c1.306 0 2.417.835 2.83 2M9 14a3.001 3.001 0 00-2.83 2M15 11h3m-3 4h2"
                          />
                        </svg>
                      </div>
                      หมายเลขบัตร
                    </span>
                  </label>
                  <div class="relative">
                    <input
                      type="text"
                      :value="maskData(userInfoLocal.cardNo)"
                      class="input bg-gradient-to-r from-red-50 to-pink-50 border-2 border-red-200 focus:border-red-400 focus:bg-white font-mono text-xl tracking-wider transition-all duration-300 pr-16 py-4 px-6 rounded-2xl opacity-60 cursor-not-allowed"
                      readonly
                      disabled
                    />
                    <div class="text-gray-500 text-sm mt-1 ml-2">
                      หมายเลขบัตรไม่สามารถแก้ไขได้
                    </div>
                    <div
                      class="absolute right-4 top-1/2 -translate-y-1/2 p-2 bg-gradient-to-r from-red-400 to-pink-400 rounded-xl"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-white"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                        />
                      </svg>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Card Photos -->
          <div
            class="bg-gradient-to-br from-white to-purple-50 rounded-3xl p-8 transition-all duration-300 border border-purple-100 hover:border-purple-200"
          >
            <div class="mb-8">
              <div class="flex items-center gap-4 mb-6">
                <div
                  class="p-4 bg-gradient-to-br from-purple-400 to-pink-500 rounded-2xl"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-8 w-8 text-white"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"
                    />
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15 13a3 3 0 11-6 0 3 3 0 616 0z"
                    />
                  </svg>
                </div>
                <div>
                  <h2
                    class="text-4xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
                  >
                    รูปบัตรประชาชน
                  </h2>
                  <p class="text-base text-gray-500 mt-1">เอกสารยืนยันตัวตน</p>
                </div>
              </div>

              <div class="grid grid-cols-1 lg:grid-cols-2 gap-10">
                <!-- Front Card Photo -->
                <div v-if="userInfoLocal.frontCardPhotoPath" class="group">
                  <label class="label pb-4">
                    <span
                      class="label-text font-semibold text-lg flex items-center gap-3"
                    >
                      <div
                        class="badge bg-gradient-to-r from-emerald-400 to-teal-400 text-white border-0 px-3 py-2 rounded-full"
                      >
                        <span class="text-base font-bold">1</span>
                      </div>
                      ด้านหน้า
                    </span>
                  </label>
                  <figure
                    class="relative border-4 border-emerald-200 rounded-3xl overflow-hidden transition-all duration-500 group-hover:border-emerald-300 p-2 bg-gradient-to-br from-emerald-50 to-teal-50"
                  >
                    <div class="rounded-2xl overflow-hidden">
                      <img
                        :src="getImageUrl(userInfoLocal.frontCardPhotoPath)"
                        alt="Front Card"
                        class="w-full h-80 object-cover group-hover:scale-105 transition-transform duration-500"
                      />
                      <div
                        class="absolute inset-2 bg-gradient-to-t from-emerald-600/30 via-transparent to-teal-400/20 opacity-0 group-hover:opacity-100 transition-opacity duration-500 rounded-2xl"
                      ></div>
                      <div
                        class="absolute bottom-6 left-6 right-6 text-white opacity-0 group-hover:opacity-100 transition-opacity duration-500"
                      >
                        <div
                          class="bg-black/60 backdrop-blur-sm rounded-2xl p-4"
                        >
                          <p class="text-base font-semibold">
                            บัตรประชาชนด้านหน้า
                          </p>
                        </div>
                      </div>
                    </div>
                  </figure>
                </div>

                <!-- Back Card Photo -->
                <div v-if="userInfoLocal.backCardPhotoPath" class="group">
                  <label class="label pb-4">
                    <span
                      class="label-text font-semibold text-lg flex items-center gap-3"
                    >
                      <div
                        class="badge bg-gradient-to-r from-rose-400 to-pink-400 text-white border-0 px-3 py-2 rounded-full"
                      >
                        <span class="text-base font-bold">2</span>
                      </div>
                      ด้านหลัง
                    </span>
                  </label>
                  <figure
                    class="relative border-4 border-rose-200 rounded-3xl overflow-hidden transition-all duration-500 group-hover:border-rose-300 p-2 bg-gradient-to-br from-rose-50 to-pink-50"
                  >
                    <div class="rounded-2xl overflow-hidden">
                      <img
                        :src="getImageUrl(userInfoLocal.backCardPhotoPath)"
                        alt="Back Card"
                        class="w-full h-80 object-cover group-hover:scale-105 transition-transform duration-500"
                      />
                      <div
                        class="absolute inset-2 bg-gradient-to-t from-rose-600/30 via-transparent to-pink-400/20 opacity-0 group-hover:opacity-100 transition-opacity duration-500 rounded-2xl"
                      ></div>
                      <div
                        class="absolute bottom-6 left-6 right-6 text-white opacity-0 group-hover:opacity-100 transition-opacity duration-500"
                      >
                        <div
                          class="bg-black/60 backdrop-blur-sm rounded-2xl p-4"
                        >
                          <p class="text-base font-semibold">
                            บัตรประชาชนด้านหลัง
                          </p>
                        </div>
                      </div>
                    </div>
                  </figure>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div
          class="bg-gradient-to-br from-white to-gray-50 rounded-3xl p-8 transition-all duration-300 border border-gray-100"
        >
          <div class="flex flex-col sm:flex-row gap-4 justify-end">
            <button
              @click="$router.go(-1)"
              class="btn itbms-cancel-button bg-gradient-to-r from-gray-400 to-gray-500 hover:from-gray-500 hover:to-gray-600 text-white border-0 px-8 py-3 rounded-2xl font-semibold transition-all duration-300 flex items-center gap-2"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
              ยกเลิก
            </button>

            <button
              @click="handleUpdateUser"
              :disabled="!isSaveEnabled"
              :class="[
                'itbms-save-button btn border-0 px-8 py-3 rounded-2xl font-semibold transition-all duration-300 flex items-center gap-2',
                isSaveEnabled
                  ? 'bg-gradient-to-r from-green-400 to-emerald-500 hover:from-green-500 hover:to-emerald-600 text-white'
                  : 'bg-gray-300 text-gray-500 cursor-not-allowed opacity-60',
              ]"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
              บันทึก
            </button>
          </div>

          <!-- Save Status Indicator -->
          <div v-if="isFormModified" class="mt-4 text-center">
            <div
              class="text-sm text-amber-600 bg-amber-50 rounded-lg p-3 border border-amber-200"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4 inline mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"
                />
              </svg>
              มีการเปลี่ยนแปลงข้อมูล กรุณาบันทึกเพื่อยืนยันการแก้ไข
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes shimmer {
  0% {
    transform: translateX(-100%) skewX(-12deg);
  }
  100% {
    transform: translateX(200%) skewX(-12deg);
  }
}

@keyframes gradient-shift {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-shimmer {
  animation: shimmer 2s infinite;
}

.animate-gradient {
  background-size: 200% 200%;
  animation: gradient-shift 4s ease infinite;
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

/* Custom gradient borders */
.border-gradient-to-r {
  background: linear-gradient(to right, var(--tw-gradient-stops));
  background-clip: border-box;
}

/* Hover effects */
.group:hover .group-hover\:scale-110 {
  transform: scale(1.1);
}

.group:hover .group-hover\:border-emerald-300 {
  border-color: rgb(110 231 183);
}

.group:hover .group-hover\:border-rose-300 {
  border-color: rgb(253 164 175);
}

/* Smooth transitions */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

/* Custom backdrop blur */
.backdrop-blur-sm {
  backdrop-filter: blur(4px);
}

/* Enhanced shadow effects */
.shadow-glow {
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.3);
}

.shadow-glow-pink {
  box-shadow: 0 0 20px rgba(236, 72, 153, 0.3);
}

.shadow-glow-green {
  box-shadow: 0 0 20px rgba(34, 197, 94, 0.3);
}
</style>
