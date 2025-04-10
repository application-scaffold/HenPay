import request, { type IResponseData } from '@/http/request'

type RequestMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

const createRequest = <T>(
  config: {
    url: string
    method: RequestMethod
    params?: Record<string, any>
    data?: any
  },
  showSuccess = true,
  showError = true,
  showLoading = false
): Promise<T> => {
  return request.request<T>({
    ...config
  }, showSuccess, showError, showLoading)
}

/*
 *  全系列 restful api格式, 定义通用req对象
 */
export const req = {
  // 通用列表查询接口
  list: <T>(url: string, params?: Record<string, any>) =>
    createRequest<T>({ url, method: 'GET', params }, true, true, false),

  // 通用新增接口
  add: <T>(url: string, data: any) =>
    createRequest<T>({ url, method: 'POST', data }, true, true, false),

  // 通用查询单条数据接口
  getById: <T>(url: string, bizId: string | number) =>
    createRequest<T>({ url: `${url}/${bizId}`, method: 'GET' }, true, true, false),

  // 通用修改接口
  updateById: <T>(url: string, bizId: string | number, data: any) =>
    createRequest<T>({ url: `${url}/${bizId}`, method: 'PUT', data }, true, true, false),

  // 通用删除接口
  delById: <T>(url: string, bizId: string | number) =>
    createRequest<T>({ url: `${url}/${bizId}`, method: 'DELETE' }, true, true, false)
}

// 全系列 restful api格式 (全局loading方式)
export const reqLoad = {
  // 通用列表查询接口
  list: <T>(url: string, params?: Record<string, any>) =>
    createRequest<T>({ url, method: 'GET', params }, true, true, true),

  // 通用新增接口
  add: <T>(url: string, data: any) => createRequest<T>({url, method: 'POST', data}, true, true, true),

  // 通用查询单条数据接口
  getById: <T>(url: string, bizId: string | number) => createRequest<T>({ url: `${url}/${bizId}`, method: 'GET' }, true, true, true),

  // 通用修改接口
  updateById: <T>(url: string, bizId: string | number, data: any) =>
    createRequest<T>({ url: `${url}/${bizId}`, method: 'PUT', data }, true, true, true),

  // 通用删除接口
  delById: <T>(url: string, bizId: string | number) =>
    createRequest<T>({ url: `${url}/${bizId}`, method: 'DELETE' }, true, true, true)

}

/** 角色管理页面 **/
export const API_URL_ENT_LIST = '/api/sysEnts'
export const API_URL_ROLE_LIST = '/api/sysRoles'
export const API_URL_ROLE_ENT_RELA_LIST = '/api/sysRoleEntRelas'
export const API_URL_SYS_USER_LIST = '/api/sysUsers'
export const API_URL_USER_ROLE_RELA_LIST = '/api/sysUserRoleRelas'
/** 首页统计 **/
export const API_URL_MAIN_STATISTIC = 'api/mainChart'

/** 商户App管理 **/
export const API_URL_MCH_APP = '/api/mchApps'
/** 支付订单管理 **/
export const API_URL_PAY_ORDER_LIST = '/api/payOrder'
/** 退款订单管理 **/
export const API_URL_REFUND_ORDER_LIST = '/api/refundOrder'
/** 支付方式列表 **/
export const API_URL_PAYWAYS_LIST = '/api/payWays'
/** 商户支付参数配置 **/
export const API_URL_MCH_PAYCONFIGS_LIST = '/api/mch/payConfigs'
/** 商户支付通道配置 **/
export const API_URL_MCH_PAYPASSAGE_LIST = '/api/mch/payPassages'
/** 转账订单管理 **/
export const API_URL_TRANSFER_ORDER_LIST = '/api/transferOrders'

/** 分账组管理 **/
export const API_URL_DIVISION_RECEIVER_GROUP = '/api/divisionReceiverGroups'

/** 分账账号管理 **/
export const API_URL_DIVISION_RECEIVER = '/api/divisionReceivers'

/** 分账记录管理 **/
export const API_URL_PAY_ORDER_DIVISION_RECORD_LIST = '/api/division/records'

type UploadType = 'avatar' | 'cert'
/** 上传图片/文件地址 **/
export const upload: Record<UploadType, string> = {
  avatar: `${request.baseConfig().baseURL}/api/ossFiles/avatar`,
  cert: `${request.baseConfig().baseURL}/api/ossFiles/cert`
}

const api = {
  user: '/user',
  role_list: '/role',
  service: '/service',
  permission: '/permission',
  permissionNoPager: '/permission/no-pager',
  orgTree: '/org/tree'
}

export default api

/**
 * 获取权限树状结构图
 */
export function getEntTree(sysType: string): Promise<IResponseData> {
  return request.request<IResponseData>({
    url: `/api/sysEnts/showTree?sysType=${encodeURIComponent(sysType)}`,
    method: 'GET'
  });
}

