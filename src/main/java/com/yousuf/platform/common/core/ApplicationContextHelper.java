package com.yousuf.platform.common.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * ClassName: ApplicationContextHelper
 * Description: springContext辅助类
 *
 * @author zhangshuai 2019/11/6
 */
@Component
@SuppressWarnings({"unchecked", "NullableProblems"})
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static BeanDefinitionRegistry beanDefinitionRegistry;

    /**
     * Title: registerBean
     * Description: 手动注册bean到spring容器
     *
     * @param beanName beanName
     * @param clazz 类
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public synchronized static void registerBean(String beanName, Class clazz) {
        if (null == beanName || null == clazz) {
            throw new RuntimeException(beanName + "注册失败");
        }
        BeanDefinition beanDefinition = getBeanDefinitionBuilder(clazz).getBeanDefinition();
        if (!beanDefinitionRegistry.containsBeanDefinition(beanName)) {
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private static BeanDefinitionBuilder getBeanDefinitionBuilder(Class clazz) {
        return BeanDefinitionBuilder.genericBeanDefinition(clazz);
    }

    /**
     * Title: setApplicationContext
     * Description: 初始化成员变量
     *
     * @param applicationContext context
     *
     * @author zhangshuai 2019/11/6
     *
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
        ConfigurableApplicationContext
                configurableApplicationContext =
                (ConfigurableApplicationContext) applicationContext;
        beanDefinitionRegistry =
                (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
    }

    /**
     * Title: getBean
     * Description: 手动获取spring容器中的bean对象
     *
     * @param targetClz 类
     *
     * @return T
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public static <T> T getBean(Class<T> targetClz) {
        T beanInstance = null;
        //byType
        try {
            beanInstance = applicationContext.getBean(targetClz);
        } catch (Exception ignored) {

        }
        //byName
        if (beanInstance == null) {
            String simpleName = targetClz.getSimpleName();
            simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            beanInstance = (T) applicationContext.getBean(simpleName);
        }
        return beanInstance;
    }

    /**
     * Title: getBean
     * Description: 通过名字获取类
     *
     * @param name 名字
     * @param requiredType 类型
     *
     * @return T
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * Title: getBean
     * Description: 根据名字获取Bean
     *
     * @param name 名字
     *
     * @return java.lang.Object
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
    /**
     * Title: getBeanMapByAnnotation
     * Description: 获取注解的类
     *
     * @param annotationClz 注解
     *
     * @return java.util.Map<java.lang.String,java.lang.Object>
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public static Map<String, Object> getBeanMapByAnnotation(Class<? extends Annotation> annotationClz) {
        return applicationContext.getBeansWithAnnotation(annotationClz);
    }
}
