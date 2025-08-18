<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/tools/useProductStore'
import SaleItemCard from '@/components/SaleItemCard.vue'
import Pagination from '@/components/Pagination.vue'
import BaseButton from '@/components/BaseButton.vue'
import { onClickOutside } from '@vueuse/core'

const router = useRouter()
const productStore = useProductStore()

const filters = ref({
  page: 0,
  size: 10,
  sortField: 'createdOn',
  sortDirection: 'asc',
  filterBrands: [],
  filterStorages: [],
  filterPriceLower: "",
  filterPriceUpper: ""
})

const paginationRef = ref()
const trigger = ref(0)
const isLoading = ref(true)
const error = ref(null)
const sortedItems = ref([])
const brands = ref([])
const selectedBrands = ref([])
const selectedStorages = ref([])
const selectedPrices = ref({ min: null, max: null })

const showBrandDropdown = ref(false)
const dropdownRef = ref(null)
onClickOutside(dropdownRef, () => { showBrandDropdown.value = false })

const totalPages = computed(() => productStore.allPages)
const products = computed(() => productStore.allProducts)

const fetchSaleItemsWithFilter = async () => {
  try {
    // สร้าง query string เฉพาะ key ที่ไม่เป็นค่า falsy
    const params = Object.entries(filters.value)
      .filter(([_, v]) => {
        if (Array.isArray(v)) return v.length > 0
        return v !== null && v !== undefined && v !== ''
      })
      .map(([k, v]) => {
        if (Array.isArray(v)) {
          // ส่ง filterStorages เป็น comma-separated
          if (k === 'filterStorages') {
            return `${encodeURIComponent(k)}=${v.join(',')}`
          }
          return v.map(val => `${encodeURIComponent(k)}=${encodeURIComponent(val)}`).join('&')
        }
        return `${encodeURIComponent(k)}=${encodeURIComponent(v)}`
      })
      .join('&')

    // สมมติว่า productStore.loadAllPages/loadProductsPage รองรับ query string
    await productStore.loadAllPages(params)
    await productStore.loadProductsPage(params)
    sortedItems.value = [...products.value]
    applySortingOnly()
  } catch (err) {
    error.value = 'Failed to load sale items.'
  } finally {
    isLoading.value = false
  }
}

const applySortingOnly = () => {
  let items = [...products.value]
  if (filters.value.sortField === 'brandName') {
    items.sort((a, b) =>
      filters.value.sortDirection === 'asc'
        ? a.brandName.localeCompare(b.brandName)
        : b.brandName.localeCompare(a.brandName)
    )
  } else {
    items.sort((a, b) =>
      filters.value.sortDirection === 'asc'
        ? new Date(a.createdOn) - new Date(b.createdOn)
        : new Date(b.createdOn) - new Date(a.createdOn)
    )
  }
  sortedItems.value = items
}

// Brand filter
const toggleBrand = (brand) => {
  if (selectedBrands.value.some(b => b.id === brand.id)) {
    selectedBrands.value = selectedBrands.value.filter(b => b.id !== brand.id)
  } else {
    selectedBrands.value = [...selectedBrands.value, brand]
  }
  filters.value.filterBrands = selectedBrands.value.map(b => b.name)
  filters.value.page = 0
  trigger.value++
}

const clearBrands = () => {
  selectedBrands.value = []
  filters.value.filterBrands = []
  filters.value.page = 0
  trigger.value++
}

// Storage filter
const toggleStorage = (storage) => {
  if (selectedStorages.value.includes(storage)) {
    selectedStorages.value = selectedStorages.value.filter(s => s !== storage)
  } else {
    selectedStorages.value.push(storage)
  }
  filters.value.filterStorages = [...selectedStorages.value]
  filters.value.page = 0
  trigger.value++
}

const clearStorages = () => {
  selectedStorages.value = []
  filters.value.filterStorages = []
  filters.value.page = 0
  trigger.value++
}

// Price filter
const applyCustomPrice = () => {
  filters.value.filterPriceLower = selectedPrices.value.min
  filters.value.filterPriceUpper = selectedPrices.value.max
  filters.value.page = 0
  trigger.value++
}

const clearPrice = () => {
  selectedPrices.value = { min: null, max: null }
  filters.value.filterPriceLower = null
  filters.value.filterPriceUpper = null
  filters.value.page = 0
  trigger.value++
}

const clearAll = () => {
  clearBrands()
  clearStorages()
  clearPrice()
}

watch(trigger, () => fetchSaleItemsWithFilter())

onMounted(async () => {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands`)
  brands.value = await res.json()
  await fetchSaleItemsWithFilter()
})

const goToAddItem = () => router.push('/sale-items/add')
</script>

<template>
  <div class="p-6">
    <!-- Add Button -->
    <div class="flex justify-between items-center mb-6">
      <BaseButton
        variant="add"
        label="Add Sale Item"
        @click="goToAddItem"
      />
    </div>

    <!-- Filter Section -->
    <div class="flex flex-wrap gap-4 mb-4">
      <!-- Brand Filter -->
      <div ref="dropdownRef" class="relative">
        <input
          readonly
          class="border px-3 py-2 rounded cursor-pointer"
          placeholder="Filter by brand(s)"
          :value="selectedBrands.map(b => b.name).join(', ')"
          @click="showBrandDropdown = !showBrandDropdown"
        />
        <div v-if="showBrandDropdown" class="absolute bg-white border rounded shadow w-64 max-h-60 overflow-y-auto">
          <div
            v-for="brand in brands"
            :key="brand.id"
            class="px-3 py-2 hover:bg-gray-100 cursor-pointer"
            @click="toggleBrand(brand)"
          >
            <input type="checkbox" :checked="selectedBrands.some(b => b.id === brand.id)" class="mr-2" />
            {{ brand.name }}
          </div>
        </div>
      </div>

      <!-- Storage Filter -->
      <div class="flex gap-2 items-center">
        <label>Storage:</label>
        <button
          v-for="s in [64,128,256,0]"
          :key="s"
          class="px-3 py-1 border rounded"
          :class="{ 'bg-blue-500 text-white': selectedStorages.includes(s) }"
          @click="toggleStorage(s)"
        >
          {{ s != 0 ? s + ' GB' : 'No Storage' }}
        </button>
      </div>

      <!-- Price Filter -->
      <div class="flex gap-2 items-center">
        <label>Price:</label>
        <input type="number" v-model.number="selectedPrices.min" placeholder="Min" class="w-20 border px-2 py-1 rounded" />
        <input type="number" v-model.number="selectedPrices.max" placeholder="Max" class="w-20 border px-2 py-1 rounded" />
        <button class="btn btn-sm btn-primary" @click="applyCustomPrice">Apply</button>
        <button class="btn btn-sm" @click="clearPrice">Clear</button>
      </div>

      <!-- Clear All -->
      <button class="btn btn-error btn-sm text-white" @click="clearAll">Clear All</button>
    </div>

    <!-- Sale Items -->
    <div v-if="isLoading" class="text-center">Loading...</div>
    <div v-else-if="error" class="text-center text-red-500">{{ error }}</div>
    <div v-else-if="sortedItems.length === 0" class="text-center">No sale item</div>
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
      <SaleItemCard v-for="item in sortedItems" :key="item.id" :item="item" />
    </div>

    <Pagination
      ref="paginationRef"
      v-if="!isLoading && sortedItems.length > 0"
      :totalPages="totalPages"
      @sendPages="page => { filters.page = page - 1; trigger++ }"
    />
  </div>
</template>
