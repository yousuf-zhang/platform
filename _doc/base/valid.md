## 参数校验
参数校验我们通过`springboot`中的`Validation`来完成.  
`validation`配置信息:
```java
@Configuration
public class ValidatorConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        /**设置validator模式为快速失败返回*/
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
                .configure()
                .failFast(true)
                .buildValidatorFactory();

        return validatorFactory.getValidator();
    }
}
```
`Validation`的使用方法这里就不在叙述,现在写一个测试方法来测试是否可以使用.
测试dto
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoDTO implements Serializable {
    private static final long serialVersionUID = -2719383277784473799L;
    @NotNull(message = "title不能为空")
    private String title;
    @Length(max = 10, message = "desc不能超过10个字符")
    private String desc;
}
```
controller信息为
```java
@RestController
public class TestController {
    @PostMapping("/test/valid")
    public RestResponse<DemoDTO> getDemo(@Valid DemoDTO demoDTO) {
        return RestResponse.success(demoDTO);
    }

}
```
测试方法
```java
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_valid_title_null_return_unknown() throws Exception {
       String json = JSON.toJSONString(RestResponse.error(GlobalCode.UNKNOWN));
       System.out.println(json);
       mockMvc.perform(MockMvcRequestBuilders.post("/test/valid"))
           .andExpect(MockMvcResultMatchers.status().is5xxServerError())
           .andExpect(MockMvcResultMatchers.content().json(json))
           .andDo(MockMvcResultHandlers.print())
           .andReturn();
    }

}
```
这个时候返回的信息为 `{"code":999,"message":"系统异常"}` 这个不是我们想要的结果, 通过查看日志我们发现这样一个错误信息,报错
的异常类型为 `BindException`
```
2019-11-05 23:40:37.586 ERROR 21757 --- [           main] c.y.p.c.e.GlobalExceptionHandler         : 未知异常

org.springframework.validation.BindException: org.springframework.validation.BeanPropertyBindingResult: 1 errors
Field error in object 'demoDTO' on field 'title': rejected value [null]; codes [NotNull.demoDTO.title,NotNull.title,NotNull.java.lang.String,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [demoDTO.title,title]; arguments []; default message [title]]; default message [title不能为空]
	at org.springframework.web.method.annotation.ModelAttributeMethodProcessor.resolveArgument(ModelAttributeMethodProcessor.java:164) ~[spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:121) ~[spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]

```
现在我们对全局异常进行改造来精确的返回校验结果
```java
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @Order(1)
    @ResponseBody
    @ExceptionHandler(value= BindException.class)
    public RestResponse<Object> handleBindException(BindException ex, HttpServletResponse response){
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        BindingResult result = ex.getBindingResult();
        Set<ValidError> errors = Sets.newHashSet();
        result.getFieldErrors().forEach(fieldError -> {
            log.warn("参数格式不正确 {} -> {}", fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(new ValidError(fieldError.getField(), fieldError.getDefaultMessage()));
        });
        return RestResponse.error(GlobalCode.PARAMS_ERROR, errors);
    }

   // .... 
}
```
修改测试类
```java
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_valid_title_null_return_unknown() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/test/valid"))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError())
            .andExpect(jsonPath("code").value(997))
            .andExpect(jsonPath("errors").exists());
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        actions.andDo(MockMvcResultHandlers.print());
    }
}
```
现在返回的信息就是我们需要的了,日志信息
```
2019-11-06 01:45:33.805  INFO 1131 --- [           main] c.y.p.controller.TestControllerTest      : Started TestControllerTest in 2.06 seconds (JVM running for 2.652)

2019-11-06 01:45:33.954  WARN 1131 --- [           main] c.y.p.c.e.GlobalExceptionHandler         : 参数格式不正确 title -> title不能为空

MockHttpServletRequest:
      HTTP Method = POST
      Request URI = /test/valid
       Parameters = {}
          Headers = []
             Body = null
    Session Attrs = {}

Handler:
             Type = com.yousuf.platform.controller.TestController
           Method = com.yousuf.platform.controller.TestController#getDemo(DemoDTO)

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = org.springframework.validation.BindException

ModelAndView:
        View name = null
             View = null
            Model = null

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 500
    Error message = null
          Headers = [Content-Type:"application/json;charset=utf-8", Content-Length:"117"]
     Content type = application/json
             Body = {
	"code":997,
	"errors":[{
		"filed":"title",
		"message":"title不能为空"
	}],
	"message":"参数校验异常"
}
    Forwarded URL = null
   Redirected URL = null
          Cookies = []

