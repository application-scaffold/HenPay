import { BasicLayout, BlankLayout, PageView, RouteView } from '@/layouts'
import { useUserStore } from '@/stores/user'
import { asyncRouteDefine } from '@/config/appConfig'
import type { RouteComponent, RouteLocationRaw, RouteRecordRaw } from 'vue-router'
import { defineAsyncComponent } from 'vue'

// 动态组件映射类型
type ComponentMap = Record<string, {
  component: RouteComponent | (() => Promise<RouteComponent>)
  defaultPath?: string
}>

declare module 'vue-router' {
  interface RouteMeta {
    title: string
    icon?: string
    hidden?: boolean
    keepAlive?: boolean
  }
}

// 菜单项数据结构
export interface MenuItem {
  entId: string
  entName: string
  entType: 'ML' | 'MO' | string
  menuUri?: string
  componentName?: string
  children?: MenuItem[]
  [key: string]: any
}

// 前端路由表 = 基础定义 + 动态组件
const constantRouterComponents: ComponentMap = {
  BasicLayout: { component: BasicLayout },
  BlankLayout: { component: BlankLayout },
  RouteView: { component: RouteView },
  PageView: { component: PageView },
  ...asyncRouteDefine
}

// 404 路由定义
const notFoundRouter: RouteRecordRaw = {
  name: 'notFound',
  path: '/:pathMatch(.*)*',
  component: () => import('@/views/exception/404.vue'),
}

// 根级菜单
const rootRouter: RouteRecordRaw = {
  name: 'index',
  path: '/',
  component: BasicLayout,
  redirect: (): RouteLocationRaw => redirectFunc(),
  children: [],
  meta: { title: '主页' }
}

// 动态跳转路径 func
function redirectFunc() : RouteLocationRaw {
  let mainPageUri = ''
  useUserStore().userInfo.allMenuRouteTree.forEach((item) => {
    if (item.entId === 'ENT_C_MAIN') {
      // 当前用户是否拥有主页权限， 如果有直接跳转到该路径
      mainPageUri = item.menuUri
      return false
    }
  })

  if (mainPageUri) {
    return mainPageUri
  }

  return getOneUri(useUserStore().userInfo.allMenuRouteTree)
}

// 获取到第一个uri (递归查找)
function getOneUri(item : any[]) {
  let result = ''
  for (let i = 0; i < item.length; i++) {
    if (item[i].menuUri && item[i].entType === 'ML') {
      return item[i].menuUri
    }

    if (item[i].children) {
      result = getOneUri(item[i].children)
      if (result) {
        return result
      }
    }
  }
  return result
}

/**
 * 动态生成菜单
 * @param token
 * @returns {Promise<Router>}
 */
export const generatorDynamicRouter = (): Promise<RouteRecordRaw[]> => {
  return new Promise((resolve) => {
    rootRouter.children = generator(
      useUserStore().userInfo.allMenuRouteTree
    ) as RouteRecordRaw[]

    resolve([rootRouter, notFoundRouter])
  })
}

// 递归生成器类型声明
const generator = (
  menuTree: MenuItem[]
): RouteRecordRaw[] => {
  return menuTree.map(item => {
    const route: RouteRecordRaw = {
      path: validatePath(item),
      name: item.entId,
      component: resolveComponent(item),
      meta: {
        title: item.entName,
        icon: `${item.menuIcon}-outlined`,
        hidden: item.entType === 'MO'
      },
      children: item.children?.length
        ? generator(item.children)
        : undefined
    }
    return route
  })
}

// 组件解析逻辑
function resolveComponent(item: MenuItem): RouteComponent {
  const def = constantRouterComponents[item.componentName || item.entId]
  return def?.component || autoImportComponent(item)
}

// 动态导入类型安全处理
function autoImportComponent(item: MenuItem): RouteComponent {
  return defineAsyncComponent(
    () => import(`@/views/${item.componentName}.vue`)
  )
}

function validatePath(item: MenuItem): string {
  if (item.menuUri) return item.menuUri
  if (item.children?.length) return `/${item.entId}`
  throw new Error(`无效的路由配置: ${item.entId}`)
}
