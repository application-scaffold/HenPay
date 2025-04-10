import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError,
  InternalAxiosRequestConfig
} from 'axios';
import axios from 'axios'
import appConfig from '@/config/appConfig'
import storageUtil from '@/utils/storageUtil'

// 定义响应数据的基础结构
export interface IResponseData<T = any> {
  code: number;
  msg: string;
  data: T;
}

class HttpRequest {
  private readonly baseUrl: string;
  private readonly queue: { [url: string]: boolean }; // 使用索引签名明确键值类型

  constructor(baseUrl: string = import.meta.env.VITE_API_BASE_URL || '') {
    this.baseUrl = baseUrl;
    this.queue = {};
  }

  // 基础配置（明确返回类型）
  public baseConfig(): AxiosRequestConfig {
    const headers: Record<string, string> = {};
    headers[appConfig.ACCESS_TOKEN_NAME] = storageUtil.getToken()
    return {
      baseURL: this.baseUrl,
      headers
    };
  }

  // 清理队列（明确参数类型）
  public destroy(url: string): void {
    delete this.queue[url];
  }

  // 拦截器配置（明确参数类型）
  public interceptors<T>(
    instance: AxiosInstance,
    url: string,
    showErrorMsg: boolean,
    showLoading: boolean
  ): void {
    // 请求拦截
    instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        if (!Object.keys(this.queue).length && showLoading) {
          // 全局 loading 逻辑
        }
        this.queue[url] = true;
        return config;
      },
      (error: AxiosError) => Promise.reject(error)
    );

    // 响应拦截（明确响应类型）
    instance.interceptors.response.use(
      (res: AxiosResponse<IResponseData>) => {
        this.destroy(url);
        const resData = res.data;

        if (resData.code !== 0) {
          if (showErrorMsg) {
            // 错误提示逻辑
          }
          return Promise.reject(resData);
        }
        return resData.data; // 直接返回业务数据
      },
      (error: AxiosError) => {
        this.destroy(url);
        const errorInfo = error.response?.data || error.message;

        if (showErrorMsg) {
          // 错误提示逻辑
        }
        return Promise.reject(errorInfo);
      }
    );
  }

  // 核心请求方法（使用泛型定义响应数据类型）
  public request<T = any>(
    options: AxiosRequestConfig,
    interceptorsFlag: boolean = true,
    showErrorMsg: boolean = true,
    showLoading: boolean = false
  ): Promise<T> {
    const instance: AxiosInstance = axios.create();
    const mergedOptions: AxiosRequestConfig = {
      ...this.baseConfig(),
      ...options
    };

    if (interceptorsFlag) {
      this.interceptors<T>(
        instance,
        mergedOptions.url || '',
        showErrorMsg,
        showLoading
      );
    }

    return instance(mergedOptions);
  }
}

export default HttpRequest;
