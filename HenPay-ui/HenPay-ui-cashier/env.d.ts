/// <reference types="vite/client" />

// 扩展 NodeJS 的 ProcessEnv 接口
declare namespace NodeJS {
  interface ProcessEnv {
    VUE_APP_API_BASE_URL: string;
    NODE_ENV: 'development' | 'production';
  }
}
