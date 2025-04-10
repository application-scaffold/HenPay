import request from '@/http/request';
import type { IResponseData } from '@/http/request'; // 从已有位置导入类型
import wayCode from "@/utils/wayCode";
import { channelUserIdStorage } from '@/utils/channelUserId'
import config from '@/config';


// 定义所有接口的请求/响应类型
interface RedirectUrlResponse {
  redirectUrl: string;
}

interface ChannelUserIdParams {
  [key: string]: any; // 根据实际参数结构细化
  authCode?: string;
  state?: string;
}

interface PayPackageParams {
  amount: number;
}

interface PayOrderInfoResponse {
  orderId: string;
  status: number;
  amount: number;
}

/** 获取跳转URL */
export function getRedirectUrl(): Promise<IResponseData<RedirectUrlResponse>> {
  return request.request({
    url: '/api/cashier/redirectUrl',
    method: 'POST',
    data: {
      wayCode: wayCode.getPayWay()?.wayCode,
      token: config.cacheToken
    }
  });
}

/** 获取渠道用户ID */
export function getChannelUserId(
    redirectData: ChannelUserIdParams
): Promise<IResponseData<{ channelUserId: string }>> {
  return request.request({
    url: '/api/cashier/channelUserId',
    method: 'POST',
    data: {
      ...redirectData,
      wayCode: wayCode.getPayWay()?.wayCode,
      token: config.cacheToken
    }
  });
}

/** 获取支付数据包 */
export function getPayPackage(
    amount: number
): Promise<IResponseData<{ package: string }>> {
  return request.request({
    url: '/api/cashier/pay',
    method: 'POST',
    data: {
      wayCode: wayCode.getPayWay()?.wayCode,
      token: config.cacheToken,
      amount,
      channelUserId: channelUserIdStorage.get()
    }
  });
}

/** 获取订单信息 */
export function getPayOrderInfo(): Promise<IResponseData<PayOrderInfoResponse>> {
  return request.request({
    url: '/api/cashier/payOrderInfo',
    method: 'POST',
    data: { token: config.cacheToken }
  });
}

/** 取消订单 */
export function cancelPay(): Promise<IResponseData<void>> {
  return request.request({
    url: '/api/cashier/cancelPay',
    method: 'POST',
    data: { token: config.cacheToken }
  });
}
