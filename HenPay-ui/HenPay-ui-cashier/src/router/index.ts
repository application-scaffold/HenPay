import { createRouter, createWebHistory } from 'vue-router'
import config from '@/config';
import wayCode from '@/utils/wayCode.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/hub/:jeepayToken', name: 'Hub', component: () => import('../views/Hub.vue') }, //自动分发器
    { path: '/error', name: 'Error', component: () => import('../views/Error.vue') },
    {
      path: '/oauth2Callback/:jeepayToken',
      name: 'Oauth2Callback',
      component: () => import('../views/Oauth2Callback.vue')
    }, //oauth回调地址
    {
      path: '/cashier', name: 'Cashier', component: () => import('../views/Cashier.vue'), //收银台（该地址无意义）
      children: [
        {
          path: '/cashier/wxpay',
          name: 'CashierWxpay',
          component: () => import('../views/payway/Wxpay.vue')
        },
        {
          path: '/cashier/alipay',
          name: 'CashierAlipay',
          component: () => import('../views/payway/Alipay.vue')
        },
        {
          path: '/cashier/ysfpay',
          name: 'CashierYsfpay',
          component: () => import('../views/payway/Ysfpay.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {

  console.log('进入路由守卫', from, to)

  // from: 当前导航正要离开的路由
  // to: 即将要进入的目标路由对象

  // 如果在免登录页面则直接放行
  if (config.errorPageRouteName.includes(<string>to.name)) {
    // 在免登录名单，直接进入
    next()
    return false
  }

  //获取不到参数
  let token = to.params[config.urlTokenName]
  // let token = 'test';  // 不提交
  if (token) {  //放置token信息
    if (typeof token === 'string') {
      config.cacheToken = token
    }
  }

  if (!config.cacheToken) {
    next({ name: config.errorPageRouteName, params: { errInfo: '请通过二维码进入支付页面！' } })
    return false
  }

  //获取不到支付类型, 需要跳转到错误页面
  if (!wayCode.getPayWay()) {
    next({
      name: config.errorPageRouteName,
      params: { errInfo: '不支持的支付方式！ 请在微信/支付宝/银联应用内扫码进入！' }
    })
    return false
  }

  next()
})

export default router
