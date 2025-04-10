export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null;

  return function (this: ThisParameterType<T>, ...args: Parameters<T>): void {
    const context = this;
    clearTimeout(timer!); // 使用非空断言，因下方逻辑确保 timer 存在

    timer = setTimeout(() => {
      fn.apply(context, args);
      timer = null;
    }, delay);
  };
}

interface ThrottleOptions<T extends (...args: any[]) => any> {
  trailing?: boolean;
  result?: (res: ReturnType<T>) => void;
}

// 定义合并了函数调用和 cancel 方法的接口
interface ThrottledFunction<T extends (...args: any[]) => any> {
  (...args: Parameters<T>): void;
  cancel: () => void;
}

export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  interval: number,
  options?: ThrottleOptions<T>
): ThrottledFunction<T> {
  let last: number = 0;
  let timer: ReturnType<typeof setTimeout> | null = null;

  const { trailing = false, result } = options || {};

  // 使用类型断言为 ThrottledFunction<T>
  const handleFn = function (
    this: ThisParameterType<T>,
    ...args: Parameters<T>
  ): void {
    const context = this;
    const now = Date.now();

    if (now - last >= interval) {
      if (timer) {
        clearTimeout(timer);
        timer = null;
      }
      callFn(context, args);
      last = now;
    } else if (trailing && !timer) {
      timer = setTimeout(() => {
        timer = null;
        callFn(context, args);
      }, interval - (now - last));
    }
  } as ThrottledFunction<T>;

  handleFn.cancel = () => {
    if (timer) {
      clearTimeout(timer);
      timer = null;
    }
  };

  function callFn(context: ThisParameterType<T>, args: Parameters<T>): void {
    const res = fn.apply(context, args);
    if (result) {
      result(res);
    }
  }

  return handleFn;
}
