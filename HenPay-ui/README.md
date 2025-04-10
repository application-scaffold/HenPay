# HenPay UI

为 HenPay 对应的前端项目，包括运营平台、商户系统、聚合码收银台。前端技术以 vue 为主，框架使用 Ant Design Vue 开发。

> 目录结构

```lua
HenPay-ui
├── HenPay-ui-cashier -- 聚合收银台项目
├── HenPay-ui-manager -- 运营平台 web 管理端
└── HenPay-ui-merchant -- 商户系统 web 管理端
```
> 参考命令

node版本要求：`<= 16 `

#### 参考命令

``` bash
# 拉取源码完毕后请先安装依赖, 进入项目根目录命令行执行:

> npm install

# 本地启动项目（开发环境）:

1. 打开根目录下文件 ".env.development", 修改请求服务器地址 "VUE_APP_API_BASE_URL"；

2. 在项目根目录命令行执行:

版本 <3.1.0 为 vue2.x 使用以下命令运行
> npm run serve

版本 >=3.1.0 之后为 vue3.x 使用以下命令运行
> npm run dev

# 打包（生产环境）：

1. 打开根目录下文件 ".env", 修改请求服务器地址 "VUE_APP_API_BASE_URL"；

2. 在项目根目录命令行执行：

> npm run build

3. 文件将输出到 [/dist]目录， 拷贝到 web 服务器即可。
```
