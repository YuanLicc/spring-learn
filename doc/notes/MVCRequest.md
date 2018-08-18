## MVC 请求处理过程

一个请求到达后，会执行对应 `Servlet`  的 `service` 方法，在 `HttpServlet` 中默认实现了此方法，将不同的请求分派给不同的方法处理，如 `doGet`、`doPost` 等。而对于 `Spring MVC` 中的 `DispatcherServlet` 来说，它的父类 `FrameworkServlet` 重写了 `service` 方法，如下：

```java
// class FrameworkServlet

protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	// 获取 method
    HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
    // 如果 method 为 patch 或为 null，调用 processRequest 方法。
    if (httpMethod == HttpMethod.PATCH || httpMethod == null) {
        processRequest(request, response);
    }
    // 其它方法如：GET、POST、HEAD 等执行 HttPServlet 的 service 方法。
    else {
        // 这里调用父级 service 方法，此方法在 HttpServlet 中有实现， 
        // 在下面进行说明。
        super.service(request, response);
    }
}
```

先假设请求的 `method` 为非 `PATH` 方法，所以从上面的调度来看执行 `HttpServlet` 中的 `service` 方法：

```java
// class HttpServlet

// 此处将不同的 method 分派给不同的方法进行处理，
// do* 方法在此类中有默认的实现，但在 FrameworkServlet 中进行了重写，
// 所以会调用 FrameworkServlet 中的实现。
protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    // 获取 method
    String method = req.getMethod();
	// 判断是否等于 GET
    if (method.equals(METHOD_GET)) {
        // 返回请求对象上次修改的时间戳，这里无实现，统统返回 -1。
        long lastModified = getLastModified(req);
        if (lastModified == -1) {
            // 调用 doGet 方法处理。   
            doGet(req, resp);
        } 
        else {
            // 获取请求头中 if-modified-since 值，
            // 这是一个标准的 HTTP 请求头，表示浏览器缓存文件最后修改时间，
            // 若再次请求此文件时，浏览器会将此参数设置到请求头。
            long ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
            // 判断时间比上次修改小，说明文件修改了，执行 doGet 重新获取。
            if (ifModifiedSince < lastModified) {
                // 将修改时间设置到 response 的头中（Last-Modified）返回给客户端。
                maybeSetLastModified(resp, lastModified);
                doGet(req, resp);
            } 
            else {
                // 判断文件未修改，不再请求文件，仅仅返回状态码 304，
                // 浏览器得到这个状态码，会直接使用缓存。
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
        }

    } 
    // 判断 method 等于 HEAD
    else if (method.equals(METHOD_HEAD)) {
        // 获取请求上次修改时间。
        long lastModified = getLastModified(req);
        // 将修改时间设置到 response 的头中（Last-Modified）返回给客户端。
        maybeSetLastModified(resp, lastModified);
        doHead(req, resp);

    } 
    // 判断 method 等于 POST
    else if (method.equals(METHOD_POST)) {
        doPost(req, resp);

    } 
    // 判断 method 等于 PUT
    else if (method.equals(METHOD_PUT)) {
        doPut(req, resp);

    } 
    // 判断 method 等于 DELETE
    else if (method.equals(METHOD_DELETE)) {
        doDelete(req, resp);

    } 
    // 判断 method 等于 OPTIONS
    else if (method.equals(METHOD_OPTIONS)) {
        doOptions(req,resp);

    } 
    // 判断 method 等于 TRACE
    else if (method.equals(METHOD_TRACE)) {
        doTrace(req,resp);

    } 
    // method 不匹配
    else {
        String errMsg = lStrings.getString("http.method_not_implemented");
        Object[] errArgs = new Object[1];
        errArgs[0] = method;
        errMsg = MessageFormat.format(errMsg, errArgs);
	    // 返回错误 501
        resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
    }
}
```

虽然上面的 `do*` 方法在 `HttpServlet` 中有默认的实现，但是由于 `FrameworkServlet` 中进行了重写：

