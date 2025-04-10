<template>
  <div class="jump">
    <!-- <p style="24px">
      正在跳转到支付页面，请稍候~
    </p> -->
    <img src="@/assets/images/loading.gif" alt="" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getRedirectUrl } from '@/api/api'
import config from '@/config'

// 类型声明
interface RedirectResponse {
  code: number
  msg: string
  data: {
    redirectUrl: string
  }
}

const router = useRouter()
const route = useRoute()

onMounted(() => {
  console.log("路由参数:", route.params)

  // TODO: 需要实现真实的 channelUserId 检查逻辑
  // if (channelUserId.getChannelUserId()) {
  //   router.push({ name: wayCodeUtils.getPayWay().routeName })
  //   return
  // }

  // 处理重定向逻辑
  const handleRedirect = async () => {
    try {
      const response = await getRedirectUrl() as RedirectResponse

      if (response.code === 200) {
        // 使用 window.location 进行完全重定向
        window.location.href = response.data.redirectUrl
      } else {
        throw new Error(response.msg || '获取重定向URL失败')
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : '未知错误'
      await router.push({
        name: config.errorPageRouteName,
        query: {  // 使用 query 代替 params 更符合 URL 规范
          errInfo: encodeURIComponent(errorMessage),
          timestamp: Date.now()  // 添加时间戳避免缓存
        }
      })
    }
  }

  handleRedirect()
})
</script>

<style lang="css" scoped>
  .jump {
    height:100vh;
    width: 100vw;
    display:flex;
    justify-content: center;
    align-items: center;
  }
</style>
