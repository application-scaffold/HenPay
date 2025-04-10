import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { VueClipboard } from '@soerenmartius/vue3-clipboard'
import App from './App.vue'
import router from './router'
import './less/global.less'
import infoBox from '@/utils/infoBox'
import type { ConfirmResult } from '@/utils/infoBox.ts'
import { useUserStore } from '@/stores/user.ts'
import * as antIcons from '@ant-design/icons-vue'

const app = createApp(App)

app.provide<ConfirmResult>('infoBox', infoBox);
app.config.globalProperties.$access = function (entId: string) {
  let accessList = useUserStore().userInfo.entIdList

  return accessList && accessList.includes(entId)
}
app.config.globalProperties.$infoBox = infoBox

// 遍历注册所有图标组件
Object.keys(antIcons).forEach((key) => {
  app.component(key, antIcons[key as keyof typeof antIcons])
})

app.use(createPinia())
app.use(router)
app.use(VueClipboard)

app.mount('#app')
