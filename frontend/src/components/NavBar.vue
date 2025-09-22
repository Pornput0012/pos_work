<template>
  <header class="bg-blue-950 text-white px-6 py-4 shadow-md">
    <div class="container mx-auto flex justify-between items-center">
      <h1 class="text-2xl font-bold tracking-wide">ITB MShop</h1>
      <nav class="space-x-6 text-white text-sm font-medium">
        <router-link to="/" class="hover:text-gray-300 transition">Home</router-link>
        <router-link to="/sale-items" class="hover:text-gray-300 transition">Sale Items</router-link>
        <router-link to="/sale-items/list" class="hover:text-gray-300 transition">Sale Item List</router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
</script>

<style scoped></style>
