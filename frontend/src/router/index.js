// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// นำเข้าหน้า Component
import HomeView from "@/views/HomeView.vue";
import SaleItemGallery from "@/views/SaleItemGallery.vue";
import SaleItemDetail from "@/views/SaleItemDetail.vue";
import AddSaleItem from "@/views/AddSaleItem.vue";
import SaleItemEdit from "@/views/SaleItemEdit.vue";
import SaleItemList from "@/views/SaleItemList.vue";
import AddBrand from "@/views/AddBrand.vue";
import BrandList from "@/views/BrandList.vue";
import BrandEdit from "@/views/BrandEdit.vue";
import Register from "@/views/Register.vue";
import SignIn from "@/views/SignIn.vue";
import VerifyEmail from "@/views/VerifyEmail.vue";
import ProfileView from "@/views/ProfileView.vue";
import ProfileEditView from "@/views/ProfileEditView.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: HomeView, // Landing page
  },
  {
    path: "/sale-items",
    name: "SaleItems",
    component: SaleItemGallery,
  },
  {
    path: "/sale-items/:id",
    name: "SaleItemDetail",
    component: SaleItemDetail,
    props: true,
  },

  {
    path: "/sale-items/add",
    name: "AddSaleItem",
    component: AddSaleItem,
  },

  {
    path: "/sale-items/:id/edit",
    name: "SaleItemEdit",
    component: SaleItemEdit,
  },

  {
    path: "/sale-items/list",
    name: "SaleItemList",
    component: SaleItemList,
    props: true,
  },

  {
    path: "/brands",
    name: "BrandList",
    component: BrandList,
  },

  {
    path: "/brands/add",
    name: "AddBrand",
    component: AddBrand,
  },

  {
    path: "/brands/:id/edit",
    name: "BrandEdit",
    component: BrandEdit,
    props: true,
  },

  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
  },

  {
    path: "/verify-email",
    name: "VerifyEmail",
    component: () => import("@/views/VerifyEmail.vue"),
  },

  {
    path: "/signin",
    name: "SignIn",
    component: () => import("@/views/SignIn.vue"),
  },
  {
    path: "/profile",
    name: "profile",
    component: ProfileView,
  },
  {
    path: "/profile/edit",
    name: "profile-edit",
    component: ProfileEditView,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
