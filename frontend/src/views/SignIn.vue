<script setup>
import { useUserStore } from "@/stores/userStore";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const email = ref("");
const password = ref("");
const errorMessage = ref("");
const loading = ref(false);

const isDisabled = computed(() => {
  return (
    loading.value ||
    !email.value.trim() || // ป้องกัน email เป็น space
    !password.value // ป้องกัน password เป็น space
  );
});

const handleSignIn = async () => {
  loading.value = true;
  errorMessage.value = "";

  // ✅ FE validation
  if (!email.value || !password.value) {
    errorMessage.value = "Email or Password is not valid.";
    loading.value = false;
    return;
  }

  if (email.value.includes(" ")) {
    errorMessage.value = "Email or Password is not valid.";
    loading.value = false;
    return;
  }

  if (!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(email.value)) {
    errorMessage.value = "Email or Password is not valid.";
    loading.value = false;
    return;
  }

  const userStore = useUserStore();

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v2/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: email.value,
        password: password.value,
      }),
      credentials: "include",
    });

    if (res.status === 200) {
      const data = await res.json();
      if (data.access_token) {
        localStorage.setItem("access_token", data.access_token);
      }

      const headers = {
        "Content-Type": "application/json",
      };

      if (data.access_token) {
        headers["Authorization"] = `Bearer ${data.access_token}`;
      }

      const resUser = await fetch(`${import.meta.env.VITE_API_URL}/v2/users`, {
        method: "GET",
        headers,
      });
      
      const user = await resUser.json();
      
      userStore.setUserInfo(user);
      router.push("/sale-items");
    } else {
      const err = await res.json();
      errorMessage.value = err.error || "Something went wrong"; 
    }
  } catch (err) {
    errorMessage.value = "Server error";
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div
    class="max-w-md mx-auto mt-20 p-8 bg-white border border-gray-200 rounded-2xl shadow-lg"
  >
    <h1 class="text-2xl font-bold mb-6 text-center text-blue-700">Sign In</h1>

    <div class="mb-4">
      <label class="block mb-2 font-medium text-gray-700">Email</label>
      <input
        v-model="email"
        type="text"
        maxlength="50"
        class="border itbms-email px-3 py-2 rounded-lg w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
      />
    </div>

    <div class="mb-6">
      <label class="block mb-2 font-medium text-gray-700">Password</label>
      <input
        v-model="password"
        type="password"
        maxlength="14"
        class="border itbms-password px-3 py-2 rounded-lg w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
      />
    </div>

    <button
      class="w-full itbms-signin-button bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold px-4 py-2 rounded-lg shadow-md transition disabled:opacity-50 hover:from-blue-600 hover:to-indigo-700"
      :disabled="isDisabled"
      @click="handleSignIn"
    >
      {{ loading ? "Signing in..." : "Sign In" }}
    </button>

    <p v-if="errorMessage" class="text-red-500 text-center mt-4">
      {{ errorMessage }}
    </p>
  </div>
</template>
