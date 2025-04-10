<template>
  <div class="center">
    <img src="../assets/icon/error.svg" alt="">
<!--    <p>支付失败，请重新扫码进入！</p>-->
    <p>错误: {{msg}}</p>
  </div>
</template>

<style lang="css" scoped>
  .center {
    margin-top:30%;
    display: flex;
    align-items: center;
    flex-direction: column;
  }
  p {
    font-size: 36px;
    margin-top: 50px;
  }
</style>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 响应式数据声明（自动推导类型为Ref<string>）
const msg = ref<string>('请重新扫码进入！')

// 生命周期钩子（替代旧版mounted）
onMounted(() => {
  // 类型安全的参数获取
  const errParam = route.params.errInfo

  // 参数存在性校验与类型断言
  if (errParam && typeof errParam === 'string') {
    msg.value = errParam
  }
})
</script>
