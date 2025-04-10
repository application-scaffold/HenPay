import storage from 'store'
import expirePlugin from 'store/plugins/expire'
import appConfig from '@/config/appConfig'

// 添加过期插件
// storage.addPlugin(expirePlugin)

// 定义Token包装器类型契约
interface StorageUtil {
  getToken: () => string;
  cleanToken: () => void;
  setToken: (tokenVal: string, isSaveStorage?: boolean) => void;
}

// 使用类型明确的存储键名
const ACCESS_TOKEN_NAME = appConfig.ACCESS_TOKEN_NAME as string;

// 内存中的Token副本
let SESSION_TOKEN: string = '';

const storageUtil: StorageUtil = {
  /* 获取当前Token（优先内存中的副本） */
  getToken: (): string => {
    return SESSION_TOKEN || storage.get(ACCESS_TOKEN_NAME) || '';
  },

  /* 清空所有存储位置的Token */
  cleanToken: (): void => {
    SESSION_TOKEN = '';
    storage.remove(ACCESS_TOKEN_NAME);
  },

  /* 设置Token并可选持久化 */
  setToken: (tokenVal: string, isSaveStorage: boolean = false): void => {

    SESSION_TOKEN = tokenVal;
    if (isSaveStorage) {
      // 7天有效期（毫秒）
      // const maxAge = 7 * 24 * 60 * 60 * 1000;
      storage.set(ACCESS_TOKEN_NAME, tokenVal);
    }
  }
};

export default storageUtil;
