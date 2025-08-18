<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/tools/useProductStore'
import SaleItemCard from '@/components/SaleItemCard.vue'
import Pagination from '@/components/Pagination.vue'
import BaseButton from '@/components/BaseButton.vue' // import BaseButton
import { onClickOutside } from '@vueuse/core'

const router = useRouter()
const productStore = useProductStore()

const filters = ref({
  page: 0,
  filterBrands: [], // Array of brand names, e.g., ["Nike", "Adidas"]
  size: 10,
  sortField: 'createdOn',
  sortDirection: 'asc',
})

const paginationRef = ref()

const resetPagination = () => {
  paginationRef.value?.goToFirst()
}

const trigger = ref(0)
const isLoading = ref(true)
const error = ref(null)
const sortedItems = ref([])
const brands = ref([])
const selectedBrands = ref(JSON.parse(sessionStorage.getItem('selectedBrands') || '[]'))
const showBrandDropdown = ref(false)
const dropdownRef = ref(null)
const isFetching = ref(false)

onClickOutside(dropdownRef, () => {
  showBrandDropdown.value = false
})

const totalPages = computed(() => productStore.allPages)
const products = computed(() => productStore.allProducts)

const fetchSaleItemsWithFilter = async () => {
  if (isFetching.value) return
  isFetching.value = true
  try {
    console.log('Filters:', filters.value) // Debug: Log filters
    await productStore.loadAllPages(filters.value)
    await productStore.loadProductsPage(filters.value)
    sortedItems.value = [...products.value]
    applySortingOnly()
  } catch (err) {
    console.error('Error fetching sale items:', err)
    error.value = 'Failed to load sale items.'
  } finally {
    isLoading.value = false
    isFetching.value = false
  }
}

const applySortingOnly = () => {
  let items = [...products.value]
  switch (filters.value.sortField) {
    case 'brandName':
      items.sort((a, b) =>
        filters.value.sortDirection === 'asc'
          ? a.brandName.localeCompare(b.brandName)
          : b.brandName.localeCompare(a.brandName)
      )
      break
    case 'createdOn':
    default:
      items.sort((a, b) =>
        filters.value.sortDirection === 'asc'
          ? new Date(a.createdOn) - new Date(b.createdOn)
          : new Date(b.createdOn) - new Date(a.createdOn)
      )
      break
  }
  sortedItems.value = items
}

const updatePages = (page) => {
  if (page < 1 || page > totalPages.value) return
  filters.value.page = page - 1
  productStore.setActivePage(page)
  sessionStorage.setItem('activePage', String(page))
  trigger.value++
}

const setSort = (option) => {
  if (option === 'brandAsc') {
    filters.value.sortField = 'brandName'
    filters.value.sortDirection = 'asc'
  } else if (option === 'brandDesc') {
    filters.value.sortField = 'brandName'
    filters.value.sortDirection = 'desc'
  } else {
    filters.value.sortField = 'createdOn'
    filters.value.sortDirection = 'asc'
  }
  sessionStorage.setItem('saleSortOption', option)
  resetPagination()
  trigger.value++
}

const toggleBrand = (brand) => {
  const index = selectedBrands.value.findIndex(b => b.id === brand.id)
  let newSelection
  if (index > -1) {
    newSelection = selectedBrands.value.filter(b => b.id !== brand.id)
  } else {
    newSelection = [...selectedBrands.value, brand]
  }
  selectedBrands.value = newSelection
  sessionStorage.setItem('selectedBrands', JSON.stringify(newSelection))
  filters.value.filterBrands = newSelection.map(b => b.name) // Use brand names
  filters.value.page = 0
  console.log('After toggleBrand:', filters.value.filterBrands) // Debug
  sessionStorage.setItem('activePage', '1')
  resetPagination()
  trigger.value++
}

const clearBrands = () => {
  selectedBrands.value = []
  sessionStorage.removeItem('selectedBrands')
  filters.value.filterBrands = []
  filters.value.page = 0
  sessionStorage.setItem('activePage', '1')
  productStore.setActivePage(1)
  resetPagination()
  trigger.value++
}

