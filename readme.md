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

## ⚙️ 核心功能模块

### 一、登录认证模块（Login 系列文件）
**包含文件**：LoginActivity、LoginApi、LoginRequest、LoginResponse

**模块作用**：应用的入口功能模块，负责用户账号密码登录的完整流程：收集用户输入的服务器地址、账号密码，封装请求参数调用后端登录接口，解析登录响应获取身份Token并持久化存储，登录成功后跳转至首页；同时支持扫码登录确认的接口能力。

### 二、报警通知模块（Alert 系列文件）
**包含文件**：AlertsActivity、AlertAdapter、AlertApi、AlertCheckWorker、AlertItem、AlertResponse

**模块作用**：负责生产异常报警的检测与通知。通过后台定时任务Worker轮询后端报警接口，检测到新报警时发送系统通知提醒用户；前端提供报警列表页面，展示报警发起人、流程、创建时间等详情，支持权限校验与空状态展示。

### 三、异常记录管理模块（Exception + Records 系列文件）
**包含文件**：
- Exception前缀：ExceptionApi、ExceptionMessage、ExceptionMessageAdapter、ExceptionMessageResponse
- Records前缀：Records、RecordsActivity、RecordsAdapter、RecordsApi、RecordsResponse、RecordDetailActivity、RecordDetailResponse

**模块作用**：生产异常记录的全生命周期管理模块。支持从后端拉取异常消息列表并展示；支持相机拍照/相册选择图片，填写描述后上传异常记录；支持查看异常记录详情（包含图片、上传时间、描述等）；支持批量清除所有异常记录，是现场异常上报与追溯的核心模块。

### 四、生产任务管理模块（Task + Manufacture + OrderDemand 系列文件）
**包含文件**：
- Task前缀：TasksActivity、TaskAdapter、TaskApi、TaskResponse
- Manufacture前缀：ManufacturePlan、ManufacturePlanApi、ManufacturePlanResponse、ManufactureTask、ManufactureTaskApi、ManufactureTaskResponse
- OrderDemand前缀：OrderDemand、OrderDemandApi、OrderDemandResponse

**模块作用**：生产业务核心模块，通过Tab分类展示三类生产数据：订单需求、生产计划、生产任务。分别调用对应后端接口拉取数据，解析后以列表形式展示任务编码、产品信息、数量、状态、计划完成时间等内容，支持权限校验与空状态提示，方便产线人员查看生产任务进度。

### 五、文件上传模块（Upload 系列文件）
**包含文件**：UploadActivity、UploadResponse

**模块作用**：通用文件上传功能模块，提供相册图片选择、预览、携带描述信息上传的能力，封装了文件路径解析、Multipart表单上传逻辑，为异常记录等需要附件上传的业务提供支撑。

### 六、用户信息模块（User + Profile 系列文件）
**包含文件**：ProfileActivity、UserApi、UserListResponse、UserProfileResponse

**模块作用**：用户个人信息管理模块。调用后端用户资料接口获取用户基本信息、所属部门、角色、岗位组等数据，在个人信息页结构化展示；支持本地缓存用户信息，同时处理认证失效时的跳转登录逻辑。

### 七、扫码功能模块（QrScanner + PortraitCapture 系列文件）
**包含文件**：QrScannerActivity、PortraitCaptureActivity、scanner_layout.xml

**模块作用**：竖屏扫码工具模块。基于ZXing实现定制化竖屏扫码界面，添加扫描线动画，支持解析二维码内容；主要用于网页端扫码登录确认（扫码后携带移动端Token调用后端接口完成网页登录），也可扩展支持设备扫码绑定等场景。

### 八、首页与导航模块（Home + Main 系列文件）
**包含文件**：HomeActivity、MainActivity

**模块作用**：应用登录后的主入口与导航分发中心。集成侧边抽屉导航栏，提供所有业务模块（任务、报警、异常记录、个人信息、设置、关于）的跳转入口；首页提供悬浮扫码按钮，快速进入扫码功能，是整个应用的页面调度中枢。

