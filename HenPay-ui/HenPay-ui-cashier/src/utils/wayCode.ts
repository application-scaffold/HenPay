/**
 * 获取支付方式工具类
 */
import config, { type PayWayItem } from '@/config'

// 获取支付方式（带完整类型注解）
export function getPayWay(): PayWayItem | null {
  const userAgent = navigator.userAgent;

  if (/MicroMessenger/i.test(userAgent)) {
    return config.payWay.WXPAY;
  }

  if (/AlipayClient/i.test(userAgent)) {
    return config.payWay.ALIPAY;
  }

  return null;
}

// 获取路由名称（带类型守卫）
export function getToPageRouteName(): string | null {
  const payWay = getPayWay();
  return payWay?.routeName ?? null;
}

// 导出对象（保持原有导出结构）
export default {
  getToPageRouteName,
  getPayWay
};