```
上面的方法主要针对通过 `@Valid` 注解校验参数是使用,当有些场景下我们通过自己调用
验证时上述方法就失效了,下面我们就来解决这个问题, 封装一个 `ValidatorHelper`用于手动调用校验方法
这里涉及到 `@PostConstruct` 来加初始化静态变量和手动获取 **spring容器**中的**bean**对象，通过 `@DependsOn`注解来确保

`ApplicationContextHelper`在 `ValidatorHelper`之前加载。 首先我们来看一下 `ApplicationContextHelper`辅助类, 这里面提供了常规手动操作spring容器的方法
```java
@Component
@SuppressWarnings({"unchecked", "NullableProblems"})
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static BeanDefinitionRegistry beanDefinitionRegistry;

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
        ConfigurableApplicationContext
                configurableApplicationContext =
                (ConfigurableApplicationContext) applicationContext;
        beanDefinitionRegistry =
                (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
    }

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
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }
   
    public static Map<String, Object> getBeanMapByAnnotation(Class<? extends Annotation> annotationClz) {
        return applicationContext.getBeansWithAnnotation(annotationClz);
    }
}
```
下面是 `ValidatorHelper`辅助类，这里面提供了手动校验的一些方法
```java
@Component
@DependsOn("applicationContextHelper")
public class ValidatorHelper {
    private static Validator validator;

    @PostConstruct
    public void init() {
        validator = ApplicationContextHelper.getBean(Validator.class);
    }
    public static  <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, groups);

        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }
    public static <T> void validateProperty(T object, String propertyName, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validateProperty(object, propertyName, groups);
        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }
    public static <T> void validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validateValue(beanType, propertyName, value, groups);
        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }
}
```
所有准备工作都已经做完，现在开始测试是否生效
```java
@RestController
public class TestController {
    @PostMapping("/test/valid1")
    public RestResponse<DemoDTO> getDemo1(DemoDTO demoDTO) {
        // 手动校验
        ValidatorHelper.validate(demoDTO);
        return RestResponse.success(demoDTO);
    }

}
```
编写测试类
```java
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void test_valid() throws Exception {
        ResultActions actions = mockMvc.perform(post("/test/valid1")
                .param("title", "test")
                .param("desc", "asdfasdfasdfasdfasdfasdfasdfadsfasdf"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("code").value(997))
                .andExpect(jsonPath("errors").exists());
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        actions.andDo(MockMvcResultHandlers.print());
    }
}
```
测试失败查看输出日志，发现返回状态码为**999**未知异常 其中抛出的异常为 `ConstraintViolationException`
```
2019-11-06 11:45:41.661 ERROR 62904 --- [    Test worker] c.y.p.exception.GlobalExceptionHandler   : 未知异常

javax.validation.ConstraintViolationException: desc: desc不能超过10个字符
	at com.yousuf.platform.common.util.ValidatorHelper.validate(ValidatorHelper.java:43) ~[main/:na]
	at com.yousuf.platform.controller.TestController.getDemo1(TestController.java:36) ~[main/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_181]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_181]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_181]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_181]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190) ~[spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138) ~[spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106) ~[spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:888) ~[spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793) ~[spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040) ~[spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943) ~[spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) [spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909) [spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:665) ~[javax.servlet-api-4.0.1.jar:4.0.1]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883) [spring-webmvc-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.test.web.servlet.TestDispatcherServlet.service(TestDispatcherServlet.java:72) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:750) ~[javax.servlet-api-4.0.1.jar:4.0.1]
	at org.springframework.mock.web.MockFilterChain$ServletFilterProxy.doFilter(MockFilterChain.java:167) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:134) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) [spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) [spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:134) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) [spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) [spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:134) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) [spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) [spring-web-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:134) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at org.springframework.test.web.servlet.MockMvc.perform(MockMvc.java:183) [spring-test-5.2.0.RELEASE.jar:5.2.0.RELEASE]
	at com.yousuf.platform.controller.TestControllerTest.test_valid(TestControllerTest.java:66) [test/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_181]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_181]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_181]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_181]
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675) [junit-platform-commons-1.5.2.jar:1.5.2]
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60) [junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125) [junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132) [junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:124) [junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:74) [junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:202) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:198) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:135) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:69) ~[junit-jupiter-engine-5.5.2.jar:5.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at java.util.ArrayList.forEach(ArrayList.java:1257) ~[na:1.8.0_181]
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at java.util.ArrayList.forEach(ArrayList.java:1257) ~[na:1.8.0_181]
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51) ~[junit-platform-engine-1.5.2.jar:1.5.2]
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220) ~[na:na]
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188) ~[na:na]
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202) ~[na:na]
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181) ~[na:na]
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128) ~[na:na]
	at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.processAllTestClasses(JUnitPlatformTestClassProcessor.java:102) ~[na:na]
	at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.access$000(JUnitPlatformTestClassProcessor.java:82) ~[na:na]
	at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor.stop(JUnitPlatformTestClassProcessor.java:78) ~[na:na]
	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.stop(SuiteTestClassProcessor.java:61) ~[na:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_181]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_181]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_181]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_181]
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36) ~[na:na]
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) ~[na:na]
	at org.gradle.internal.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:33) ~[na:na]
	at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94) ~[na:na]
	at com.sun.proxy.$Proxy2.stop(Unknown Source) ~[na:na]
	at org.gradle.api.internal.tasks.testing.worker.TestWorker.stop(TestWorker.java:132) ~[na:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_181]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_181]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_181]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_181]
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36) ~[na:na]
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) ~[na:na]
	at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182) ~[na:na]
	at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164) ~[na:na]
	at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:412) ~[na:na]
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64) ~[na:na]
	at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48) ~[na:na]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[na:1.8.0_181]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[na:1.8.0_181]
	at org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56) ~[na:na]
	at java.lang.Thread.run(Thread.java:748) ~[na:1.8.0_181]