```java
// class FrameworkServlet

// GET 请求某个资源
protected final void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
}

// 
protected final void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
}

protected final void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
}

protected final void doDelete(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
}


protected void doOptions(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
	// 判断是否调度 OPTIONS method 及是否为跨域预检请求。
    // CORS 全称为跨域资源共享，是 W3C 标准，允许浏览器向跨域服务器发出 xmlHttpRequest 请求。
    // 跨域需要浏览器和服务器同时支持，目前几乎所有浏览器都支持跨域。
    // 浏览器将跨域请求分为两类：简单请求，非简单请求。
    // 简单请求：HEAD，GET，POST 且请求头信息的字段有限制，若不满足前面的条件即非简单请求。
    // 浏览器发现跨域请求时，会对请求头添加字段：Origin, 表示请求的目标源。
    // 服务器接收到请求后可以决定是否允许跨域，设置 Access-Control-Allow-Origin 响应头字段
    // 来标识允许哪些源可以跨域请求，可以为 *，表示允许任意源。浏览器会根据这个字段来进行判断，
    // 若请求的源不在 Access-Control-Allow-Origin 字段中，表示不允许跨域，会抛出异常。
    // 另一种非简单请求会在真正的请求前增加一次请求，称为预检请求。预检请求的方法为 OPTIONS,
    // 道理同简单请求，就是询问是否能对目标域进行跨域请求，服务器响应后，若同意跨域则进行真正的请求。
    if (this.dispatchOptionsRequest || CorsUtils.isPreFlightRequest(request)) {
        processRequest(request, response);
        if (response.containsHeader("Allow")) {
            // Proper OPTIONS response coming from a handler - we're done.
            return;
        }
    }

    // 调用 HttpServlet 中的实现，且重写了 HttpServletResponseWrapper 中的 setHeader 方法。
    // 在后面进行说明。
    super.doOptions(request, new HttpServletResponseWrapper(response) {
        @Override
        public void setHeader(String name, String value) {
            if ("Allow".equals(name)) {
                value = (StringUtils.hasLength(value) ? value + ", " : "") 
                    + HttpMethod.PATCH.name();
            }
            super.setHeader(name, value);
        }
    });
}

// 跟踪
protected void doTrace(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    // 是否调度 TRACE 请求。
    if (this.dispatchTraceRequest) {
        processRequest(request, response);
        if ("message/http".equals(response.getContentType())) {
            // Proper TRACE response coming from a handler - we're done.
            return;
        }
    }
    // 在下面进行说明。
    super.doTrace(request, response);
}
```

```java
// class HttpServlet

protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
        		throws ServletException, IOException {
    // 反射获取当前对象对应 class 方法及其父类一直到 HttpServlet 的方法。
    Method[] methods = getAllDeclaredMethods(this.getClass());

    boolean ALLOW_GET = false;
    boolean ALLOW_HEAD = false;
    boolean ALLOW_POST = false;
    boolean ALLOW_PUT = false;
    boolean ALLOW_DELETE = false;
    boolean ALLOW_TRACE = true;
    boolean ALLOW_OPTIONS = true;

    // 遍历方法，若实现了 do* 方法，则对应的 method 允许。
    for (int i=0; i<methods.length; i++) {
        String methodName = methods[i].getName();

        if (methodName.equals("doGet")) {
            ALLOW_GET = true;
            ALLOW_HEAD = true;
        } else if (methodName.equals("doPost")) {
            ALLOW_POST = true;
        } else if (methodName.equals("doPut")) {
            ALLOW_PUT = true;
        } else if (methodName.equals("doDelete")) {
            ALLOW_DELETE = true;
        }
    }

    StringBuilder allow = new StringBuilder();
    if (ALLOW_GET) {
        allow.append(METHOD_GET);
    }
    // ... 此处省略类似的操作。
    
	// 设置响应头
    resp.setHeader("Allow", allow.toString());
}

// 这是一种 TRACE 信息的实现，将信息拼接并写到主体中去。
// 很容已看出处理逻辑，不做过多的解释。
protected void doTrace(HttpServletRequest req, HttpServletResponse resp) 
        		throws ServletException, IOException {

    int responseLength;

    String CRLF = "\r\n";
    StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI())
        .append(" ").append(req.getProtocol());

    Enumeration<String> reqHeaderEnum = req.getHeaderNames();

    while( reqHeaderEnum.hasMoreElements() ) {
        String headerName = reqHeaderEnum.nextElement();
        buffer.append(CRLF).append(headerName).append(": ")
            .append(req.getHeader(headerName));
    }

    buffer.append(CRLF);

    responseLength = buffer.length();

    resp.setContentType("message/http");
    resp.setContentLength(responseLength);
    ServletOutputStream out = resp.getOutputStream();
    out.print(buffer.toString());
}
```

