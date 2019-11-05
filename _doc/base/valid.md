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
```java

```
