<template>
  <button
    class="itbms-edit-button bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded font-semibold"
    @click="handleClick"
  >
    <slot>Edit</slot>
  </button>
</template>

<script setup>
const emit = defineEmits(['click'])
const handleClick = () => emit('click')
</script>