### 九、系统设置模块（Settings 系列文件）
**包含文件**：SettingsActivity

**模块作用**：应用配置管理模块，提供用户偏好设置能力，比如“记住密码”开关控制，开关状态持久化存储，关闭记住密码时自动清除已保存的账号密码，保障用户数据安全。

### 十、关于页面模块（About 系列文件）
**包含文件**：AboutActivity

**模块作用**：应用信息展示模块，自动读取并展示应用版本号，同时集成侧边导航栏，支持快速跳转至其他页面。

### 十一、公共基础模块（通用工具与底层组件）
**包含文件**：MyApplication、ApiClient、Routes、NetworkUtils、ClearResponse

**模块作用**：为所有业务模块提供底层基础能力：
1. `MyApplication`：全局应用上下文，提供全局Context获取能力；
2. `ApiClient`：Retrofit网络客户端封装，统一管理主业务接口、异常接口的网络实例；
3. `Routes`：接口地址与Token管理工具，统一维护主服务、异常服务的基础地址，提供Token有效性校验与格式处理；
4. `NetworkUtils`：网络状态检测工具，判断当前设备网络是否可用；
5. `ClearResponse`：通用清除操作响应实体，统一解析后端清除类接口的返回结果。

### 十二、资源配置模块（res 目录）
**包含文件**：drawable图片资源、layout布局文件、menu菜单资源、mipmap启动图标、values主题/颜色/字符串配置、xml文件路径配置

**模块作用**：为所有页面提供UI布局、样式、图片、文本等资源，统一管理应用的视觉风格与静态文案，支持日间/夜间主题切换；同时提供FileProvider所需的文件路径配置，适配Android文件访问权限。

### 十三、应用配置与构建模块
**包含文件**：AndroidManifest.xml、Gradle Scripts、测试目录（androidTest/test）、generated自动生成目录

**模块作用**：
1. `AndroidManifest.xml`：应用全局清单文件，声明网络、相机、存储、通知等所需权限，注册所有Activity组件，指定应用启动页与应用图标、主题等基础配置；
2. `Gradle Scripts`：项目构建脚本，定义依赖库、编译版本、签名配置等构建规则；
3. `androidTest/test`目录：存放单元测试与仪器测试代码，用于功能验证与测试；
4. `generated`目录：编译过程自动生成的代码与资源文件，无需手动维护。

---

## 📂 项目核心目录结构

