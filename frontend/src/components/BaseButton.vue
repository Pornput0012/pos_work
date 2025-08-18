<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const emit = defineEmits(['click'])

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
  },
  label: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: 'button',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  navigateTo: {
    type: String,
    default: '',
  },
  customClass: {
    type: String,
    default: '',
  }
})

const router = useRouter()

const handleClick = () => {
  if (props.disabled) return
  if (props.navigateTo) {
    router.push(props.navigateTo)
  } else {
    emit('click')
  }
}

const buttonClass = computed(() => {
  if (props.disabled && props.variant === 'save') return 'bg-green-300'
  if (props.disabled) return 'bg-gray-300'

  switch (props.variant) {
    case 'add':
    case 'edit':
    case 'primary':
      return 'bg-blue-600 hover:bg-blue-700'
    case 'delete':
      return 'bg-red-600 hover:bg-red-700'
    case 'cancel':
      return 'bg-gray-300 hover:bg-gray-400'
    case 'save':
      return 'bg-green-600 hover:bg-green-700'
    default:
      return 'bg-gray-500 hover:bg-gray-600'
  }
})
</script>

<template>
  <button
    :type="type"
    :disabled="disabled"
    @click="handleClick"
    :class="[
      'px-4 py-2 rounded font-semibold transition shadow text-white',
      buttonClass,
      { 'cursor-not-allowed': disabled },
      customClass
    ]"
  >
    <slot>{{ label }}</slot>
  </button>
</template>
