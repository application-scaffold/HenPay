import request, { type IResponseData } from '@/http/request'
import { Base64 } from 'js-base64';

// 定义接口类型
interface LoginParams {
  username: string;
  password: string;
  vercode: string;
  vercodeToken: string;
}

interface UserInfo {
  id: number;
  username: string;
  roles: string[];
  // 根据实际接口返回字段补充
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

// 登录认证接口
export function login(params: LoginParams): Promise<{iToken: string}> {
  const data = {
    ia: Base64.encode(params.username), // 账号
    ip: Base64.encode(params.password), // 密码
    vc: Base64.encode(params.vercode),   // 验证码值
    vt: Base64.encode(params.vercodeToken) // 验证码token
  };
  return request.request<{iToken: string}>({
    url: '/api/anon/auth/validate',
    method: 'post',
    data
  }, true, false, false);
}

// 获取图形验证码信息接口
export function vercode(): Promise<VerCode> {
  return request.request<VerCode>({
    url: '/api/anon/auth/vercode',
    method: 'get'
  }, true, true, true);
}

// 获取当前用户信息
export function getInfo(): Promise<UserInfo> {
  return request.request<UserInfo>({
    url: '/api/current/user',
    method: 'get'
  });
}

// 获取网站标题等信息
export function getSiteInfo(): Promise<SiteInfo> {
  return request.request<SiteInfo>({
    url: '/api/auth/siteTitle',
    method: 'get'  // 显式声明 method
  }, true, true, true);
}

// 获取当前用户信息（与 getInfo 重复，建议保留一个）
export function getCurrentUserInfo(): Promise<UserInfo> {
  return request.request<UserInfo>({
    url: '/api/current/user',
    method: 'get'
  });
}
