## Spring MVC

MVC 模式：

分离了模型、视图、控制器三汇总角色，将业务处理从 UI 设计中独立出来，封装到模型和控制器设计中去，使得他们之间解耦，可以独立扩展而不需彼此依赖。

### Web 环境中的 Spring MVC

MVC 是建立在 IoC 容器基础上的，而 Spring IoC 是一个独立的模块，如果要在 Web 环境中使用 IoC 容器，需要 Spring 为 IoC 设计一个启动过程，把IoC 容器导入，并在 Web 容器中建立起来。这个启动过程和 Web 容器的启动过程集成在一起的，在这个过程中，一方面处理 Web 容器的启动，另一方面通过设计特定的 Web 容器拦截器，将 IoC 容器载入到 Web 环境中来，并将其初始化。

在 Tomcat 中，需要配置对应的 Web.xml 文件：

```xml
<servlet>
  	<servlet-name>dispatcher</servlet-name>
  	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/dispatcher-servlet.xml</param-value>
</context-param>

<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

首先定义一个 Servlet对象，它是 Spring MVC 的 DispatcherServlet。这个 DispatcherServlet 是 MVC 中很重要的一个类，起着分发请求的作用。可配置 context-param 参数来指定 Spring IoC 容器读取 Bean 定义的 XML 文件的路径。最后配置一个监听器，ContextLoaderListener，负责完成 IoC 容器在Web 容器环境中的启动工作。

DispatcherServlet 和 ContextLoaderListener 提供了在 Web 容器中对 Spring 的接口，也就是说哦，这些接口与 Web 容器耦合是通过 ServletContext 来实现的。这个 ServletContext 为 Spring 的 IoC 容器提供了一个宿主环境，在宿主环境中，Spring MVC 建立起一个IoC 容器体系。这个容器体系是通过 ContextLoaderListener 的初始化建立起来的，在建立 IoC 容器体系后，把 DispatcherServlet 作为 Spring MVC 处理 Web 请求的转发器建立起来，从而完成响应 HTTP 请求的准备。

### 上下文在 Web 容器中的启动

#### IoC 容器启动的基本过程

IoC 容器的启动过程就是建立上下文的过程，该上下文是与 ServletContext 相伴而生的，同时也是 IoC 容器在 Web 应用环境中的具体表现之一。由 ContextLoaderListener 启动的上下文是根上下文。在根上下文的基础上，还有一个与 WebMVC 相关的上下文用来保存控制器需要的 MVC 对象，作为根上下文的自上下文，构成一个层次化的上下文体系。在 Web 容器中启动 Spring 应用程序时，首先建立根上下文，然后建立这个上下文体系的，这个上下文体系的建立是由 ContextLoader 来完成的。

![](./images/contextRoute.png)

在 web.xml 中，已经配置了 ContextLoaderListener，这个 ContextLoaderListener 是 Spring 提供的类，是为在 Web 容器中建立 IoC 容器服务的，它实现了 ServletContextListener 接口。这个接口是在 Servlet API 中定义的，提供了 与 Servlet 声明周期结合的回调，比如 contextInitialized 方法和 contextDestroyed 方法。而在web容器中，建立 WebApplicationContext 的过程，是在的接口实现中完成的。具体的载入 IoC 容器的过程是由 ContextLoaderListener 交由 ContextLoader 来完成的，而 ContextLoader 本身就是 ContextLoaderListener 的基类。

在 ContextLoader 中，完成了两个 IoC 容器建立的基本过程，一个是 Web 容器中建立起双亲 IoC 容器，另一个是生成响应的 WebApplicationContext 并将其初始化。



#### Web 容器中的上下文设计

![](./images/webContext.png)

下面是 WebApplication 的接口方法，它定义了 getServletContext 方法，通过这个方法可以得到当前 Web 容器的 Servlet 上下文环境，通过这个方法，相当于提供了一个 Web 容器级别的全局环境。

![](./images/webApplicationContext.png)

在启动过程中，Spring 会使用一个默认的 WebApplicationContext 实现作为 IoC 容器。这个默认使用的 IoC 容器就是 XmlWebApplicationContext，XMLWebApplicationContext 是从 ApplicationContext 继承下来的，在基本的 ApplicationContext功能的基础上，增加了对 Web 环境和 XML配置定义的处理。在 XmlWebApplicationContext 的初始化过程中，web容器中的 IoC 容器被建立起来，从而在 Web 容器中建立起整个 Spring 应用。

```java
// 执行 refresh 的过程中会调用此方法
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) 
                                                 throws BeansException, IOException {
    // Create a new XmlBeanDefinitionReader for the given BeanFactory.
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

    // Configure the bean definition reader with this context's
    // resource loading environment.
    beanDefinitionReader.setEnvironment(getEnvironment());
    beanDefinitionReader.setResourceLoader(this);
    beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

    // Allow a subclass to provide custom initialization of the reader,
    // then proceed with actually loading the bean definitions.
    initBeanDefinitionReader(beanDefinitionReader);
    loadBeanDefinitions(beanDefinitionReader);
}
```

从代码中可以看到 XMLWebApplicationContext 中，基本的实现都已经通过继承获得，这里处理的就是如何获取 Bean 定义信息，即如何获取 WEBINF 下的配置文件信息，获取这些信息后的处理都是一样的。



#### ContextLoader 的设计与实现

对于 Spring 承载的 Web 应用而言，可以指定在 Web 应用程序启动时载入 IoC 容器。这个功能是由 ContextLoaderListener 这样的类完成的，它是在 Web 容器中配置的监听器。这个 ContextLoaderListener 通过使用 ContextLoader 来完成实际的 WebApplicationContext，也就是 IoC 容器的初始化工作。这个 ContextLoader 就像 Spring 应用程序在 Web 容器中的启动器，这个启动过程是 web 容器中发生的，所以需要根据 web 容器部署要求来定义 ContextLoader。

ContextLoaderListener 是启动 根容器并把它载入到 web 容器的主要功能模块，也是整个 Spring Web应用加载 IoC 的第一个地方。从加载过程可以看到，首先从 Servlet 事件中得到 ServletContext，然后可以读取配置在 web,xml 中的各个相关属性值，接着 ContextLoader 会实例化 WebApplicationContext，并完成其载入和初始化过程。这个初始化的第一个上下文作为根上下文而存在，这个根上下文载入后，被绑定到 web 应用程序的 ServletContext 上。任何需要访问根上下文的应用程序代码可以从 WebApplicationContextUtils 类的静态方法中得到。

```java
public static WebApplicationContext getWebApplicationContext(ServletContext sc
															, String attrName) {
    Assert.notNull(sc, "ServletContext must not be null");
    Object attr = sc.getAttribute(attrName);
    if (attr == null) {
        return null;
    }
    if (attr instanceof RuntimeException) {
        throw (RuntimeException) attr;
    }
    if (attr instanceof Error) {
        throw (Error) attr;
    }
    if (attr instanceof Exception) {
        throw new IllegalStateException((Exception) attr);
    }
    if (!(attr instanceof WebApplicationContext)) {
        throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
    }
    return (WebApplicationContext) attr;
}
```

在 ContextLoaderListener 中，实现的是 ServletContextListener 接口，这个接口里的函数结合 web 容器的声明周期被调用，因为 ServletContextListener 是 ServletContext 的监听者，如果 ServletContext 发生变化，会触发相应的事件，而监听器一直在对这些事件进行监听，如果接收到了监听的事件，就会做出预先设计好的响应动作。由于 ServletContext 的变化而触发的监听器的响应具体包括：在服务器启动时，ServletContext 被创建的时候；服务器关闭时，ServletContext 将被销毁的时候等。对应这些事件及 Web 容器状态的变化，在监听器中定义了对应的时间响应的回调方法。比如在服务器启动时，ServletContextListener 的 contextInitialized 方法将会被调用，服务器将要关闭时，ServletContextListener 的 contextDestroyed 方法将会被调用。

```java
public void contextInitialized(ServletContextEvent event) {
    initWebApplicationContext(event.getServletContext());
}

