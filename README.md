# spring-learn [![Build Status](https://travis-ci.org/YuanLicc/spring-learn.svg?branch=master)](https://travis-ci.org/YuanLicc/spring-learn)![GitHub version](https://badge.fury.io/gh/yuanlicc%2Fspring-learn.svg)
#### 简介
1）spring 框架的学习 demo 及源码类图（doc/文件夹下，请使用Idea 查看这些类图，请保证您的 gradle 能成功加载依赖，否则无法查看类图）。

2）笔记，对书籍 [《Spring技术内幕：深入解析Spring架构与设计原理（第2版）》](https://www.amazon.cn/dp/B0077K9ZXY/ref=sr_1_1?s=books&ie=UTF8&qid=1533693228&sr=1-1&keywords=Spring+%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95)的摘录。这本书不会对 Spring 的使用进行讲解，书籍的很大一部分都是对源码的解释，但是对于一些概念却没有讲，这造成阅读的难度提高。这本书还是很值得看的哦。

3）源码调试及说明，这一块是我看了书以及其它资料后对 Spring 一些过程的源码解析。

#### 源码调试

- [MVC 启动过程](https://github.com/YuanLicc/spring-learn/blob/master/doc/notes/MVCStart.md)

#### 笔记

- [普及](https://github.com/YuanLicc/spring-learn/blob/master/doc/notes/OtherBasic.md)

- [Spring 整体架构及设计理念](https://github.com/YuanLicc/spring-learn/blob/master/doc/notes/SpringFramework.md)
- [Spring IoC](https://github.com/YuanLicc/spring-learn/blob/master/doc/notes/SpringIoC.md)
- [Spring AOP](https://github.com/YuanLicc/spring-learn/blob/master/doc/notes/SpringAOP.md)
- [Spring MVC](https://github.com/YuanLicc/spring-learn/blob/master/doc/notes/SpringMVC.md)

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
