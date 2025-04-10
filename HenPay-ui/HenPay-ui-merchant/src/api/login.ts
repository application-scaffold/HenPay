import request from '@/http/request'
import { Base64 } from 'js-base64'
import type { UserInfo } from '@/stores/user.ts'

// 定义请求参数类型（使用 RFC 规范命名）
interface LoginParams {
  username: string
  password: string
  vercode: string
  vercodeToken: string
}

interface SiteInfo {
  title: string;
  logoUrl: string;
  // 根据实际接口返回字段补充
}

interface VerCode {
  vercodeToken: string,
  imageBase64Data: string,
  expireTime: number
}

// 登录认证接口（添加参数类型和返回类型）
export function login(params: LoginParams): Promise<{iToken: string}> {
  const data = {
    ia: Base64.encode(params.username),  // 强制字符串编码
    ip: Base64.encode(params.password),
    vc: Base64.encode(params.vercode),
    vt: Base64.encode(params.vercodeToken)
  }

  return request.request(
    {
      url: '/api/anon/auth/validate',
      method: 'post',
      data: data,
    },
    true,
    true,
    false
  )
}

// 图形验证码接口（明确返回类型）
export function vercode(): Promise<VerCode> {
  return request.request<VerCode>(
    { url: '/api/anon/auth/vercode', method: 'get' },
    true,
    true,
    true
  )
}

// 用户信息接口（复用 UserInfo 类型）
export function getInfo(): Promise<UserInfo> {
  return request.request<UserInfo>({
    url: '/api/current/user',
    method: 'get',
  })
}

// 退出接口（明确 Promise 类型）
export function logout(): Promise<void> {
  return new Promise((resolve) => {
    resolve()
  })
}

// 网站信息接口（添加泛型类型）
export function getSiteInfo(): Promise<SiteInfo> {
  return request.request<SiteInfo>(
    { url: '/api/auth/siteTitle' },
    true,
    true,
    true
  )
}

// 当前用户信息（复用接口避免冗余）
export function getCurrentUserInfo(): Promise<UserInfo> {
  return request.request<UserInfo>({
    url: '/api/current/user',
    method: 'get',
  })
}
