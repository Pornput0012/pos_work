<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const message = ref("Verifying your email...");
const loading = ref(true);

onMounted(async () => {
  const token = route.query.token;
  if (!token) {
    message.value = "Invalid verification link.";
    loading.value = false;
    return;
  }

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v2/users/verify-email`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token }),
    });

    if (res.status === 200) {
      message.value = "Your account has been successfully activated.";
      setTimeout(() => router.push("/sale-items"), 2000);
    } else {
      message.value = "Verification failed. Please try again.";
    }
  } catch (err) {
    console.error(err);
    message.value = "Error occurred while verifying.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="flex items-center justify-center h-screen bg-gray-50">
    <div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full text-center">
      <p class="text-lg font-semibold" :class="loading ? 'text-blue-600' : 'text-green-600'">
        {{ message }}
      </p>
    </div>
  </div>
</template>
