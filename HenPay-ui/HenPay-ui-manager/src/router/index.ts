import { createRouter, createWebHistory, type RouteRecordRaw, type RouteLocationNormalized, type NavigationGuardNext } from 'vue-router'
import NProgress from 'nprogress' // 进度条
import '@/components/NProgress/nprogress.less' // 进度条自定义样式
import { useUserStore } from '@/stores/user'
import storageUtil from '@/utils/storageUtil'
import { generatorDynamicRouter } from '@/router/generator-routers'

// 配置进度条隐藏旋转动画
NProgress.configure({ showSpinner: false })

// 定义根路由类型
const rootRoute: RouteRecordRaw[] = [
  {
    name: 'login',
    path: '/login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '', requiresAuth: false } // 添加元数据增强类型安全
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: rootRoute as RouteRecordRaw[],
  strict: true // 启用严格路由模式
})

// 路由守卫配置
const allowList = ['login', 'register', 'registerResult'] // 白名单
const loginRoutePath = '/login'

// 增强路由守卫类型
router.beforeEach(async (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  NProgress.start()

  // 白名单校验
  if (allowList.includes(to.name as string)) {
    return next()
  }

  const tokenStr = storageUtil.getToken();

  // Token 校验逻辑
  if (!tokenStr && to.path !== loginRoutePath) {
    return next(loginRoutePath)
  }

  // 登录状态校验
  if (tokenStr && to.path === loginRoutePath) {
    return next({ path: from.path || '/' })
  }

  // 用户信息校验及动态路由加载
  const userStore = useUserStore()
  if (!userStore.userInfo?.sysUserId) {
    try {
      await userStore.refUserInfo()
      const dynamicRoutes = await generatorDynamicRouter() as RouteRecordRaw[]

      // 动态添加路由
      dynamicRoutes.forEach(route => {
        if (!router.hasRoute(route.name!)) {
          router.addRoute(route)
        }
      })

      // 重定向到目标路由
      next({ ...to, replace: true })
    } catch (error) {
      console.error('路由加载失败:', error)
      next(loginRoutePath)
    }
  } else {
    next()
  }
})

// 后置守卫处理进度条
router.afterEach((to, from) => {
  NProgress.done()
})

export default router
