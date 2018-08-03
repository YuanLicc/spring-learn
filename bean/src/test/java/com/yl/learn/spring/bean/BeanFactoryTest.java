package com.yl.learn.spring.bean;

import com.yl.common.util.PrintUtil;
import com.yl.learn.BeanFactoryBuilder;
import junit.framework.TestCase;

public class BeanFactoryTest extends TestCase {

    public void testDefaultFactory() {
        PrintUtil.printLine();
        BeanFactoryBuilder.build("Spring.xml");
    }

}
