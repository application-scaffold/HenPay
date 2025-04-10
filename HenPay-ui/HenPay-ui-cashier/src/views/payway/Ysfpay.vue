<template>
  <div>
    <header class="header">
      <div class="header-text">付款给{{ merchantName }}</div>
      <div class="header-img">
        <img :src="avatar ? avatar : icon_member_default" alt="" />
      </div>
    </header>
    <div class="plus-input">
      <!-- ￥字符 货币的符号-->
      <div class="S">
        <img src="../../assets/icon/S.svg" alt="" />
      </div>

      <!-- 手写输入框 -->
      <div  class="input-c">
        <div  class="input-c-div-1">{{ amount }}</div>
      </div>
      <!-- 手写输入框的提示文字 -->
      <div v-show="!amount" class="placeholder">请输入金额</div>
    </div>
    <ul class="plus-ul" >
      <!-- 支付板块 -->
      <li style="border-radius:10px;">
        <!-- 支付金额板块 -->
        <div class="img-div">
          <img :src="wxImg" alt="" />
          <div class="div-text">
            云闪付支付
          </div>
        </div>
      </li>
    </ul>
    <div class="bnt-pay">
      <!-- <div
        class="bnt-pay-text"
        style="background-color:#ff534d"
        @click="pay"
      >
        付款
      </div> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPayPackage } from '@/api/api'
import config from "@/config/index"
import type { IResponseData } from '@/api/api' // 假设已有响应类型定义
import ysfImg from '../../assets/images/ysf.jpg'
import wxImg from '../../assets/images/ysf.jpg'

// 支付宝支付结果类型
interface AlipayTradeResult {
  resultCode: string
  memo?: string
  result?: string
}

// 响应式数据
const merchantName = ref<string>('HenPay')
const avatar = ref(ysfImg)
const amount = ref<number>(1)
const resData = ref<Record<string, any>>({})
const router = useRouter()

// 声明支付宝 JSBridge 类型
declare const AlipayJSBridge: {
  call: (
    method: 'tradePay',
    params: { tradeNO: string },
    callback: (data: AlipayTradeResult) => void
  ) => void
}

// 支付逻辑
const pay = async () => {
  if (isNaN(amount.value) || amount.value <= 0) {
    alert('请输入金额')
    return
  }

  try {
    const res = await getPayPackage(amount.value)
    resData.value = res

    if (!window.AlipayJSBridge) {
      document.addEventListener('AlipayJSBridgeReady', () => {
        handleAlipayPayment(res.alipayTradeNo)
      }, { once: true })
    } else {
      handleAlipayPayment(res.alipayTradeNo)
    }
  } catch (error) {
    const err = error as IResponseData
    await router.push({
      name: config.errorPageRouteName,
      params: { errInfo: err.msg }
    })
  }
}

// 支付宝支付处理
const handleAlipayPayment = (tradeNo: string) => {
  AlipayJSBridge.call("tradePay", { tradeNO: tradeNo }, (data) => {
    if (data.resultCode === '9000') {
      alert('支付成功！')
    } else {
      console.error('支付失败:', data.memo)
    }
  })
}

// 生命周期钩子
onMounted(() => {
  // 自动调起支付（如需启用请取消注释）
  // pay()
})
</script>

<style lang="css" scoped>
 @import './pay.css';
</style>