在 `FrameworkServlet` 中除了 `doTrace` 及 `doOptions` 由 `HttpServlet` 处理，其它均通过调用 `processRequest` 方法，若允许调度 `TRACE` 及 `OPTIONS` 请求，这两种请求也会被 `processRequest` 方法处理：

```java
protected final void processRequest(HttpServletRequest request
		, HttpServletResponse response) throws ServletException, IOException {

    long startTime = System.currentTimeMillis();
    Throwable failureCause = null;
	// 从当前线程中获取 LocaleContext。
    LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
    // 获取请求信息中 Accept-Language 首部来确定一个 localeContext。
    LocaleContext localeContext = buildLocaleContext(request);

    RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
    ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
    asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());

    initContextHolders(request, localeContext, requestAttributes);

    try {
        doService(request, response);
    }
    catch (ServletException | IOException ex) {
        failureCause = ex;
        throw ex;
    }
    catch (Throwable ex) {
        failureCause = ex;
        throw new NestedServletException("Request processing failed", ex);
    }

    finally {
        resetContextHolders(request, previousLocaleContext, previousAttributes);
        if (requestAttributes != null) {
            requestAttributes.requestCompleted();
        }

        if (logger.isDebugEnabled()) {
            if (failureCause != null) {
                this.logger.debug("Could not complete request", failureCause);
            }
            else {
                if (asyncManager.isConcurrentHandlingStarted()) {
                    logger.debug("Leaving response open for concurrent processing");
                }
                else {
                    this.logger.debug("Successfully completed request");
                }
            }
        }

        publishRequestHandledEvent(request, response, startTime, failureCause);
    }
}
```



`DispatcherServlet` 内 `doService` 方法：

