<template>
  <div>
    <!-- <p style="font-size:16px;">获取用户ID信息</p> -->
    <p style="font-size:16px;">正在跳转...</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getChannelUserId } from '@/api/api'
import wayCodeUtils from '@/utils/wayCode'
import { channelUserIdStorage } from '@/utils/channelUserId'
import config from "@/config/index"

const router = useRouter()
const route = useRoute()

// URL查询参数解析（带类型声明）
const searchToObject = (): Record<string, string> => {
  if (!window.location.search) return {}

  return window.location.search.substring(1)
    .split('&')
    .reduce((acc: Record<string, string>, pair) => {
      const [key, value] = pair.split('=')
      if (key) {
        acc[decodeURIComponent(key)] = decodeURIComponent(value || '')
      }
      return acc
    }, {})
}

// 生命周期钩子
onMounted(async () => {
  try {
    const allQuery = {
      ...searchToObject(),
      ...route.query
    }

    const response = await getChannelUserId(allQuery)
    channelUserIdStorage.set(response.data.channelUserId)

    await router.push({
      name: wayCodeUtils.getPayWay()?.routeName
    })
  } catch (error) {
    const err = error as { msg?: string }
    await router.push({
      name: config.errorPageRouteName,
      params: { errInfo: err?.msg || '未知错误' }
    })
  }
})
</script>
