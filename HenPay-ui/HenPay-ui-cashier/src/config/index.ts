// 支付方式类型定义
type PayWayKey = 'WXPAY' | 'ALIPAY';
export interface PayWayItem {
  wayCode: string;
  routeName: string;
}

/*
 * 配置主体文件（config.ts）
 */
// 配置项接口
interface AppConfig {
  readonly errorPageRouteName: string;
  readonly passGuardRouteList: string[];
  readonly urlTokenName: string;
  readonly payWay: Record<PayWayKey, PayWayItem>;
  cacheToken: string; // 允许后续修改的字段
}


const errorPageRouteName = 'Error' as const //错误页面名称定义
const passGuardRouteList = [errorPageRouteName] as const  // 不进入路由守卫的name

/** 定义支付方式 (使用常量枚举确保类型安全）**/
const payWay = {
  WXPAY : {wayCode: "WX_JSAPI", routeName: "CashierWxpay"},
  ALIPAY : {wayCode: "ALI_JSAPI", routeName: "CashierAlipay"}
} as const;

// 配置导出对象
const config: AppConfig = {
  errorPageRouteName,
  passGuardRouteList: [...passGuardRouteList], // 解构转换确保类型匹配
  urlTokenName: "jeepayToken",
  payWay,
  cacheToken: ""
};

export default config;
