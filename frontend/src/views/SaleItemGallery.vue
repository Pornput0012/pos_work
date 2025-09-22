<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useProductStore } from "@/tools/useProductStore";
import SaleItemCard from "@/components/SaleItemCard.vue";
import Pagination from "@/components/Pagination.vue";
import BaseButton from "@/components/BaseButton.vue";
import { onClickOutside } from "@vueuse/core";

const router = useRouter();
const productStore = useProductStore();

const filters = ref({
  page: 0,
  size: 10,
  sortField: "createdOn",
  sortDirection: "asc",
  filterBrands: [],
  filterStorages: [],
  filterPriceLower: "",
  filterPriceUpper: "",
  searchKeyword: "",
});

const paginationRef = ref();
const trigger = ref(0);
const isLoading = ref(true);
const error = ref(null);
const sortedItems = ref([]);
const brands = ref([]);
const storageOptions = ref([32, 64, 128, 256, 512, 1024, -1]);
const priceRangeOptions = ref([
  { label: "0 – 5,000", min: 0, max: 5000 },
  { label: "5,001 – 10,000", min: 5001, max: 10000 },
  { label: "10,001 – 20,000", min: 10001, max: 20000 },
  { label: "20,001 – 30,000", min: 20001, max: 30000 },
  { label: "30,001 – 40,000", min: 30001, max: 40000 },
  { label: "40,001 – 50,000", min: 40001, max: 50000 },
]);

const selectedBrands = ref([]);
const selectedStorages = ref([]);
const selectedPrices = ref({ min: null, max: null });

const showBrandDropdown = ref(false);
const showStorageDropdown = ref(false);
const showPriceDropdown = ref(false);
const customMin = ref(null);
const customMax = ref(null);
const dropdownRef = ref(null);
onClickOutside(dropdownRef, () => {
  showBrandDropdown.value = false;
  showStorageDropdown.value = false;
  showPriceDropdown.value = false;
});

const totalPages = computed(() => productStore.allPages);
const products = computed(() => productStore.allProducts);

// ✅ computed: แบรนด์เรียง A → Z
const sortedBrands = computed(() =>
  [...brands.value].sort((a, b) => a.name.localeCompare(b.name))
);

const fetchSaleItemsWithFilter = async () => {
  try {
    const params = Object.entries(filters.value)
      .filter(([_, v]) => {
        if (Array.isArray(v)) return v.length > 0;
        return v !== null && v !== undefined && v !== "";
      })
      .map(([k, v]) => {
        if (Array.isArray(v)) {
          if (k === "filterStorages") {
            return `${encodeURIComponent(k)}=${v.join(",")}`;
          }
          return v
            .map((val) => `${encodeURIComponent(k)}=${encodeURIComponent(val)}`)
            .join("&");
        }
        return `${encodeURIComponent(k)}=${encodeURIComponent(v)}`;
      })
      .join("&");

    await productStore.loadAllPages(params);
    await productStore.loadProductsPage(params);
    sortedItems.value = [...products.value];
    applySortingOnly();
  } catch (err) {
    error.value = "Failed to load sale items.";
  } finally {
    isLoading.value = false;
  }
};

const applySortingOnly = () => {
  let items = [...products.value];
  if (filters.value.sortField === "brandName") {
    items.sort((a, b) =>
      filters.value.sortDirection === "asc"
        ? a.brandName.localeCompare(b.brandName)
        : b.brandName.localeCompare(a.brandName)
    );
  } else {
    items.sort((a, b) =>
      filters.value.sortDirection === "asc"
        ? new Date(a.createdOn) - new Date(b.createdOn)
        : new Date(b.createdOn) - new Date(a.createdOn)
    );
  }
  sortedItems.value = items;
};
const setSort = (option) => {
  if (option === "brandAsc") {
    filters.value.sortField = "brandName";
    filters.value.sortDirection = "asc";
  } else if (option === "brandDesc") {
    filters.value.sortField = "brandName";
    filters.value.sortDirection = "desc";
  } else {
    filters.value.sortField = "createdOn";
    filters.value.sortDirection = "asc";
  }
  trigger.value++;
};

