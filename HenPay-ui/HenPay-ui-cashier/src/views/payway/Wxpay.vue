<template>
  <div>
    <header class="header">
      <div class="header-text">付款给{{ payOrderInfo.mchName }}</div>
      <div class="header-img">
        <img :src="avatar ? avatar : icon_member_default" alt="" />
      </div>
    </header>
    <div class="plus-input">
      <!-- ￥字符 货币的符号-->
      <div class="S">
        <img src="../../assets/icon/S.svg" alt="" />
      </div>

      <!-- 输入框光标 -->
      <!-- <div class="ttt">
        <div
          class="input-c-div"
          style="background: #07c160"
        ></div>
      </div> -->

      <!-- 手写输入框 -->
      <div  class="input-c"  style="width: 100%">
        <div  v-if="payOrderInfo.amount" class="input-c-div-1">{{ payOrderInfo.amount/100 }}</div>
        <input style="height: 120px;"  type="number" v-else v-model="amount" placeholder="请输入金额">
        <!-- 数字金额后边的光标 -->
        <!-- <div class="input-c-div" style="background:#07c160"></div> -->
      </div>
      <!-- 手写输入框的提示文字 -->
      <!-- <div v-show="!amount" class="placeholder">请输入金额</div> -->
    </div>
    <ul class="plus-ul" >
      <!-- 支付板块 -->
      <li
        style="border-radius:10px;"
      >
        <!-- 支付金额板块 -->
        <div class="img-div">
          <img :src="wxImg" alt="" />
          <div class="div-text">
            微信支付
          </div>
        </div>
      </li>
    </ul>
    <!-- 备注板块 ，目前不需要添加备注，隐藏-->
    <!-- <div class="remark-k" :class="payType != 'wx' ? 'margin-top-30' : ''">
      <div class="remark">
        <div class="remark-hui" v-show="remark">{{ remark }}</div>
        <div @click="myDialogStateFn">{{ remark ? "修改" : "添加备注" }}</div>
      </div>
    </div> -->
    <!-- dialog 对话框 目前不需要添加备注，隐藏-->
    <!-- <MyDialog
      v-show="myDialogState"
      @myDialogStateFn="myDialogStateFn"
      :remark="remark"
    >
    </MyDialog> -->

    <!-- 键盘板块 目前不需要键盘 隐藏 -->
    <!-- <div class="keyboard-plus" v-if="isAllowModifyAmount">
      <Keyboard
        @delTheAmount="delTheAmount"
        @conceal="conceal"
        @enterTheAmount="enterTheAmount"
        @payment="payment"
        :money="money"
        :concealSate="concealSate"
        :typeColor="typeColor[payType]"
      ></Keyboard>
    </div> -->

    <!-- jeepay中，付款的点击事件 由 payment 修改为 pay  -->
    <!-- jeepay中，付款页面是唯一的，颜色不在需要v-bind，去掉即可 -->
    <!-- <div class="bnt-pay" v-if="!isAllowModifyAmount"> -->
    <div class="bnt-pay">
      <div
        class="bnt-pay-text"
        style="background-color:#07c160"
        @click="pay"
      >
        付款
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Composition API 重构
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPayPackage, getPayOrderInfo } from '@/api/api'
import config from '@/config'

// 类型声明
interface PayOrderInfo {
  amount?: number
  returnUrl?: string
}

interface ResData {
  code: string
  data: {
    orderState: number
    errMsg?: string
    payInfo: string
  }
  msg?: string
}

// 响应式数据
const merchantName = ref<string>('HenPay') // 付款的商户默认
const avatar = ref(require("../../assets/icon/wx.svg")) // 商户头像默认
const amount = ref<number>(0)  // 支付金额默认
const resData = ref<ResData>({} as ResData)
const wxImg = ref(require("../../assets/icon/wx.svg")) // 微信支付图片
const payOrderInfo = ref<PayOrderInfo>({}) //订单信息

// 路由实例
const router = useRouter()

// 生命周期钩子
onMounted(() => {
  setPayOrderInfo()
})

// 获取订单信息
const setPayOrderInfo = async () => {
  try {
    const res = await getPayOrderInfo()
    payOrderInfo.value = res

    if (res.amount) {
      amount.value = res.amount / 100
      await pay()
    }
  } catch (error: any) {
    await router.push({
      name: config.errorPageRouteName,
      params: { errInfo: error.msg }
    })
  }
}

// 支付逻辑
const pay = async () => {
  if (!amount.value || isNaN(amount.value) || amount.value <= 0) {
    return alert('请输入有效金额')
  }

  try {
    const res = await getPayPackage(amount.value)

    if (res.code !== '0') {
      alert(res.msg)
      return
    }

    if (res.data.orderState !== 1) {
      alert(res.data.errMsg)
      return
    }

    resData.value = res.data
    initWechatPayment()
  } catch (error: any) {
    await router.push({
      name: config.errorPageRouteName,
      params: { errInfo: error.msg }
    })
  }
}

// 微信支付初始化
const initWechatPayment = () => {
  if (typeof WeixinJSBridge === 'undefined') {
    const eventMethod = document.addEventListener ? 'addEventListener' : 'attachEvent'
    const handler = () => onBridgeReady()

    document[eventMethod]('WeixinJSBridgeReady', handler, false)
    document[eventMethod]('onWeixinJSBridgeReady', handler)
  } else {
    onBridgeReady()
  }
}

// 微信支付回调
const onBridgeReady = () => {
  WeixinJSBridge.invoke(
    'getBrandWCPayRequest',
    JSON.parse(resData.value.data.payInfo),
    (res: { err_msg: string }) => {
      const { err_msg } = res
      const actions: Record<string, () => void> = {
        'get_brand_wcpay_request:ok': () => handlePaymentSuccess(),
        'get_brand_wcpay_request:cancel': () => alert("支付取消"),
        'get_brand_wcpay_request:fail': () => alert('支付失败'),
        'total_fee': () => alert('缺少参数')
      }

      actions[err_msg]?.()
      window.WeixinJSBridge.call('closeWindow')
    }
  )
}

// 支付成功处理
const handlePaymentSuccess = () => {
  if (payOrderInfo.value.returnUrl) {
    location.href = payOrderInfo.value.returnUrl
  } else {
    alert('支付成功！')
  }
}
</script>
<style lang="css" scoped>
 @import './pay.css';
</style>
