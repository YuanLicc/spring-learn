# spring-learn [![Build Status](https://travis-ci.org/YuanLicc/spring-learn.svg?branch=master)](https://travis-ci.org/YuanLicc/spring-learn) [![codecov.io](https://codecov.io/gh/YuanLicc/spring-learn/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/gh/YuanLicc/spring-learn?branch=master) [![GitHub version](https://badge.fury.io/gh/yuanlicc%2Fspring-learn.svg)](https://badge.fury.io/gh/yuanlicc%2Fspring-learn)
#### 简介
spring 框架的学习demo 及源码类图（doc/文件夹下，请使用Idea 查看这些类图，请保证您的 gradle 能成功加载依赖，否则无法查看类图）
#### 目标
为这些类图添加注释，并为一些support 类写相关的测试demo
#### 相关
- gradle
- Java 8
- kotlin
- idea

#### Question

**1)  Travis CI Error**

```shell
./gradlew : Permission denied
```

解决办法：修改脚本权限

```shell
git update-index --chmod=+x gradlew
```

提交修改到远程即可。