const fetchBrands = async () => {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands`)
    if (!res.ok) throw new Error(`Error ${res.status}`)
    brands.value = (await res.json()).sort((a, b) => a.name.localeCompare(b.name))
    console.log('Fetched brands:', brands.value) // Debug
  } catch (err) {
    console.error('Error fetching brand list:', err)
  }
}

// Watch for changes in page size
watch(() => filters.value.size, () => {
  filters.value.page = 0 // Reset to first page
  sessionStorage.setItem('activePage', '1')
  productStore.setActivePage(1)
  resetPagination()
  trigger.value++ // Trigger data fetch with new page size
})

watch(trigger, () => {
  fetchSaleItemsWithFilter()
})

onMounted(async () => {
  console.log('Initial selectedBrands:', selectedBrands.value) // Debug
  const savedPage = sessionStorage.getItem('activePage')
  console.log("sessionStorage.getItem('activePage')", sessionStorage.getItem('activePage'))
  if (savedPage) {
    const page = parseInt(savedPage)
    filters.value.page = page - 1
    productStore.setActivePage(page)
  }
  await fetchBrands()
  selectedBrands.value = selectedBrands.value.filter(sb =>
    brands.value.some(b => b.id === sb.id)
  )
  sessionStorage.setItem('selectedBrands', JSON.stringify(selectedBrands.value))
  filters.value.filterBrands = selectedBrands.value.map(b => b.name)
  console.log('Initial filterBrands:', filters.value.filterBrands) // Debug
  await fetchSaleItemsWithFilter()
})

const goToAddItem = () => {
  router.push('/sale-items/add')
}
</script>

<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-6">
      <BaseButton
        variant="add"
        label="Add Sale Item"
        customClass="itbms-sale-item-add"
        @click="goToAddItem"
      />
      <div class="space-x-2">
        <BaseButton
          variant="primary"
          class="itbms-brand-asc px-3 py-1 rounded"
          :customClass="filters.sortField === 'brandName' && filters.sortDirection === 'asc' ? 'bg-gray-800 text-white' : 'bg-blue-200'"
          @click="setSort('brandAsc')"
        >
          Sort by Brand ↑
        </BaseButton>
        <BaseButton
          variant="primary"
          class="itbms-brand-desc px-3 py-1 rounded"
          :customClass="filters.sortField === 'brandName' && filters.sortDirection === 'desc' ? 'bg-gray-800 text-white' : 'bg-blue-200'"
          @click="setSort('brandDesc')"
        >
          Sort by Brand ↓
        </BaseButton>
        <BaseButton
          variant="primary"
          class="itbms-brand-none px-3 py-1 rounded"
          :customClass="filters.sortField === 'createdOn' ? 'bg-gray-800 text-white' : 'bg-blue-200'"
          @click="setSort('default')"
        >
          Default
        </BaseButton>
      </div>
    </div>

    <div class="mb-4 relative">
      <div class="flex items-center space-x-2">
        <div class="relative" ref="dropdownRef">
          <input
            readonly
            class="border px-3 py-1 rounded w-64 cursor-pointer bg-white"
            placeholder="Filter by brand(s)"
            @click="showBrandDropdown = !showBrandDropdown"
            :value="selectedBrands.map(b => b.name).join(', ')"
          />
          <div
            v-if="showBrandDropdown"
            class="absolute top-full mt-1 left-0 w-full max-h-60 overflow-y-auto border rounded bg-white z-10 shadow"
          >
            <div
              v-for="brand in brands"
              :key="brand.id"
              class="px-3 py-2 hover:bg-gray-100 cursor-pointer"
              @click="toggleBrand(brand)"
            >
              <input
                type="checkbox"
                :checked="selectedBrands.some(b => b.id === brand.id)"
                class="mr-2"
              />
              {{ brand.name }}
            </div>
          </div>
        </div>

        <BaseButton
          variant="cancel"
          label="Clear"
          customClass="itbms-clear-button"
          @click="clearBrands"
        />

        <label for="page-size" class="text-sm font-medium">Show</label>
        <select
          name="page-size"
          id="page-size"
          v-model="filters.size"
          class="itbms-page-size bg-gray-300 border border-gray-300 rounded-md px-3 py-2 text-sm cursor-pointer focus:outline-none"
        >
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="20">20</option>
        </select>
      </div>
    </div>

    <div v-if="isLoading" class="text-center text-gray-400">Loading...</div>
    <div v-else-if="error" class="text-center text-red-500">{{ error }}</div>
    <div v-else-if="sortedItems.length === 0" class="text-center text-gray-400">No sale item</div>
    <div
      v-else
      class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4"
    >
      <SaleItemCard v-for="item in sortedItems" :key="item.id" :item="item" />
    </div>

    <Pagination
      ref="paginationRef"
      v-if="!isLoading && sortedItems.length > 0"
      :totalPages="totalPages"
      @sendPages="updatePages"
      @page-change="updatePages"
    />
  </div>
</template>
