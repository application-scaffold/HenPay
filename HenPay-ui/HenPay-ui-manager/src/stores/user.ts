import { defineStore } from 'pinia'
import storageUtil from '@/utils/storageUtil'
import { getCurrentUserInfo } from '@/api/login'

// 定义用户信息接口
interface UserInfo {
  avatarUrl: string;
  realname: string;
  userName: string;
  sysUserId: string;
  avatarImgPath: string;
  allMenuRouteTree: any[]; // 建议替换为具体路由类型
  entIdList: string[];     // 根据实际权限标识类型调整
  accessList: string[];    // 根据实际权限类型调整
  isAdmin: boolean;        // 修正为boolean类型更合理
  loginUsername: string;
  state: 'active' | 'inactive'; // 枚举状态
  sysType: string;
  telphone: string;
  sex: 'male' | 'female' | 'unknown';
}

// 定义Store状态结构
interface UserState {
  userInfo: UserInfo;
  globalLoading: boolean;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: {
      avatarUrl: '',
      realname: '',
      userName: '',
      sysUserId: '',
      avatarImgPath: '',
      allMenuRouteTree: [],
      entIdList: [],
      accessList: [],
      isAdmin: false,
      loginUsername: '',
      state: 'active',
      sysType: '',
      telphone: '',
      sex: 'unknown'
    },
    globalLoading: false
  }),

  actions: {
    putToken(token: string): void {
      storageUtil.setToken(token, true)
    },

    async refUserInfo(): Promise<void> {
      try {
        const bizData = await getCurrentUserInfo()
        this.userInfo = { ...this.userInfo, ...bizData }
      } catch (error) {
        console.error('Failed to refresh user info:', error)
        throw error
      }
    },

    logout(): void {
      storageUtil.cleanToken()
      window.location.reload()
    },

    setGlobalLoading(val: boolean): void {
      this.globalLoading = val
    }
  }
})
