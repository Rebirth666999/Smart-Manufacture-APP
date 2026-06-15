# 智能制造移动看板系统 (Smart-Manufacture-APP)

本项目为智能工业物联网系统的移动端监控客户端（Android 原生应用）。系统基于前后端分离架构开发，通过对接后端服务生态，将传统工业车间的设备状态、生产任务及异常数据延伸至移动终端，实现车间现场数据的实时看板化、任务流转数字化与异常响应的高效化。

---

## 🛠️ 技术栈与核心架构

* **开发语言：** Java (Android SDK)
* **构建系统：** Gradle (Kotlin DSL - `*.gradle.kts`)
* **网络通信：** 基于 OkHttp / Retrofit 封装的 RESTful API 通信层，支持多基地址（Multi-BaseUrl）分发路由
* **数据解析：** Gson 序列化与反序列化工具
* **UI 架构：** 遵循 Android 官方原生布局规范，采用 Material Design 控件，通过自定义适配器（如 `ImageAdapter`）实现复杂多媒体列表的动态渲染。

---

仔细核对了你上传的项目目录截图，之前的大纲确实遗漏了底层架构包（`api/`、`model/`）以及后台服务、拦截器和多项独立布局文件。

我已经根据实际的代码结构为你进行了深度的信息补全与映射，纠正了之前布局文件命名不准确的问题（如统一写成 `activity_main.xml` 的错误），并完善了数据模型和网络层的分类。

你可以直接全选、复制以下内容，替换你文档中的对应部分：

---

## ⚙️ 核心功能模块


### 1. 用户认证与网关配置 (Authentication & Gateway)

* **核心类：** `LoginActivity.java`、`TokenInterceptor.java` & `activity_login.xml`
* **功能描述：** 提供工业系统权限鉴权接入。底层利用 `TokenInterceptor` 实现全局请求头的 Token 注入与过期自动拦截。
* **双路由网关配置：** UI 层面不仅提供标准账户密码表单，更内置了动态网关入口，允许用户在登录时分别输入“主业务系统（若依）”与“异常记录系统（Python）”的 API 地址，实现底层数据流的物理隔离。
* **会话管理：** 系统主页底部提供一键“退出登录”功能，支持随时销毁本地 Token 会话并退回安全网关。



### 2. 实时报警通知机制 (Alarm Notifications)

* **核心类：** `NotificationService.java`
* **功能描述：** 基于 Android 原生 Service 组件打造的后台常驻服务。系统初始化及登录验证后，自动轮询并检查车间系统的报警队列。若检测到异常数据，将直接通过 Android 系统级通知栏（Notification Bar）向下发报，**直观展示报警的设备编号及具体原因（例如：`设备 [Device-001] 温度过高`）**，确保车间突发状况的零延迟触达。

### 3. 车间数字化看板 (Home Dashboard)

* **核心类：** `HomeActivity.java` & `activity_home.xml`
* **数据模型：** `DeviceOee.java`、`DeviceStatus.java`、`WorkOrder.java`
* **功能描述：** 采用多维图表与核心指标卡片构建的数字化概览页：
* **业务指标卡片：** 顶部聚合展示当前“待办任务”、“我的工单”及“异常记录”的数量，提供直观的工作量盘点。
* **设备状态分布：** 使用**饼状图**实时呈现当前车间所有设备的运行态势（运行、空闲、故障）。
* **设备 OEE 分析：** 采用**柱状图**横向对比不同设备（如 Device-001 / 002）的综合效率百分比。
* **产线良率统计：** 利用**折线/面积趋势图**展示生产质量的历史波动情况，辅助现场管理与决策。



### 4. 生产任务分发与流转 (Task Management)

* **核心类：** `TasksActivity.java` & `activity_tasks.xml`、`item_task.xml`
* **数据模型：** `Task.java`
* **功能描述：** 实现跨角色（排产员、操作工）的任务动态流转管控。
* **任务卡片渲染：** 基于 RecyclerView 定制卡片视图，详细展示各任务的“工单号”、“产品名称”、“计划数量”及“当前状态”。
* **状态机闭环：** 在移动端本地提供一键状态切换按钮，支持将任务从“待执行”状态推进至“开始执行”，并最终标记为“完成任务”，所有操作流转实时同步回写至后端主业务数据库。



### 5. 现场异常多媒体追踪 (Exception Records)

* **核心类：** `ExceptionRecordsActivity.java`、`ImageAdapter.java` & `activity_exception_records.xml`、`item_image.xml`
* **网络与数据：** `ImageApi.java`、`ImageResponse.java`
* **功能描述：** 跨过主业务库，直接对接独立 Python 后端的轻量级图文上报与追踪系统。
* **图文融合上报：** 提供独立的添加视图，支持直接拉起手机原生相机进行故障现场取证，并提供“异常描述(选填)”文本域，实现图文数据的捆绑上传。
* **网格流展示：** 成功上传的异常记录，通过 `ImageAdapter` 以网格（Grid）形式动态渲染在列表中。
* **存储优化：** Python 后端采用纯物理文件系统落地图片，避免了 MySQL 存储多媒体 BLOB 数据导致的 I/O 性能灾难，实现了移动端列表的高速异步拉取。
---