MockHttpServletRequest:
      HTTP Method = POST
      Request URI = /test/valid1
       Parameters = {title=[test], desc=[asdfasdfasdfasdfasdfasdfasdfadsfasdf]}
          Headers = []
             Body = null
    Session Attrs = {}

Handler:
             Type = com.yousuf.platform.controller.TestController
           Method = com.yousuf.platform.controller.TestController#getDemo1(DemoDTO)

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = javax.validation.ConstraintViolationException

ModelAndView:
        View name = null
             View = null
            Model = null

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 500
    Error message = null
          Headers = [Content-Type:"application/json", Content-Length:"42"]
     Content type = application/json
             Body = {
	"code":999,
	"message":"????????????"
}
    Forwarded URL = null
   Redirected URL = null
          Cookies = []

JSON path "code" expected:<997> but was:<999>
Expected :997
Actual   :999
<Click to see difference>

java.lang.AssertionError: JSON path "code" expected:<997> but was:<999>
	at org.springframework.test.util.AssertionErrors.fail(AssertionErrors.java:59)
	at org.springframework.test.util.AssertionErrors.assertEquals(AssertionErrors.java:98)
	at org.springframework.test.util.JsonPathExpectationsHelper.assertValue(JsonPathExpectationsHelper.java:116)
	at org.springframework.test.web.servlet.result.JsonPathResultMatchers.lambda$value$2(JsonPathResultMatchers.java:111)
	at org.springframework.test.web.servlet.MockMvc$1.andExpect(MockMvc.java:196)
	at com.yousuf.platform.controller.TestControllerTest.test_valid(TestControllerTest.java:70)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:124)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:74)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:202)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:198)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:135)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:69)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.processAllTestClasses(JUnitPlatformTestClassProcessor.java:102)
	at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.access$000(JUnitPlatformTestClassProcessor.java:82)
	at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor.stop(JUnitPlatformTestClassProcessor.java:78)
	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.stop(SuiteTestClassProcessor.java:61)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
	at org.gradle.internal.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:33)
	at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)
	at com.sun.proxy.$Proxy2.stop(Unknown Source)
	at org.gradle.api.internal.tasks.testing.worker.TestWorker.stop(TestWorker.java:132)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
	at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182)
	at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164)
	at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:412)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)
	at java.lang.Thread.run(Thread.java:748)

com.yousuf.platform.controller.TestControllerTest > test_valid() FAILED
```
修改我们的全局异常类
```java
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @Order(1)
    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public RestResponse<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                   HttpServletResponse response) {
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Set<ValidError> errors = Sets.newHashSet();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            ValidError error = new ValidError(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage());
            errors.add(error);
            log.warn("参数格式不正确 {} -> {}", error.getFiled(), error.getMessage());
        });
        return RestResponse.error(GlobalCode.PARAMS_ERROR, errors);
    }

}
```
执行测试 校验通过，关于参数校验的处理就全部做完了，其实前后端分离的项目参数校验
和显示应该放到前端做，当前端没有校验或者失效时，后端直接保存返回。
```
2019-11-06 11:53:50.854  WARN 65980 --- [    Test worker] c.y.p.exception.GlobalExceptionHandler   : 参数格式不正确 desc -> desc不能超过10个字符

MockHttpServletRequest:
      HTTP Method = POST
      Request URI = /test/valid1
       Parameters = {title=[test], desc=[asdfasdfasdfasdfasdfasdfasdfadsfasdf]}
          Headers = []
             Body = null
    Session Attrs = {}

Handler:
             Type = com.yousuf.platform.controller.TestController
           Method = com.yousuf.platform.controller.TestController#getDemo1(DemoDTO)

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = javax.validation.ConstraintViolationException

ModelAndView:
        View name = null
             View = null
            Model = null

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 500
    Error message = null
          Headers = [Content-Type:"application/json;charset=utf-8", Content-Length:"126"]
     Content type = application/json
             Body = {
	"code":997,
	"errors":[{
		"filed":"desc",
		"message":"desc不能超过10个字符"
	}],
	"message":"参数校验异常"
}
    Forwarded URL = null
   Redirected URL = null
          Cookies = []
```
