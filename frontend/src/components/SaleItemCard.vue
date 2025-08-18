<script setup>
defineProps({
  item: Object
})

</script>
<template>
    <router-link
      :to="`/sale-items/${item.id}`"
      class="itbms-row border rounded-lg p-4 space-y-2 bg-white hover:shadow-md"
    >
      <img src="../assets/Test.jpg" alt="phone" class="w-full rounded" />
      <div class="itbms-brand text-sm text-gray-600">{{ item.brandName }}</div>
      <div class="itbms-model font-semibold">{{ item.model }}</div>
      <div>
        <span class="itbms-ramGb">{{ item.ramGb ?? '-' }}</span>
        /
        <span class="itbms-storageGb">{{ item.storageGb ?? '-' }}</span>
        <span class="itbms-storageGb-unit">GB</span>
      </div>
      <div class="text-red-500"><span class="itbms-price">
        {{ item.price.toLocaleString() }}
      </span>
      <span class="itbms-price-unit"> Baht</span>
    </div>
    </router-link>
  </template>
  
