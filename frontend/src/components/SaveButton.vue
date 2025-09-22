<template>
  <button
    :disabled="disabled"
    class="itbms-save-button px-4 py-2 rounded text-white font-semibold"
    :class="disabled ? 'bg-green-300 cursor-not-allowed' : 'bg-green-600 hover:bg-green-700'"
    @click="handleClick"
  >
    <slot>Save</slot>
  </button>
</template>

<script setup>
const emit = defineEmits(['click'])
const props = defineProps({
  disabled: Boolean
})
const handleClick = () => emit('click')
</script>
