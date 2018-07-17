import com.yl.learn.cglib.DefaultCglibDynamicProxy;
import com.yl.learn.jdk.DefaultJdkDynamicProxy;
import com.yl.learn.jdk.print.PrintAroundAdvice;
import com.yl.learn.spring.print.PrintAfterAdvice;
import com.yl.learn.spring.print.PrintBeforeAdvice;
import com.yl.learn.mvc.controller.ControllerMark;
import com.yl.learn.mvc.controller.TestMVCController;
import junit.framework.TestCase;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * AOP 测试
 * @author YuanLi
 */
public class ControllerAopTest extends TestCase {

    /**
     * JDK 代理测试
     */
    public void testJdkAop() {
        TestMVCController proxied = new TestMVCController();

        DefaultJdkDynamicProxy defaultJdkDynamicProxy =
                new DefaultJdkDynamicProxy(proxied, new PrintAroundAdvice());

        ControllerMark controllerMark = defaultJdkDynamicProxy.getProxy();
        controllerMark.printApiInfo();
    }

    /**
     * Cglib 代理测试
     */
    public void testCglibAop() {
        DefaultCglibDynamicProxy defaultCglibDynamicProxy =
                new DefaultCglibDynamicProxy(TestMVCController.class, new PrintAroundAdvice());

        ControllerMark controllerMark = defaultCglibDynamicProxy.getProxy();
        controllerMark.printApiInfo();
    }

    /**
     * Spring Cglib 测试
     */
    public void testSpringCglibAop() {
        ProxyFactory proxyFactory = new ProxyFactory();

        proxyFactory.setTarget(new TestMVCController());
        proxyFactory.addAdvice(new PrintBeforeAdvice());
        proxyFactory.addAdvice(new PrintAfterAdvice());

        ((TestMVCController) proxyFactory.getProxy()).printApiInfo();
    }

    /**
     * Spring 代理，未使用 Cglib
     */
    public void testSpringCglibPrintAopXML_singleBean_interface() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-interface-aop.xml");

        ControllerMark controllerMark = (ControllerMark) applicationContext.getBean("printProxy");
        controllerMark.printApiInfo();
    }

    /**
     * Spring Cglib 代理类
     */
    public void testSpringCglibPrintAopXML_singleBean_class() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-class-aop.xml");

        TestMVCController controllerMark = (TestMVCController) applicationContext.getBean("printProxy");
        controllerMark.hello();
    }

    /**
     * Spring Cglib 代理，单个 bean
     */
    public void testSpringCglibPrintAopXml_singleBean_advisor() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-advisor-aop.xml");

        TestMVCController controllerMark = (TestMVCController) applicationContext.getBean("printProxy");
        controllerMark.hello();
    }

    /**
     * Spring Cglib 代理，多个 bean
     */
    public void testSpringCglibPrintAopXml_multipartBean_advisor_auto() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-advisor-auto-aop.xml");

        TestMVCController controllerMark = (TestMVCController) applicationContext.getBean("testMVCController");
        controllerMark.printApiInfo();
    }

    /**
     * Spring Cglib 自动代理
     */
    public void testSpringCglibPrintAopXml_multipartBean_advisor_auto_proxy() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-advisor-auto-proxy-aop.xml");

        TestMVCController controllerMark = (TestMVCController) applicationContext.getBean("testMVCController");
        controllerMark.hello();
    }

    /**
     * 未代理成功的测试用例，暂未发现原因
     */
    public void testAspectJXml() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-aspectj-aop.xml");
        Object proxied = applicationContext.getBean("testMVCController");
        TestMVCController controllerMark = (TestMVCController)proxied;
        controllerMark.hello();
    }

    /**
     * 未代理成功的测试用例，暂未发现原因
     */
    public void testAspectJAnnotation() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-aspectj-annotation-aop.xml");
        Object proxied = applicationContext.getBean("testMVCController");
        TestMVCController controllerMark = (TestMVCController)proxied;
        controllerMark.hello();
    }

    /**
     * Spring Cglib 代理
     * @see org.springframework.aop.interceptor.SimpleTraceInterceptor
     */
    public void testTrace_XMl() {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("print-advisor-trace-auto-aop.xml");
        Object proxied = applicationContext.getBean("testMVCController");
        TestMVCController controllerMark = (TestMVCController)proxied;
        controllerMark.hello();
    }
}
