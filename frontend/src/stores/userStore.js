import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const userInfo = ref(null);

  const setUserInfo = (user) => {
    userInfo.value = user;
  };

  const getUserInfo = () => {
    return userInfo.value;
  };

  return { setUserInfo, getUserInfo };
});
