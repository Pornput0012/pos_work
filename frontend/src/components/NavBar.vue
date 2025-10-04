<script setup>
import { useUserStore } from "@/stores/userStore";
import { onMounted } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const userStore = useUserStore();
const handleLogout = async () => {
  try {
    const access_token = localStorage.getItem("access_token");
    const headers = {
      "Content-Type": "application/json",
    };
    if (access_token) {
      headers["Authorization"] = `Bearer ${access_token}`;
    }
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v2/auth/logout`, {
      method: "POST",
      headers,
      credentials: "include",
    });

    if (res.ok) {
      userStore.setUserInfo(null);
      localStorage.removeItem("access_token");
      router.push("/sale-items");
    }
  } catch (err) {
    console.error(err);
  }
};
onMounted(async () => {
  try {
    const headers = {
      "Content-Type": "application/json",
    };

    const accessToken = localStorage.getItem("access_token");

    if (accessToken) {
      headers["Authorization"] = `Bearer ${accessToken}`;
    }

    const resUser = await fetch(`${import.meta.env.VITE_API_URL}/v2/users`, {
      method: "GET",
      headers,
    });
    if (resUser.ok) {
      const user = await resUser.json();
      userStore.setUserInfo(user);
    } else {
      const tryUseRefreshToken = await fetch(
        `${import.meta.env.VITE_API_URL}/v2/auth/refresh`,
        {
          method: "POST",
          credentials: "include",
        }
      );

      if (tryUseRefreshToken.ok) {
        const userAccessToken = await tryUseRefreshToken.json();
        if (userAccessToken.access_token) {
          localStorage.setItem("access_token", userAccessToken.access_token);
          const headers = {
            "Content-Type": "application/json",
          };

          headers["Authorization"] = `Bearer ${userAccessToken.access_token}`;

          const resUserRound2 = await fetch(
            `${import.meta.env.VITE_API_URL}/v2/users`,
            {
              method: "GET",
              headers,
            }
          );
          if (resUserRound2.ok) {
            const userRound2 = await resUserRound2.json();
            userStore.setUserInfo(userRound2);
          }
        }
      }
    }
  } catch (err) {
    console.log(err);
  }
});
</script>

<template>
  <header class="bg-blue-950 text-white px-6 py-4 shadow-md">
    <div class="container mx-auto flex justify-between items-center">
      <h1 class="text-2xl font-bold tracking-wide">ITB MShop</h1>
      <nav class="space-x-6 text-white text-sm font-medium flex flex-wrap">
        <router-link to="/" class="hover:text-gray-300 transition"
          >Home</router-link
        >
        <router-link to="/sale-items" class="hover:text-gray-300 transition"
          >Sale Items</router-link
        >
        <router-link
          to="/sale-items/list"
          class="hover:text-gray-300 transition"
          >Sale Item List</router-link
        >

        <router-link
          v-if="!userStore.getUserInfo()"
          to="/register"
          class="hover:text-gray-300 transition"
          >Register</router-link
        >
        <router-link
          v-if="!userStore.getUserInfo()"
          to="/signin"
          class="hover:text-gray-300 transition"
          >Sign In</router-link
        >
        <div v-else class="flex gap-4">
          <p class="text-green-400">
            Hello,
            <span class="itbms-nickname">
              {{ userStore.getUserInfo().nickName }}
            </span>
          </p>
          <router-link :to="{ name: 'profile' }" class="itbms-profile"
            >profile</router-link
          >
          <button @click="handleLogout" class="itbms-logout">Logout</button>
        </div>
      </nav>
    </div>
  </header>
</template>

<style scoped></style>
