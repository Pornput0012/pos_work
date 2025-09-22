<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

import BaseButton from '@/components/BaseButton.vue'

const saleItems = ref([]);
const router = useRouter();

const showDeleteModal = ref(false);
const showDeleteSuccessModal = ref(false);
const deleteTargetId = ref(null);
const deleteError = ref(null);

onMounted(async () => {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items`);
  const data = await res.json();
  saleItems.value = data;
});

const handleEdit = (id) => {
  router.push(`/sale-items/${id}/edit`);
};

const handleDelete = (id) => {
  deleteTargetId.value = id;
  showDeleteModal.value = true;
};

const confirmDelete = async () => {
  if (!deleteTargetId.value) return;

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/sale-items/${deleteTargetId.value}`, {
      method: "DELETE",
    });

    if (res.ok) {
      saleItems.value = saleItems.value.filter((item) => item.id !== deleteTargetId.value);
      showDeleteModal.value = false;
      showDeleteSuccessModal.value = true;
    } else if (res.status === 404) {
      deleteError.value = "The requested sale item does not exist.";
    } else {
      deleteError.value = `Failed to delete item: ${res.statusText}`;
    }
  } catch (err) {
    console.error("Delete error:", err);
    deleteError.value = "Error deleting item.";
  }
};

const cancelDelete = () => {
  showDeleteModal.value = false;
  deleteTargetId.value = null;
};

const closeSuccessModal = () => {
  showDeleteSuccessModal.value = false;
  deleteTargetId.value = null;
};
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <BaseButton
        variant="add"
        label="Add Sale Item"
        @click="() => router.push('/sale-items/new')"
      />
      <BaseButton
        variant="primary"
        label="Manage Brands"
        customClass=""
        @click="() => router.push('/brands')"
      />
    </div>

    <!-- Table -->
    <div
      class="overflow-x-auto rounded-lg shadow border border-gray-800 bg-white"
    >
      <table class="w-full table-auto text-sm text-white">
        <thead class="bg-blue-800 border-b border-gray-800">
          <tr>
            <th class="text-left px-4 py-3 font-semibold">ID</th>
            <th class="text-left px-4 py-3 font-semibold">Brand</th>
            <th class="text-left px-4 py-3 font-semibold">Model</th>
            <th class="text-left px-4 py-3 font-semibold">RAM</th>
            <th class="text-left px-4 py-3 font-semibold">Storage</th>
            <th class="text-left px-4 py-3 font-semibold">Color</th>
            <th class="text-left px-4 py-3 font-semibold">Price</th>
            <th class="text-center px-4 py-3 font-semibold">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-if="saleItems?.length === 0" class="text-center text-gray-500">
            <td colspan="8" class="px-4 py-6">No Sale Item</td>
          </tr>
          <tr
            v-for="item in saleItems"
            :key="item.id"
            class="itbms-row hover:bg-gray-50 transition-colors border-b border-gray-800 text-black text-sm"
          >
            <td class="itbms-id px-4 py-3">{{ item.id }}</td>
            <td class="itbms-brand px-4 py-3">{{ item.brandName }}</td>
            <td class="itbms-model px-4 py-3">{{ item.model }}</td>
            <td class="itbms-ramGb px-4 py-3">{{ item.ramGb ?? "-" }}</td>
            <td class="itbms-storageGb px-4 py-3">{{ item.storageGb ?? "-" }}</td>
            <td class="itbms-color px-4 py-3">{{ item.color ?? "-" }}</td>
            <td class="itbms-price px-4 py-3">{{ item.price.toLocaleString() }}</td>
            <td class="px-4 py-3 text-center flex justify-center gap-2">
              <BaseButton
                variant="edit"
                customClass="itbms-edit-button"
                @click="() => handleEdit(item.id)"
                label="Edit"
              />
              <BaseButton
                variant="delete"
                customClass="itbms-delete-button"
                @click="() => handleDelete(item.id)"
                label="Delete"
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Delete Modal -->
  <DeleteSaleItem
    v-if="showDeleteModal"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />

  <!-- Success Modal -->
  <DeleteSaleItemSuccess 
    v-if="showDeleteSuccessModal"
    @close="closeSuccessModal"
  />
</template>
