import { defineStore } from 'pinia'
import storageUtil from '@/utils/storageUtil'
import { getCurrentUserInfo } from '@/api/login'

// 定义用户信息接口
export interface UserInfo {
  userId: string
  realname: string
  user: string
  sysUserId: string
  avatarUrl: string
  allMenuRouteTree: any[] // 可根据实际路由类型替换 any
  entIdList: any[]        // 可替换为具体权限类型
  isAdmin: string
  loginUsername: string
  state: string
  sysType: string
  telphone: string
  sex: string
  mchType: string
  mchLevel: string
  isHasAgentEnt: boolean
  isHasMemberEnt: boolean
  infoNo: string
}

// 定义 Store 状态结构
interface UserState {
  userInfo: UserInfo
  globalLoading: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: {
      userId: '',
      realname: '',
      user: '',
      sysUserId: '',
      avatarUrl: '',
      allMenuRouteTree: [],
      entIdList: [],
      isAdmin: '',
      loginUsername: '',
      state: '',
      sysType: '',
      telphone: '',
      sex: '',
      mchType: '',
      mchLevel: '',
      isHasAgentEnt: false,
      isHasMemberEnt: false,
      infoNo: ''
    },
    globalLoading: false
  }),

  actions: {
    // Token 操作（保持字符串类型）
    putToken(token: string): void {
      storageUtil.setToken(token, true)
    },

    // 用户信息刷新（使用可选参数和明确返回类型）
    async refUserInfo(val?: Partial<UserInfo>): Promise<void> {
      if (val) {
        this.userInfo = { ...this.userInfo, ...val }
        return
      }

      const bizData = await getCurrentUserInfo()
      this.userInfo = { ...bizData } as unknown as UserInfo;
    },

    // 登出操作（移除箭头函数保持 this 上下文）
    logout(): void {
      storageUtil.cleanToken()
      window.location.reload()
    },

    // 全局加载状态控制（明确 boolean 类型）
    setGlobalLoading(val: boolean): void {
      this.globalLoading = val
    }
  }
})