// ContextLoader#initWebApplicationContext(ServletContext servletContext)
public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
    // 判断根上下文是否已经存在
    if(servletContext.getAttribute(
        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
        // 存在则抛出异常
        throw new IllegalStateException(
            "Cannot initialize context because there is already a root application context present - " +
            "check whether you have multiple ContextLoader* definitions in your web.xml!");
    }

    Log logger = LogFactory.getLog(ContextLoader.class);
    servletContext.log("Initializing Spring root WebApplicationContext");
    if (logger.isInfoEnabled()) {
        logger.info("Root WebApplicationContext: initialization started");
    }
    long startTime = System.currentTimeMillis();

    try {
        // Store context in local instance variable, to guarantee that
        // it is available on ServletContext shutdown.
        if (this.context == null) {
            this.context = createWebApplicationContext(servletContext);
        }
        if (this.context instanceof ConfigurableWebApplicationContext) {
            ConfigurableWebApplicationContext cwac 
                = (ConfigurableWebApplicationContext) this.context;
            if (!cwac.isActive()) {
                // The context has not yet been refreshed -> provide services such as
                // setting the parent context, setting the application context id, etc
                if (cwac.getParent() == null) {
                    // The context instance was injected without an explicit parent ->
                    // determine parent for root web application context, if any.
                    ApplicationContext parent = loadParentContext(servletContext);
                    cwac.setParent(parent);
                }
                configureAndRefreshWebApplicationContext(cwac, servletContext);
            }
        }
        servletContext
            .setAttribute(WebApplicationContext
                          .ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE , this.context);

        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl == ContextLoader.class.getClassLoader()) {
            currentContext = this.context;
        }
        else if (ccl != null) {
            currentContextPerThread.put(ccl, this.context);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" +
                         WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
        }
        if (logger.isInfoEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
        }

        return this.context;
    }
    catch (RuntimeException ex) {
        logger.error("Context initialization failed", ex);
        servletContext
            .setAttribute(WebApplicationContext
                          .ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
        throw ex;
    }
    catch (Error err) {
        logger.error("Context initialization failed", err);
        servletContext
            .setAttribute(WebApplicationContext
                          .ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, err);
        throw err;
    }
}
```

这里创建的根上下文是 web 容器中唯一的实例，如果在这个初始化过程中发现已经有根上下文被常见了，这里会抛出异常提示创建失败。创建成功后，会被存到 web 容器的 ServletContext 中，供需要时使用。

### Spring MVC 的设计与实现

#### Spring MVC 设计概览

在完成对 ContextLoaderListener 的初始化以后，web容器开始初始化 DispatcherServlet，这个初始化的启动与在 Web.xml 中对载入次序的定义有关。DispatcherServlet 会建立自己的上下文来持有 Spring MVC 的 Bean 对象，在建立这个自己持有的 IoC 容器时，会从 ServletContext 中得到根上下文作为 DispatcherServlet 持有上下文的双亲上下文。有了这个根上下文，在对自己持有的上下文进行初始化，最后把自己持有的这个上下文保存到 ServletContext 中，供以后检索和使用。

![](./images/DispatcherServlet.png)

DispatcherServlet 通过集成 FrameworkServlet 和 HttpServletBean 而继承了 HTTPServlet，通过使用 Servlet API来对 HTTP 请求进行响应，成为 Spring MVC 的前端处理器，同时成为 MVC 模块与 Web 容器集成的处理前端。DispatcherServlet 的工作大致可分为两个部分：一个是初始化部分，由 initServletBean 启动，通过 initWebApplicationContext 方法最终调用 DispatcherServlet 的 initStrategies 方法，在这个方法里，DispatcherServlet 对 MVC 模块的其他部分进行了初始化，比如 handlerMapping、ViewResolver等。另一个是对 HTTP 请求进行响应，作为一个 Servlet。web 容器会调用 Servlet 的doget方法等。DispatcherServlet 还重写了 doService 方法，在这个方法调用中封装了 doDispatch 方法，此方法是实现 MVC 的主要部分。

![](./images/dispatcherTime.png)

#### DispatcherServlet 的启动和初始化

作为 Servlet，DispatcherServlet 的启动与 Servlet 的启动过程是相联系的，在 Servlet 的初始化过程中，Servlet 的init 方法会被调用，以进行初始化。DispatcherServlet 的基类 HttpServletBean 中的这个初始化过程：

```java
public final void init() throws ServletException {
    if (logger.isDebugEnabled()) {
        logger.debug("Initializing servlet '" + getServletName() + "'");
    }

    // Set bean properties from init parameters.
    PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
    if (!pvs.isEmpty()) {
        try {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
            bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
            initBeanWrapper(bw);
            bw.setPropertyValues(pvs, true);
        }
        catch (BeansException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
            }
            throw ex;
        }
    }

    // Let subclasses do whatever initialization they like.
    initServletBean();

    if (logger.isDebugEnabled()) {
        logger.debug("Servlet '" + getServletName() + "' configured successfully");
    }
}
```

在初始化开始时，需要读取配置在 ServletContext 中的 Bean 属性参数，这写属性参数设置在 web.xml 的 web 容器初始化参数中，使用编程式的方式来设置这些 bean 属性，这里的依赖注入是与 web 容器初始化相关的。

接着会执行 DispatcherServlet 持有的 IoC 容器的初始化过程，在这个初始化过程中，一个新的上下文被建立起来，这个 DispatcherServlet 持有的上下文被设置为根上下文的子上下文，DispatcherServlet 持有的上下文是和 Servlet对应的一个上下文。在一个 web 应用中，往往可以容纳多个 Servlet 存在。与此相对应，对于应用在 Web 容器中的上下文体系，一个根上下文可以作为许多 Servlet 上下文的双亲上下文。

根上下文 ContextLoader 设置到 ServletContext 中去的，