/// <reference types="vitest" />
import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig(
  ({ mode }) => {
    const env = loadEnv(mode, process.cwd())
    return {
      plugins: [
        vue(),
        vueJsx(),
        vueDevTools(),
        Components({
          resolvers: [
            AntDesignVueResolver({
              importStyle: false, // 加载源文件而非编译后的 CSS
              resolveIcons: false   // 关键：启用图标自动导入，因为菜单是通过配置动态导入的，不能启动按需导入
            })
          ]
        })
      ],
      base: env.VITE_APP_BASE_URL,
      resolve: {
        alias: {
          '@': fileURLToPath(new URL('./src', import.meta.url))
        }
      },
      css: {
        preprocessorOptions: {
          less: {
            additionalData: `@import "@/less/color.less";`, // 在每个 Less 文件编译前自动注入指定内容
            modifyVars: { // 覆盖 Ant Design 变量，动态修改 Less 变量，实现 UI 库主题定制
              '@primary-color': '#1890ff', // 全局主色
              '@link-color': '#1890ff', // 链接色
              '@success-color': '#52c41a', // 成功色
              '@warning-color': '#faad14', // 警告色
              '@primary-glass': '#2691ff26', // 半透明主题色
              '@background-color': '#F2F5F7', //light全局背景色
              '@error-color': '#f5222d', // 错误色
              '@font-size-base': '14px', // 主字号
              '@heading-color': 'rgba(0, 0, 0, 0.85)', // 标题色
              '@text-color': 'rgba(0, 0, 0, 0.65)', // 主文本色
              '@text-color-secondary': 'rgba(0, 0, 0, 0.45)', // 次文本色
              '@disabled-color': 'rgba(0, 0, 0, 0.25)', // 失效色
              '@border-radius-base': '4px', // 组件/浮层圆角
              '@border-color-base': '#d9d9d9', // 边框色
              '@box-shadow-base': '0 2px 8px rgba(0, 0, 0, 0.15)' // 浮层阴影
            },
            // DO NOT REMOVE THIS LINE
            javascriptEnabled: true // Ant Design 的 Less 主题系统依赖 Less 的 JS 解释功能，必须开启
          }
        }
      }
    }
  })