## 📂 项目核心目录结构

```text
Smart-Manufacture-APP/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml                   # Android 核心清单文件 (权限与四大组件注册)
│   │       ├── java/com/pzy/smart_manufacture_app/   # Java 源码主包目录
│   │       │   ├── ApiClient.java                    # 核心网络客户端封装 (Retrofit/OkHttp)
│   │       │   ├── ApiService.java                   # 主业务系统 API 路由接口
│   │       │   ├── DeviceOee.java                    # 设备综合效率数据模型
│   │       │   ├── DeviceStatus.java                 # 设备实时状态模型
│   │       │   ├── ExceptionRecordsActivity.java     # 异常记录与多媒体管控业务页
│   │       │   ├── HomeActivity.java                 # 数字化看板与核心指标数据页
│   │       │   ├── ImageAdapter.java                 # 多媒体图片列表动态适配器
│   │       │   ├── ImageApi.java                     # 异常多媒体系统 API 路由接口
│   │       │   ├── ImageResponse.java                # 图片上传交互响应模型
│   │       │   ├── LoginActivity.java                # 登录管控与双接口网关配置页
│   │       │   ├── MainActivity.java                 # 客户端主程序入口/导航框架壳
│   │       │   ├── NotificationService.java          # 报警轮询与系统级通知后台服务
│   │       │   ├── Task.java                         # 生产任务数据模型
│   │       │   ├── TasksActivity.java                # 生产任务状态机流转管理页
│   │       │   ├── TokenInterceptor.java             # Http 鉴权 Token 全局拦截器
│   │       │   └── WorkOrder.java                    # 生产工单数据模型
│   │       └── res/                                  # 静态资源目录
│   │           ├── drawable/                         # 矢量图与图形资源
│   │           │   ├── ic_launcher_background.xml
│   │           │   └── ic_launcher_foreground.xml
│   │           ├── layout/                           # UI 视图布局层
│   │           │   ├── activity_exception_records.xml# 异常记录页面布局
│   │           │   ├── activity_home.xml             # 数字化看板页面布局
│   │           │   ├── activity_login.xml            # 登录认证页面布局
│   │           │   ├── activity_main.xml             # 客户端主页面框架布局
│   │           │   ├── activity_tasks.xml            # 生产任务台页面布局
│   │           │   ├── item_image.xml                # 图片网格单项卡片布局
│   │           │   └── item_task.xml                 # 生产任务单项卡片布局
│   │           ├── mipmap/                           # 应用图标资源 (适配各分辨率)
│   │           ├── values/                           # 全局常量资源
│   │           │   ├── colors.xml                    # 颜色规范定义
│   │           │   ├── strings.xml                   # 文本常量定义
│   │           │   └── themes.xml                    # 主题与样式定义
│   │           └── xml/                              # 应用级特殊 XML 配置
│   │               ├── backup_rules.xml              # 数据备份规则
│   │               └── data_extraction_rules.xml     # 数据提取规则
│   ├── build.gradle.kts                              # 模块级构建配置 (Module: app)
│   └── proguard-rules.pro                            # 代码混淆与压缩规则
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties                 # Gradle Wrapper 版本定义
├── build.gradle.kts                                  # 项目级构建配置 (Project)
├── gradle.properties                                 # Gradle 全局编译参数配置
├── local.properties                                  # 本地 SDK 路径配置 (私密不上传)
├── settings.gradle.kts                               # 项目级组件注入与中央仓库声明
└── README.md                                         # 项目工程技术文档
```

---


## 📝 开发复盘与持续演进

1. **多后端解耦与架构拆分：** 在 2.0.0 版本迭代中，针对图片等多媒体文件直接存入 MySQL 易导致 B+ 树索引臃肿及查询性能断崖式下降的痛点，项目果断剥离了独立的 Python 异常后端。移动端重构为双接口直连模式，确保了核心业务系统的高可用性。
2. **轻量级文件系统存储：** Python 后端摒弃了传统关系型数据库存储图片的方案，采用纯物理文件系统落地（独立 `images/` 与 `texts/` 目录），并利用轻量级的 `image_metadata.json` 维护图文映射关系。这种读写分离的底层逻辑极大提升了多媒体列表的拉取速度。
3. **前瞻性 AI 视觉赋能预留：** 将异常记录独立为 Python 微服务，为后续工业机器视觉（CV）的无缝接入预留了绝佳的生态位。未来演进中，移动端前端代码与 API 协议均实现零代码侵入，只需后端进行图像特征提取，即可将 AI 检测结果动态拼接至配套文字中，平滑完成车间智能化升级。
