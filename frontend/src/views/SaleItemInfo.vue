<script setup>
  const props = defineProps({
    item: Object
  })
  
  const placeholder = '—' // dash character
  
  function formatPrice(price) {
    return price ? `${price.toFixed(2)} ฿` : placeholder
  }
  
  function formatWithUnit(value, unit) {
    if (value === null || value === undefined || value === '') {
      return placeholder
    }
    return `${value} ${unit}`
  }
  </script>
  
<template>
    <div class="space-y-4">
      <div><strong>Model:</strong> {{ item.model || placeholder }}</div>
      <div><strong>Brand:</strong> {{ item.brandName || placeholder }}</div>
      <div><strong>Description:</strong> {{ item.description || placeholder }}</div>
      <div><strong>Price:</strong> {{ formatPrice(item.price) }}</div>
      <div><strong>RAM:</strong> {{ formatWithUnit(item.ramGb, 'GB') }}</div>
      <div><strong>Storage:</strong> {{ formatWithUnit(item.storageGb, 'GB') }}</div>
      <div><strong>Screen Size:</strong> {{ formatWithUnit(item.screenSizeInch, 'inches') }}</div>
      <div><strong>Quantity:</strong> {{ item.quantity ?? placeholder }}</div>
      <div><strong>Color:</strong> {{ item.color || placeholder }}</div>
    </div>
  </template>

<style scoped>
  div:has(strong) {
    display: flex;
    gap: 0.5rem;
  }
  </style>
  