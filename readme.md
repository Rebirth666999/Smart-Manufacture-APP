# Smart Manufacture 智控生产

## 介绍
智控生产项目app端



## 开发说明

1. `master`分支是成品分支，不要直接在上面做修改，可以创建自己的分支改，然后提代码评审pull request
2. 为了避免冲突，尽量不要多人同时修改同一文件，在自己的分支修改前，先从`master`分支pull一下
3. `master`上一旦发生代码无法运行等意外情况，需要及时通知
4. 各自创建的分支可以随意处置，回滚或者删除什么的都没问题



## git简单使用

从远程拉取（代码更改、分支信息等）

```bash
git pull
```



将master分支的代码拉到当前的分支（之后需要用git push将合并操作推送到远程）

```bash
git pull origin master
```



切换分支（需要先从远程拉取分支信息）

```bash
git checkout <branch name>
```



暂存所有的更改（请忽略本指令的所有`WARNING`）

```bash
git add *
```



提交更改到本地（commit message需要用双引号包裹）

```bash
git commit -m <commit message>
```



本地更改推送到远程的对应分支

```bash
git push
```





<!--

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md

2. Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)

3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目

4. [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目

5. Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)

6. Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 

-->