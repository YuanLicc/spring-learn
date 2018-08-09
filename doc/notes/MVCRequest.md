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
        super.service(request, response);
    }
}
```

先来看下 `service` 方法：

```java
// class HttpServlet

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

    } else if (method.equals(METHOD_POST)) {
        doPost(req, resp);

    } else if (method.equals(METHOD_PUT)) {
        doPut(req, resp);

    } else if (method.equals(METHOD_DELETE)) {
        doDelete(req, resp);

    } else if (method.equals(METHOD_OPTIONS)) {
        doOptions(req,resp);

    } else if (method.equals(METHOD_TRACE)) {
        doTrace(req,resp);

    } else {
        //
        // Note that this means NO servlet supports whatever
        // method was requested, anywhere on this server.
        //

        String errMsg = lStrings.getString("http.method_not_implemented");
        Object[] errArgs = new Object[1];
        errArgs[0] = method;
        errMsg = MessageFormat.format(errMsg, errArgs);

        resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
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