```java
protected void doService(HttpServletRequest request
						, HttpServletResponse response) throws Exception {
    if (logger.isDebugEnabled()) {
        String resumed = WebAsyncUtils
        		.getAsyncManager(request)
        		.hasConcurrentResult() ? " resumed" : "";
    }

	// 持有 include 请求的部分 attributes，以便能够恢复到原始数据。那么什么是 include ？
	// 下一步进行说明。
    Map<String, Object> attributesSnapshot = null;
    // 判断请求中是否包含 javax.servlet.include.request_uri 的 attribute。
    // 场景：一个 jsp -> A 引了另一个 jsp -> B：<jsp:include page="B.jsp"/>。
    // 在此种情况下，jsp A 在解析成 Servlet 时，会请求 jsp B，这就是 include。
    // 在此种情况下，被请求的 jsp B 中怎么获取 jsp A 的请求信息呢？
    // 名字如： javax.servlet.include.request_uri, javax.servlet.include.context_path 
    // 等的 attribute。可以直接通过 getAttributes 方法获取。
    if (WebUtils.isIncludeRequest(request)) {
        attributesSnapshot = new HashMap<>();
        Enumeration<?> attrNames = request.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            // 仅存储名字以 org.springframework.web.servlet 开头的属性。
            if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
                attributesSnapshot.put(attrName, request.getAttribute(attrName));
            }
        }
    }

    // 设置一些框架级的对象到 request 中，方便 handlers 及 视图对象获取。
    // 设置 DispatcherServlet 的上下文。
    request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
    // 设置 DispatcherServlet 的地域解析器。
    request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
    // 设置 DispatcherServlet 的主题解析器。
    request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
    // 设置主题资源，此处若 DispatcherServlet 的上下文实现了 ThemeSource 接口，则返回上下文，
    // 否则返回 null。
    request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
	// 判断 flashMapManager 是否为 null，这是 FlashMap 的管理器。
	// 什么是 FlashMap，通常 FlashMap 的实现都是通过 session 来完成，
	// 将一些数据存储到 session 中，方便指定的请求获取。
    if (this.flashMapManager != null) {
    	// 查找与当前请求相关的 FlashMap，删除它并返回它，删除其它过期的 FlashMap。
        FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
        if (inputFlashMap != null) {
        	// 将 inputFlashMap 设置到 request 中去。
        	// 这里将 inputFlashMap 转换为不可变的，以免被修改。
            request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE,
            	Collections.unmodifiableMap(inputFlashMap));
        }
        // 设置输出的 FlashMap
        request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
        // 设置 FlashMapManager
        request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
    }

    try {	
    	// 进行调度，这是请求处理的核心。
    	// 在下面进行说明。
        doDispatch(request, response);
    }
    finally {
        if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
            // Restore the original attribute snapshot, in case of an include.
            if (attributesSnapshot != null) {
                restoreAttributesAfterInclude(request, attributesSnapshot);
            }
        }
    }
}

protected void doDispatch(HttpServletRequest request
							, HttpServletResponse response) throws Exception {
	
    HttpServletRequest processedRequest = request;
    // 声明执行链。
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;
	// 获取异步管理器。
	WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

	try {
    	ModelAndView mv = null;
        Exception dispatchException = null;

		try {
             processedRequest = checkMultipart(request);
             multipartRequestParsed = (processedRequest != request);

		    // Determine handler for the current request.
             mappedHandler = getHandler(processedRequest);
             if (mappedHandler == null) {
             	noHandlerFound(processedRequest, response);
                return;
             }

			// Determine handler adapter for the current request.
             HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

			// Process last-modified header, if supported by the handler.
             String method = request.getMethod();
             boolean isGet = "GET".equals(method);
             if (isGet || "HEAD".equals(method)) {
             long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
             	 if (logger.isDebugEnabled()) {
                 logger.debug("Last-Modified value for [" + getRequestUri(request) 
                 		+ "] is: " + lastModified);
                 }
                 if (new ServletWebRequest(request, response)
                 			.checkNotModified(lastModified) && isGet) {
                 	return;
                 }
			}

			if (!mappedHandler.applyPreHandle(processedRequest, response)) {
				return;
              }

			 // Actually invoke the handler.
              mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

              if (asyncManager.isConcurrentHandlingStarted()) {
              	  return;
              }

			applyDefaultViewName(processedRequest, mv);
			mappedHandler.applyPostHandle(processedRequest, response, mv);
		}
		catch (Exception ex) {
			dispatchException = ex;
		}
		catch (Throwable err) {
			// As of 4.3, we're processing Errors thrown from handler methods as well,
			// making them available for @ExceptionHandler methods and other scenarios.
			dispatchException = 
				new NestedServletException("Handler dispatch failed", err);
		}
		processDispatchResult(processedRequest, response
					, mappedHandler, mv, dispatchException);
	}
	catch (Exception ex) {
		triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
	}
	catch (Throwable err) {
		triggerAfterCompletion(processedRequest, response, mappedHandler,
					new NestedServletException("Handler processing failed", err));
	}
	finally {
		if (asyncManager.isConcurrentHandlingStarted()) {
			// Instead of postHandle and afterCompletion
			if (mappedHandler != null) {
				mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
			}
		}
		else {
				// Clean up any resources used by a multipart request.
			if (multipartRequestParsed) {
				cleanupMultipart(processedRequest);
			}
		}
	}
}
```