```text
app
├─ manifests
│  └─ AndroidManifest.xml              # 应用全局清单文件，声明APP所需权限（如网络、相机等）、注册所有Activity组件，指定LoginActivity为APP启动入口，是Android系统识别APP配置的核心文件
├─ java
│  └─ com.pzy.smart_manufacture_app
│     ├─ AboutActivity.java            # 展示应用的版本信息，实现侧边导航栏功能，支持跳转到其他功能页面。
│     ├─ AlertAdapter.java             # 为 RecyclerView 提供数据适配功能，将报警数据列表绑定到对应的视图项，展示每条报警的详细信息。
│     ├─ AlertApi.java                 # 定义获取报警信息的 Retrofit 接口，声明调用报警接口的 GET 请求方法。
│     ├─ AlertCheckWorker.java         # 作为后台工作者组件，定时获取报警信息数量，当存在报警信息时发送通知，并保存当前报警数量到 SharedPreferences 中。
│     ├─ AlertItem.java                # 封装单条报警信息的数据模型，包含报警标签、任务 ID、发起人信息等字段及对应的 getter 方法。
│     ├─ AlertResponse.java            # 定义报警接口响应的数据模型，包含响应码、提示信息和报警数据列表，其中报警数据列表的每一项封装了具体的报警详情。
│     ├─ AlertsActivity.java           # 实现报警信息展示页面，包含权限检查、通知渠道创建、加载报警数据并通过 RecyclerView 展示，同时处理侧边导航栏的跳转逻辑。
│     ├─ ApiClient.java                # 提供 Retrofit 客户端的单例创建方法，分别生成针对常规接口和异常接口的 Retrofit 实例，简化网络请求客户端的创建。
│     ├─ ClearResponse.java            # 定义清除操作响应的数据模型，包含操作是否成功和提示信息两个字段及对应的 getter 方法。
│     ├─ ExceptionApi.java             # 定义了用于获取异常消息记录的 Retrofit 网络请求接口，通过 GET 请求访问指定接口并携带 Authorization 请求头。
│     ├─ ExceptionMessage.java         # 封装了异常消息的实体类，包含异常记录的 ID、编码、描述、时间、处理结果等属性及对应的获取方法。
│     ├─ ExceptionMessageAdapter.java  # 为 RecyclerView 提供适配功能，用于展示异常消息列表数据，绑定数据到对应的列表项视图，并修复了数据为空时的空指针异常。
│     ├─ ExceptionMessageResponse.java # 封装了异常消息列表接口的返回数据结构，包含状态码、提示信息、总条数和异常消息列表数据。
│     ├─ HomeActivity.java             # 作为应用的首页 Activity，负责检查用户登录状态、设置页面布局（含 Toolbar、导航抽屉、扫码悬浮按钮），并处理导航菜单和顶部菜单的点击跳转逻辑。
│     ├─ LoginActivity.java            # 登录页面控制器，登录流程核心枢纽：收集用户输入的服务器地址、账号密码，调用LoginApi发起登录请求，处理登录成功/失败逻辑，保存Token和服务器地址到本地，跳转首页
│     ├─ LoginApi.java                 # 登录相关网络接口定义（Retrofit），定义登录接口（POST /login）和扫码确认登录接口的请求方式、参数（LoginRequest）和响应（LoginResponse）规则
│     ├─ LoginRequest.java             # 登录请求数据模型类（POJO），封装用户名和密码，作为登录请求的请求体，序列化为JSON传递给服务端
│     ├─ LoginResponse.java            # 登录响应数据模型类，解析服务端返回的登录结果，包含状态码、提示信息、Token等核心数据，提供getter方法供前端获取数据
│     ├─ MainActivity.java             # 作为应用主界面 Activity，处理用户登录状态校验、侧边导航栏交互、当前时间展示、生产相关任务数据（订单需求 / 生产计划 / 生产任务）的网络请求与展示等核心逻辑。
│     ├─ ManufacturePlan.java          # 定义生产计划实体类，包含生产计划的核心属性（如计划编码、订单编码、状态等）及对应的获取方法，还提供拼接计划描述信息的getPlanDesc方法。
│     ├─ ManufacturePlanApi.java       # 定义 Retrofit 接口，通过 GET 请求结合 Authorization 头信息获取生产计划列表数据。
│     ├─ ManufacturePlanResponse.java  # 定义生产计划响应数据结构，包含响应状态码、消息、总数及生产计划列表，内部嵌套的生产计划实体类涵盖生产计划全量属性及对应的获取方法。
│     ├─ ManufactureTask.java          # 定义基础的生产任务实体类，包含任务编码和任务描述属性及对应的 get/set 方法。
│     ├─ ManufactureTaskApi.java       # 定义 Retrofit 接口，通过 GET 请求结合 Authorization 头信息获取生产任务列表数据。
│     ├─ ManufactureTaskResponse.java  # 定义生产任务响应数据结构，包含响应状态码、消息、总数及生产任务列表，内部嵌套生产任务实体类并提供属性获取和任务描述拼接方法。
│     ├─ MyApplication.java            # 自定义 Application 类，用于全局获取应用上下文和 Application 实例，在应用启动时初始化自身实例。
│     ├─ NetworkUtils.java             # 提供网络连接状态检测工具方法，通过 Context 获取网络连接管理器判断当前网络是否可用。
│     ├─ OrderDemand.java              # 封装单个订单需求的核心字段，提供各字段的获取方法，还包含一个拼接产品、需求数量、单价信息的便捷方法。
│     ├─ OrderDemandApi.java           # 定义获取订单需求列表的 Retrofit 接口，通过 GET 请求访问指定接口并携带 Authorization 请求头，返回封装订单需求的 TaskResponse 类型数据。
│     ├─ OrderDemandResponse.java      # 定义了订单需求响应数据结构，包含响应状态码、消息、总数及订单需求列表，内部嵌套 OrderDemand 类封装单个订单需求的详细字段及对应的获取方法。
│     ├─ PortraitCaptureActivity.java  # 自定义的竖屏扫码捕获 Activity，继承自 CaptureActivity，添加自定义扫描 UI 并实现扫描线的无限滚动动画效果。
│     ├─ ProfileActivity.java          # 展示用户个人信息页面，包含导航抽屉功能，从本地缓存和网络接口获取并展示用户基本信息、详细信息，处理认证失败和网络错误等异常情况。
│     ├─ QrScannerActivity.java        # 实现安卓端扫码登录功能，强制竖屏扫码，处理内置 / 外部扫码结果，解析二维码中的令牌并调用接口完成网页端登录确认。
│     ├─ RecordDetailActivity.java     # 展示异常记录的详情页面，接收文件名参数并调用接口获取记录详情，格式化展示文件信息、上传时间，使用 Glide 加载并显示高清图片。
│     ├─ RecordDetailResponse.java     # 定义异常记录详情的响应数据结构，通过 SerializedName 注解映射接口返回字段，包含文件名称、描述、上传时间、图片 / 文本链接等字段及对应的获取方法。
│     ├─ Records.java                  # 数据模型类，封装异常记录的文件名、描述、图片 URL 等属性，通过 Gson 注解映射接口返回字段，并提供对应的 getter/setter 方法。
│     ├─ RecordsActivity.java          # 作为异常记录功能的主活动，实现了拍照上传异常图片、加载 / 清除服务器异常记录、侧边导航栏切换等核心功能，处理相机权限请求、图片文件创建与上传、接口调用及结果反馈。
│     ├─ RecordsAdapter.java           # RecyclerView 的适配器类，用于将异常记录数据列表绑定到 UI 视图，通过 Glide 加载图片，设置条目点击事件跳转到详情页。
│     ├─ RecordsApi.java               # Retrofit 接口定义类，声明获取所有异常图片、获取单张图片详情、上传图片带描述、清除所有记录的 HTTP 请求方法。
│     ├─ RecordsResponse.java          # 接口响应数据模型类，封装异常记录列表接口返回的成功状态、记录数量和图片记录列表，提供对应的 getter 方法。
│     ├─ Routes.java                   # 工具类，管理服务器基础地址、异常记录接口地址的存取，处理 JWT 密钥生成 / 读取，封装 Token 有效性校验和格式处理逻辑。
│     ├─ SettingsActivity.java         # 设置页面的活动类，管理侧边导航栏跳转，提供记住登录凭证的开关功能，可读取 / 修改 SharedPreferences 中的记住密码状态并清除保存的登录信息。
│     ├─ TaskAdapter.java              # 实现 RecyclerView 的适配器，用于将 TaskResponse.Task 类型的任务数据绑定到列表项视图，支持空数据视图的显示控制和数据更新。
│     ├─ TaskApi.java                  # 定义获取任务相关数据的 Retrofit 接口，声明获取订单需求、生产计划、生产任务列表的 GET 请求方法。
│     ├─ TaskResponse.java             # 定义了包含状态码、消息和任务列表的通用任务响应数据结构，同时内置了包含编码和描述的 Task 内部类，用于统一封装任务相关接口的返回数据。
│     ├─ TasksActivity.java            # 实现任务页面的核心逻辑，包含侧边栏导航、TabLayout 切换不同任务类型（订单需求 / 生产计划 / 生产任务）、通过 Retrofit 加载任务数据并展示到 RecyclerView。
│     ├─ UploadActivity.java           # 实现图片选择和上传功能的 Activity，支持从相册选择图片、获取图片真实路径，并通过 Retrofit 以 Multipart 形式上传图片和描述信息。
│     ├─ UploadResponse.java           # 定义文件上传接口的响应数据结构，包含上传是否成功、消息、文件名、文件 URL 等字段，适配 Gson 解析的 SerializedName 注解映射字段名。
│     ├─ UserApi.java                  # 定义获取用户个人信息的 Retrofit 接口，声明 GET 请求获取用户资料的方法并支持 Authorization 请求头。
│     ├─ UserListResponse.java         # 定义用户列表接口的响应数据结构，包含状态码、消息、总条数和用户列表，用户信息中嵌套部门信息。
│     └─ UserProfileResponse.java      # 定义用户个人信息接口的响应数据结构，包含用户基本信息、部门、角色等多层嵌套数据，提供安全的 getter 方法避免空指针。
├─ com.pzy.smart_manufacture_app (androidTest)
├─ com.pzy.smart_manufacture_app (test)
├─ java (generated)
├─ res
│  ├─ drawable
│  │  ├─ app_logo.png
│  │  ├─ ic_image_error.jpg
│  │  ├─ ic_launcher_background.xml
│  │  └─ ic_scan.png
│  ├─ layout
│  │  ├─ activity_about.xml
│  │  ├─ activity_alerts.xml
│  │  ├─ activity_home.xml
│  │  ├─ activity_login.xml
│  │  ├─ activity_main.xml
│  │  ├─ activity_profile.xml
│  │  ├─ activity_record_detail.xml
│  │  ├─ activity_records.xml
│  │  ├─ activity_settings.xml
│  │  ├─ activity_tasks.xml
│  │  ├─ activity_upload.xml
│  │  ├─ item_alert.xml
│  │  ├─ item_empty.xml
│  │  ├─ item_exception_message.xml
│  │  ├─ item_image.xml
│  │  ├─ item_records_message.xml
│  │  ├─ item_task.xml
│  │  └─ scanner_layout.xml
│  ├─ menu
│  │  ├─ home_menu.xml
│  │  └─ nav_drawer_menu.xml
│  ├─ mipmap
│  │  └─ ic_launcher.xml (anydpi-v26)
│  ├─ values
│  │  ├─ themes
│  │  │  ├─ themes.xml
│  │  │  └─ themes.xml (night)
│  │  ├─ arrays.xml
│  │  ├─ colors.xml
│  │  ├─ strings.xml
│  │  └─ styles.xml
│  └─ xml
│     └─ file_paths.xml
├─ res (generated)
└─ Gradle Scripts
```

---


## 📝 开发复盘与持续演进

1. **多后端解耦与架构拆分：** 在 2.0.0 版本迭代中，针对图片等多媒体文件直接存入 MySQL 易导致 B+ 树索引臃肿及查询性能断崖式下降的痛点，项目果断剥离了独立的 Python 异常后端。移动端重构为双接口直连模式，确保了核心业务系统的高可用性。
2. **轻量级文件系统存储：** Python 后端摒弃了传统关系型数据库存储图片的方案，采用纯物理文件系统落地（独立 `images/` 与 `texts/` 目录），并利用轻量级的 `image_metadata.json` 维护图文映射关系。这种读写分离的底层逻辑极大提升了多媒体列表的拉取速度。
3. **前瞻性 AI 视觉赋能预留：** 将异常记录独立为 Python 微服务，为后续工业机器视觉（CV）的无缝接入预留了绝佳的生态位。未来演进中，移动端前端代码与 API 协议均实现零代码侵入，只需后端进行图像特征提取，即可将 AI 检测结果动态拼接至配套文字中，平滑完成车间智能化升级。
