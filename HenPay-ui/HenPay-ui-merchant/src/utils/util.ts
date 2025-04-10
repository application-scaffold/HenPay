export function timeFix() {
  const hour = new Date().getHours();
  return hour < 9 ? '早上好' : hour <= 11 ? '上午好' : hour <= 13 ? '中午好' : hour < 20 ? '下午好' : '晚上好';
}

export function isIE() {
  const ie11 = 'ActiveXObject' in window;
  return /MSIE/.test(navigator.userAgent) || ie11;
}
