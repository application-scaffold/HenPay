/**
 * 全局配置信息，包含网站标题，动态组件定义
 */

import type { DefineComponent } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

// 应用配置类型
interface AppConfig {
  APP_TITLE: string
  ACCESS_TOKEN_NAME: string
}

/** 应用配置项 **/
const appConfig: AppConfig = {
  APP_TITLE: 'Henpay运营平台',
  ACCESS_TOKEN_NAME: 'iToken'
}

// 异步路由项类型
interface AsyncRouteItem {
  defaultPath: string
  component: () => Promise<DefineComponent> | RouteRecordRaw['component']  // 直接引用组件类型
}

// 动态路由配置类型
type AsyncRouteDefine = Record<string, AsyncRouteItem>

/**
 * 与后端开发人员的路由名称及配置项
 * 组件名称：{ 默认跳转路径，组件渲染 }
 */
const asyncRouteDefine: AsyncRouteDefine = {

  'CurrentUserInfo': {
    defaultPath: '/current/userinfo',
    component: () => import('@/views/current/UserinfoPage.vue')
  }, // 用户设置

  'MainPage': {
    defaultPath: '/main',
    component: () => import('@/views/dashboard/Analysis.vue')
  },

  'SysUserPage': {
    defaultPath: '/users',
    component: () => import('@/views/sysuser/SysUserPage.vue')
  },

  'RolePage': {
    defaultPath: '/roles',
    component: () => import('@/views/role/RolePage.vue')
  },

  'EntPage': {
    defaultPath: '/ents',
    component: () => import('@/views/ent/EntPage.vue')
  },

  'PayWayPage': {
    defaultPath: '/payways',
    component: () => import('@/views/payconfig/payWay/List.vue')
  },

  'IfDefinePage': {
    defaultPath: '/ifdefines',
    component: () => import('@/views/payconfig/payIfDefine/List.vue')
  },

  'IsvListPage': {
    defaultPath: '/isv',
    component: () => import('@/views/isv/IsvList.vue')
  }, // 服务商列表

  'MchListPage': {
    defaultPath: '/mch',
    component: () => import('@/views/mch/MchList.vue')
  }, // 商户列表

  'MchAppPage': {
    defaultPath: '/apps',
    component: () => import ('@/views/mchApp/List.vue')
  }, // 商户应用列表

  'PayOrderListPage': {
    defaultPath: '/payOrder',
    component: () => import('@/views/order/pay/PayOrderList.vue')
  }, // 支付订单列表

  'RefundOrderListPage': {
    defaultPath: '/refundOrder',
    component: () => import('@/views/order/refund/RefundOrderList.vue')
  }, // 退款订单列表

  'TransferOrderListPage': {
    defaultPath: '/transferOrder',
    component: () => import('@/views/order/transfer/TransferOrderList.vue')
  }, // 转账订单

  'MchNotifyListPage': {
    defaultPath: '/notify',
    component: () => import('@/views/order/notify/MchNotifyList.vue')
  }, // 商户通知列表

  'SysConfigPage': {
    defaultPath: '/config',
    component: () => import('@/views/sys/config/SysConfig.vue')
  }, // 系统配置

  'SysLogPage': {
    defaultPath: '/log',
    component: () => import('@/views/sys/log/SysLog.vue')
  } // 系统日志
}

// 类型导出
export type { AppConfig, AsyncRouteItem, AsyncRouteDefine }

// 默认导出配置
export default appConfig
export { asyncRouteDefine }
