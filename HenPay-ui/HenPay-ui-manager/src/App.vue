<template>
  <a-config-provider :locale="zhCN">
    <a-spin :spinning="userStore.globalLoading">
      <div id="app" style="height: 100vh">
        <router-view />
      </div>
    </a-spin>
  </a-config-provider>
</template>

<script lang="ts" setup>
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { useUserStore } from '@/stores/user'
import { ref } from 'vue'

const userStore = useUserStore()

dayjs.locale('zh-cn')

// :root 是 CSS 中的一个 伪类选择器，用于选择文档的根元素
// 在 HTML 文档中，根元素始终是 <html> 标签，因此 :root 等价于 html 选择器
const styleDom = document.createElement('style')
styleDom.textContent = `
  :root {
    --ant-primary-color: #1677ff
  }
`
// 创建全局的css变量
document.head.appendChild(styleDom)

</script>
