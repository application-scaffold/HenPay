<template>
  <router-view></router-view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

// 组件逻辑（现代组合式API）
onMounted(() => {
  console.log("进入cashier页面")
})
</script>