// Brand filter
const toggleBrand = (brand) => {
  if (selectedBrands.value.some((b) => b.id === brand.id)) {
    removeBrand(brand);
  } else {
    selectedBrands.value.push(brand);
    filters.value.filterBrands = selectedBrands.value.map((b) => b.name);
    filters.value.page = 0;
    trigger.value++;
  }
};
const removeBrand = (brand) => {
  selectedBrands.value = selectedBrands.value.filter((b) => b.id !== brand.id);
  filters.value.filterBrands = selectedBrands.value.map((b) => b.name);
  filters.value.page = 0;
  trigger.value++;
};

// Storage filter
const toggleStorage = (storage) => {
  if (selectedStorages.value.includes(storage)) {
    removeStorage(storage);
  } else {
    selectedStorages.value.push(storage);
    filters.value.filterStorages = selectedStorages.value.map((s) =>
      s === -1 ? -1 : s
    );
    filters.value.page = 0;
    trigger.value++;
  }
};
const removeStorage = (storage) => {
  selectedStorages.value = selectedStorages.value.filter((s) => s !== storage);
  filters.value.filterStorages = [...selectedStorages.value];
  filters.value.page = 0;
  trigger.value++;
};

// Price filter
const selectPriceRange = (range) => {
  selectedPrices.value = { min: range.min, max: range.max };
  filters.value.filterPriceLower = range.min;
  filters.value.filterPriceUpper = range.max;
  filters.value.page = 0;
  trigger.value++;
  showPriceDropdown.value = false;
};
const applyCustomPrice = () => {
  if (customMin.value === null && customMax.value === null) return;
  selectedPrices.value = {
    min: customMin.value !== null ? customMin.value : null,
    max: customMax.value !== null ? customMax.value : null,
  };
  filters.value.filterPriceLower = selectedPrices.value.min;
  filters.value.filterPriceUpper = selectedPrices.value.max;
  filters.value.page = 0;
  trigger.value++;
  showPriceDropdown.value = false;
  customMin.value = null;
  customMax.value = null;
};
const removePrice = () => {
  selectedPrices.value = { min: null, max: null };
  filters.value.filterPriceLower = null;
  filters.value.filterPriceUpper = null;
  filters.value.page = 0;
  trigger.value++;
};

const clearAll = () => {
  selectedBrands.value = [];
  selectedStorages.value = [];
  selectedPrices.value = { min: null, max: null };
  filters.value.filterBrands = [];
  filters.value.filterStorages = [];
  filters.value.filterPriceLower = null;
  filters.value.filterPriceUpper = null;
  filters.value.page = 0;
  trigger.value++;
};

watch(trigger, () => fetchSaleItemsWithFilter());