/** 更新用户角色信息 */
export function uSysUserRoleRela(
  sysUserId: string,
  roleIdList: string[]
): Promise<IResponseData> {
  return request.request({
    url: `api/sysUserRoleRelas/relas/${sysUserId}`,
    method: 'POST',
    data: {
      roleIdListStr: JSON.stringify(roleIdList)
    }
  });
}

export function getRoleList(
  parameter: { page?: number; size?: number } & Record<string, unknown>
): Promise<IResponseData> {
  return request.request({
    url: '/api/sysRoles',
    method: 'get',
    params: parameter
  });
}

/** 根据支付接口查询支付参数配置 **/
export function getMchPayConfigUnique (infoId: string, ifCode: string) {
  return request.request({
    url: `/api/mch/payConfigs/${infoId}/${ifCode}`,
    method: 'get'
  })
}

/** 支付体验配置 **/
export function payTest (appId: string) {
  return request.request({
    url: `api/paytest/payways/${appId}`,
    method: 'GET'
  })
}

/** 支付体验下单配置 **/
export function payTestOrder (parameter: any) {
  return request.request({
    url: '/api/paytest/payOrders',
    method: 'POST',
    data: parameter
  })
}

/** 根据支付方式查询可用支付接口 **/
export function getAvailablePayInterfaceList (appId: string, wayCode: string) {
  return request.request({
    url: `/api/mch/payPassages/availablePayInterface/${appId}/${wayCode}`,
    method: 'GET'
  })
}

export function getPayAmountWeek () {
  return request.request({
    url: API_URL_MAIN_STATISTIC + '/payAmountWeek',
    method: 'GET'
  })
}

export function getNumCount () {
  return request.request({
    url: API_URL_MAIN_STATISTIC + '/numCount',
    method: 'GET'
  })
}

export function getPayCount (parameter: any) {
  return request.request({
    url: API_URL_MAIN_STATISTIC + '/payCount',
    method: 'GET',
    params: parameter
  })
}

export function getPayType (parameter: any) {
  return request.request({
    url: API_URL_MAIN_STATISTIC + '/payTypeCount',
    method: 'GET',
    params: parameter
  })
}

export function getMainUserInfo () {
  return request.request({
    url: API_URL_MAIN_STATISTIC,
    method: 'GET'
  })
}

export function updateUserPass (parameter: any) {
  return request.request({
    url: '/api/current/modifyPwd',
    method: 'put',
    data: parameter
  })
}

export function updateUserInfo (parameter: any) {
  return request.request({
    url: '/api/current/user',
    method: 'put',
    data: parameter
  })
}

export function getUserInfo () {
  return request.request({
    url: '/api/current/user',
    method: 'get'
  })
}

/** 获取到webSocket的前缀 （ws://localhost） **/
export function getWebSocketPrefix () {
  // 获取网站域名 +  端口号
  let domain = document.location.protocol + '//' + document.location.host

  // 判断api_base_url 是否设置 import.meta.env.VITE_API_BASE_URL
  if (import.meta.env.VITE_API_BASE_URL && import.meta.env.VITE_API_BASE_URL !== '/') {
    domain = import.meta.env.VITE_API_BASE_URL
  }

  if (domain.startsWith('https:')) {
    return 'wss://' + domain.replace('https://', '')
  } else {
    return 'ws://' + domain.replace('http://', '')
  }
}

/** 查询支付宝授权地址URL **/
export function queryAlipayIsvsubMchAuthUrl (mchAppId: string) {
  return request.request({
    url: `/api/mch/payConfigs/alipayIsvsubMchAuthUrls/${mchAppId}`,
    method: 'GET'
  })
}

/** 查询商户转账支出的接口 **/
export function queryMchTransferIfCode (appId: string) {
  return request.request({
    url: `api/mchTransfers/ifCodes/${appId}`,
    method: 'GET'
  })
}

/** 获取渠道用户ID二维码地址 **/
export function getChannelUserQrImgUrl (ifCode: any, appId: any, extParam: any) {
  return request.request({
    url: '/api/mchTransfers/channelUserId',
    method: 'GET',
    params: { ifCode, appId, extParam }
  })
}

/** 转账 **/
export function doTransfer (parameter: any) {
  return request.request({
    url: '/api/mchTransfers/doTransfer',
    method: 'POST',
    data: parameter
  }, true, true, true)
}

/** 查询当前应用支持的支付接口 **/
export function getIfCodeByAppId (appId: string) {
  return request.request({
    url: `/api/mch/payConfigs/ifCodes/${appId}`,
    method: 'GET'
  }, true, true, true)
}

/** 退款接口 */
export function payOrderRefund(
  payOrderId: string,
  refundAmount: number,
  refundReason: string
): Promise<IResponseData> {
  return request.request<IResponseData>({
    url: `/api/payOrder/refunds/${payOrderId}`,
    method: 'POST',
    data: {
      refundAmount: refundAmount.toFixed(2), // 金额格式化处理
      refundReason
    }
  });
}

/** 分账重试 */
export function resendDivision ( recordId: string ) {
  return request.request({
    url: `/api/division/records/resend/${recordId}`,
    method: 'POST'
  })
}
