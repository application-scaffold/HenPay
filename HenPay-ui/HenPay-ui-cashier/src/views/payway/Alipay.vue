<template>
  <div>
    <header class="header">
      <div class="header-text">付款给 {{ payOrderInfo.mchName }}</div>
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
      <div  class="input-c" style="width: 100%">
        <div  v-if="payOrderInfo.amount" class="input-c-div-1">{{ payOrderInfo.amount/100 }}</div>
        <input type="number" style="height: 120px;" v-else v-model="amount" placeholder="请输入金额">
      </div>
      <!-- 手写输入框的提示文字 -->
      <!-- <div v-show="!amount" class="placeholder">请输入金额</div> -->
    </div>
    <ul class="plus-ul" >
      <!-- 支付板块 -->
      <li style="border-radius:10px;">
        <!-- 支付金额板块 -->
        <div class="img-div">
          <img :src="wxImg" alt="" />
          <div class="div-text">
            支付宝支付
          </div>
        </div>
      </li>
    </ul>
    <div class="bnt-pay">
      <div
        class="bnt-pay-text"
        style="background-color:#1678ff"
        @click="pay"
      >
        付款
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Composition API 实现
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPayPackage, getPayOrderInfo } from '@/api/payment'
import config from '@/config'
import type { IResponseData } from '@/types/api' // 统一类型定义

// 类型声明
interface PayOrderInfo {
  amount?: number
  returnUrl?: string
  // 根据实际接口补充其他字段
}

interface AlipayResponse {
  resultCode: string
  // 其他可能的字段
}

// 路由实例
const router = useRouter()

// 响应式数据
const merchantName = ref<string>('HenPay')
const avatar = ref(require("../../assets/images/zfb.jpeg"))
const amount = ref<number | null>(null)
const payOrderInfo = ref<PayOrderInfo>({})
const wxImg = ref(require("../../assets/images/zfb.jpeg"))

// 生命周期钩子
onMounted(() => {
  setPayOrderInfo()
})

// 获取订单信息
const setPayOrderInfo = async () => {
  try {
    const res = await getPayOrderInfo() as IResponseData<PayOrderInfo>
    payOrderInfo.value = res.data

    if (res.data.amount) {
      amount.value = res.data.amount / 100
      await pay()
    }
  } catch (error: any) {
    await router.push({
      name: config.errorPageRouteName,
      params: { errInfo: error.msg }
    })
  }
}

// 支付流程
const pay = async () => {
  if (!amount.value || amount.value <= 0) {
    return alert('请输入有效金额')
  }

  try {
    const res = await getPayPackage(amount.value) as IResponseData<{
      alipayTradeNo: string
      orderState: number
      errMsg?: string
    }>

    if (res.code !== '0') {
      return alert(res.msg)
    }

    if (res.data.orderState !== 1) {
      return alert(res.data.errMsg || '订单状态异常')
    }

    handleAlipayPayment(res.data.alipayTradeNo)
  } catch (error: any) {
    await router.push({
      name: config.errorPageRouteName,
      params: { errInfo: error.msg }
    })
  }
}

// 支付宝支付处理
const handleAlipayPayment = (tradeNo: string) => {
  if (!window.AlipayJSBridge) {
    document.addEventListener('AlipayJSBridgeReady', () => {
      executeAlipayTrade(tradeNo)
    }, { once: true })
  } else {
    executeAlipayTrade(tradeNo)
  }
}

// 执行支付宝交易
const executeAlipayTrade = (tradeNo: string) => {
  const AlipayJSBridge = window.AlipayJSBridge as {
    call: (
      method: string,
      params: { tradeNO: string },
      callback: (data: AlipayResponse) => void
    ) => void
    closeWebview: () => void
  }

  AlipayJSBridge.call("tradePay", { tradeNO: tradeNo }, (data) => {
    handleAlipayResponse(data)
  })
}

// 处理支付宝响应
const handleAlipayResponse = (data: AlipayResponse) => {
  const resultCodes = ['9000', '8000', '6004']

  if (data.resultCode === '9000') {
    if (payOrderInfo.value.returnUrl) {
      location.href = payOrderInfo.value.returnUrl
    } else {
      alert('支付成功！')
      window.AlipayJSBridge?.call('closeWebview')
    }
  } else if (['8000', '6004'].includes(data.resultCode)) {
    alert(JSON.stringify(data))
    window.AlipayJSBridge?.call('closeWebview')
  } else {
    alert('用户已取消支付')
    window.AlipayJSBridge?.call('closeWebview')
  }
}
</script>

<style lang="css" scoped>
 @import './pay.css';
</style>