onMounted(async () => {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/brands`);
  brands.value = await res.json();

  const saved = sessionStorage.getItem("saleItemsFilters");
  if (saved) {
    try {
      const state = JSON.parse(saved);
      filters.value = { ...filters.value, ...state.filters };
      if (state.selectedBrands?.length) {
        selectedBrands.value = brands.value.filter((b) =>
          state.selectedBrands.some((sb) => sb.name === b.name)
        );
        filters.value.filterBrands = selectedBrands.value.map((b) => b.name);
      }
      selectedStorages.value = state.selectedStorages || [];
      filters.value.filterStorages = [...selectedStorages.value];
      selectedPrices.value = state.selectedPrices || { min: null, max: null };
      filters.value.filterPriceLower = selectedPrices.value.min;
      filters.value.filterPriceUpper = selectedPrices.value.max;
    } catch (e) {
      console.error("Failed to restore state:", e);
    }
  }
  await fetchSaleItemsWithFilter();
});

const goToAddItem = () => router.push("/sale-items/add");

watch(
  [filters, selectedBrands, selectedStorages, selectedPrices],
  () => {
    const state = {
      filters: filters.value,
      selectedBrands: selectedBrands.value,
      selectedStorages: selectedStorages.value,
      selectedPrices: selectedPrices.value,
    };
    sessionStorage.setItem("saleItemsFilters", JSON.stringify(state));
  },
  { deep: true }
);
</script>

<template>
  <div class="p-6">
    <!-- Add Button + Sort -->
    <div class="flex justify-between items-center mb-6">
      <BaseButton variant="add" label="Add Sale Item" @click="goToAddItem" />
      <div class="space-x-2">
        <BaseButton
          variant="primary"
          class="px-3 py-1 rounded"
          :customClass="
            filters.sortField === 'brandName' && filters.sortDirection === 'asc'
              ? 'bg-gray-800 text-white'
              : 'bg-blue-200'
          "
          @click="setSort('brandAsc')"
        >
          Sort by Brand ↑
        </BaseButton>
        <BaseButton
          variant="primary"
          class="px-3 py-1 rounded"
          :customClass="
            filters.sortField === 'brandName' &&
            filters.sortDirection === 'desc'
              ? 'bg-gray-800 text-white'
              : 'bg-blue-200'
          "
          @click="setSort('brandDesc')"
        >
          Sort by Brand ↓
        </BaseButton>
        <BaseButton
          variant="primary"
          class="px-3 py-1 rounded"
          :customClass="
            filters.sortField === 'createdOn'
              ? 'bg-gray-800 text-white'
              : 'bg-blue-200'
          "
          @click="setSort('default')"
        >
          Default
        </BaseButton>
      </div>
    </div>
    <!-- Search Bar -->
    <div class="mb-4 flex items-center gap-4">
      <input
        v-model="filters.searchKeyword"
        type="text"
        placeholder="ค้นหาสินค้า, รุ่น, สี, คำอธิบาย..."
        class="border rounded px-3 py-2 w-full max-w-md focus:outline-none focus:ring focus:border-blue-400"
        @keyup.enter="trigger++"
      />
      <BaseButton
        variant="primary"
        label="ค้นหา"
        @click="trigger++"
        class="px-4 py-2"
      />
    </div>
    <!-- Filters -->
    <div class="flex flex-wrap gap-4 mb-4">
      <!-- Brand Filter -->
      <!-- Filters -->
      <div class="flex flex-wrap gap-4 mb-4">
        <!-- Brand Filter -->
        <div ref="dropdownRef" class="relative">
          <div
            class="flex items-center border px-3 py-2 rounded cursor-pointer"
            @click="showBrandDropdown = !showBrandDropdown"
          >
            <span class="flex-1 text-gray-700"> Filter by brand(s) </span>
          </div>
          <div
            v-if="showBrandDropdown"
            class="absolute bg-white border rounded shadow w-64 max-h-60 overflow-y-auto z-10"
          >
            <div
              v-for="brand in sortedBrands"
              :key="brand.id"
              class="px-3 py-2 hover:bg-gray-100 cursor-pointer flex justify-between items-center"
              @click="toggleBrand(brand)"
            >
              <div class="flex items-center">
                <input
                  type="checkbox"
                  :checked="selectedBrands.some((b) => b.id === brand.id)"
                  class="mr-2"
                />
                {{ brand.name }}
              </div>
            </div>
          </div>
        </div>

        <!-- Selected brands chip -->
        <div
          v-if="selectedBrands.length"
          class="flex flex-wrap gap-2 items-center"
        >
          <span
            v-for="brand in selectedBrands"
            :key="brand.id"
            class="flex items-center bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm"
          >
            {{ brand.name }}
            <button
              class="ml-1 text-red-500 hover:text-red-700"
              @click.stop="removeBrand(brand)"
            >
              ×
            </button>
          </span>
        </div>
      </div>

      <!-- Storage Filter -->
      <div class="relative">
        <div
          class="flex items-center border px-3 py-2 rounded cursor-pointer"
          @click="showStorageDropdown = !showStorageDropdown"
        >
          <span class="flex-1 text-gray-700">
            Filter by storage:
            <span v-if="selectedStorages.length" class="text-blue-600">
              {{
                selectedStorages
                  .map((s) => (s === -1 ? "No Storage" : s + " GB"))
                  .join(", ")
              }}
            </span>
          </span>
        </div>
        <div
          v-if="showStorageDropdown"
          class="absolute bg-white border rounded shadow w-64 max-h-60 overflow-y-auto z-10"
        >
          <div
            v-for="s in storageOptions"
            :key="s"
            class="px-3 py-2 hover:bg-gray-100 cursor-pointer flex justify-between items-center"
            @click="toggleStorage(s)"
          >
            <div class="flex items-center">
              <input
                type="checkbox"
                :checked="selectedStorages.includes(s)"
                class="mr-2"
              />
              {{ s != -1 ? s + " GB" : "No Storage" }}
            </div>
            <span v-if="selectedStorages.includes(s)" class="text-blue-600">
              &#10003;
            </span>
          </div>
        </div>
      </div>

      <!-- Price Filter -->
      <div class="relative">
        <div
          class="flex items-center border px-3 py-2 rounded cursor-pointer"
          @click="showPriceDropdown = !showPriceDropdown"
        >
          <span class="flex-1 text-gray-700">
            Filter by price:
            <span
              v-if="selectedPrices.min !== null || selectedPrices.max !== null"
              class="text-blue-600"
            >
              {{ selectedPrices.min !== null ? selectedPrices.min : "" }} -
              {{ selectedPrices.max !== null ? selectedPrices.max : "" }}
            </span>
          </span>
        </div>
        <div
          v-if="showPriceDropdown"
          class="absolute bg-white border rounded shadow w-64 max-h-60 overflow-y-auto z-10"
        >
          <div
            v-for="range in priceRangeOptions"
            :key="range.label"
            class="px-3 py-2 hover:bg-gray-100 cursor-pointer flex justify-between items-center"
            @click="selectPriceRange(range)"
          >
            <div class="flex items-center">
              <input
                type="checkbox"
                :checked="
                  selectedPrices.min === range.min &&
                  selectedPrices.max === range.max
                "
                class="mr-2"
              />
              {{ range.label }}
            </div>
            <span
              v-if="
                selectedPrices.min === range.min &&
                selectedPrices.max === range.max
              "
              class="text-blue-600"
            >
              &#10003;
            </span>
          </div>
          <div class="px-3 py-2 border-t">
            <div class="flex gap-2 items-center">
              <input
                type="number"
                v-model.number="customMin"
                placeholder="Custom Min"
                class="w-full border px-2 py-1 rounded"
              />
              <input
                type="number"
                v-model.number="customMax"
                placeholder="Custom Max"
                class="w-full border px-2 py-1 rounded"
              />
            </div>
            <div class="flex gap-2 mt-2">
              <button
                class="btn btn-sm btn-primary flex-1"
                @click="applyCustomPrice"
              >
                Apply
              </button>
              <button class="btn btn-sm flex-1" @click="removePrice">
                Remove
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Clear All -->
      <button class="btn btn-error btn-sm text-white" @click="clearAll">
        Clear All
      </button>
    </div>

    <!-- Sale Items -->
    <div v-if="isLoading" class="text-center">Loading...</div>
    <div v-else-if="error" class="text-center text-red-500">{{ error }}</div>
    <div v-else-if="sortedItems.length === 0" class="text-center">
      No sale item
    </div>
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
      @sendPages="
        (page) => {
          filters.page = page - 1;
          trigger++;
        }
      "
    />
  </div>
</template>
