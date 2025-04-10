export function timeFix() {
  const hour = new Date().getHours();
  return hour < 9 ? '早上好' : hour <= 11 ? '上午好' : hour <= 13 ? '中午好' : hour < 20 ? '下午好' : '晚上好';
}

export function isIE() {
  const ie11 = 'ActiveXObject' in window;
  return /MSIE/.test(navigator.userAgent) || ie11;
}

// 定义全局自增ID
var atomicLong = 1
/** 生成自增序列号（不重复） **/
export function genRowKey () {
  return new Date().getTime() + '_' + (atomicLong++)
}
