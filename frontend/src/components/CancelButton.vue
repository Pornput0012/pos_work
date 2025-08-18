<template>
  <button
    type="button"
    @click="$emit('click')"
    class="itbms-cancel-button bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded font-semibold"
  >
    <slot>Cancel</slot>
  </button>
</template>
