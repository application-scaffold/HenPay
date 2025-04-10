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

/** 服务商、商户管理 **/
export const API_URL_ISV_LIST = '/api/isvInfo'
export const API_URL_MCH_LIST = '/api/mchInfo'
/** 商户App管理 **/
export const API_URL_MCH_APP = '/api/mchApps'
/** 支付订单管理 **/
export const API_URL_PAY_ORDER_LIST = '/api/payOrder'
/** 退款订单管理 **/
export const API_URL_REFUND_ORDER_LIST = '/api/refundOrder'
/** 商户通知管理 **/
export const API_URL_MCH_NOTIFY_LIST = '/api/mchNotify'
/** 系统日志 **/
export const API_URL_SYS_LOG = 'api/sysLog'
/** 系统配置 **/
export const API_URL_SYS_CONFIG = 'api/sysConfigs'
/** 首页统计 **/
export const API_URL_MAIN_STATISTIC = 'api/mainChart'

/** 支付接口定义页面 **/
export const API_URL_IFDEFINES_LIST = '/api/payIfDefines'
export const API_URL_PAYWAYS_LIST = '/api/payWays'
/** 服务商、商户支付参数配置 **/
export const API_URL_ISV_PAYCONFIGS_LIST = '/api/isv/payConfigs'
export const API_URL_MCH_PAYCONFIGS_LIST = '/api/mch/payConfigs'
/** 商户支付通道配置 **/
export const API_URL_MCH_PAYPASSAGE_LIST = '/api/mch/payPassages'
/** 转账订单管理 **/
export const API_URL_TRANSFER_ORDER_LIST = '/api/transferOrders'

type UploadType = 'avatar' | 'ifBG' | 'cert'
/** 上传图片/文件地址 **/
export const upload: Record<UploadType, string> = {
  avatar: `${request.baseConfig().baseURL}/api/ossFiles/avatar`,
  ifBG: `${request.baseConfig().baseURL}/api/ossFiles/ifBG`,
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
export function getEntTree(sysType: string): Promise<any[]> {
  return request.request<any[]>({
    url: `/api/sysEnts/showTree?sysType=${encodeURIComponent(sysType)}`,
    method: 'GET'
  });
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

export function getServiceList(
  parameter: any
): Promise<IResponseData> {
  return request.request({
    url: api.service,
    method: 'get',
    params: parameter
  });
}

export function getPermissions (parameter: any) {
  return request.request({
    url: api.permissionNoPager,
    method: 'get',
    params: parameter
  });
}

export function getOrgTree (parameter: any) {
  return request.request({
    url: api.orgTree,
    method: 'get',
    params: parameter
  });
}

// id == 0 add     post
// id != 0 update  put
export function saveService (parameter: any) {
  return request.request({
    url: api.service,
    method: parameter.id === 0 ? 'post' : 'put',
    data: parameter
  });
}

export function saveSub (sub: any) {
  return request.request({
    url: '/sub',
    method: sub.id === 0 ? 'post' : 'put',
    data: sub
  });
}

export function getIsvPayConfigUnique (infoId: string, ifCode: string) {
  return request.request({
    url: `/api/isv/payConfigs/${infoId}/${ifCode}`,
    method: 'get'
  })
}

export function getMchPayConfigUnique (infoId: string, ifCode: string) {
  return request.request({
    url: `/api/mch/payConfigs/${infoId}/${ifCode}`,
    method: 'get'
  })
}

export function getAvailablePayInterfaceList (mchNo: string, wayCode: string) {
  return request.request({
    url: `/api/mch/payPassages/availablePayInterface/${mchNo}/${wayCode}`,
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

export function getMainUserInfo (parameter: any) {
  return request.request({
    url: API_URL_MAIN_STATISTIC + '/' + parameter,
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

export function getConfigs (parameter: any) {
  return request.request({
    url: API_URL_SYS_CONFIG + '/' + parameter,
    method: 'GET'
  })
}

export function getEntBySysType (entId: string , sysType: string) {
  return request.request({
    url: '/api/sysEnts/bySysType',
    method: 'GET',
    params: { entId: entId, sysType: sysType }
  })
}

export function mchNotifyResend (notifyId: string) {
  return request.request({
    url: `/api/mchNotify/resend/${notifyId}`,
    method: 'POST'
  })
}

/** 查询支付宝授权地址URL **/
export function queryAlipayIsvsubMchAuthUrl (mchAppId: string) {
  return request.request({
    url: `/api/mch/payConfigs/alipayIsvsubMchAuthUrls/${mchAppId}`,
    method: 'GET'
  })
}
