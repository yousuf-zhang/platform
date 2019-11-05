## 统一异常处理
全局异常处理封装,统一返回错误格式的异常. 这里通过 `@ControllerAdvice` 注解来处理异常
```java
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public RestResponse<Object> handler(ServletRequest request, Throwable throwable, HttpServletResponse response) {
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (Objects.isNull(throwable)) {
            log.error("没有获取到异常信息");
            return RestResponse.error(GlobalCode.UNKNOWN);
        }
        
        // 业务异常
        if (throwable instanceof BaseException) {
            RestCode code = ((BaseException) throwable).getCode();
            log.warn("自定义异常 异常码 -> {}, 异常信息 -> {}", code.getCode(), code.getText());
            return RestResponse.error(code);
        }

        log.error("未知异常", throwable);
        return RestResponse.error(GlobalCode.UNKNOWN);
    }
}
```
异常时设置response的状态码为500, 表明服务器错误, 所有业务异常多需要继承`BaseException`类.
```java
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 6623312926437427049L;

    protected RestCode code;

    public BaseException() { }


    public BaseException(RestCode code, String message) {
        super(message);
        this.code = code;
    }
}
```
接下来测试一下全局异常, 新建`TestController`
```java
@RestController
public class TestController {

    @GetMapping("/test/exception")
    public RestResponse<String> getUtilsException() {
        throw new UtilsException(GlobalCode.UTILS_ERROR, "工具类异常");
        //return RestResponse.success();
    }

}
```
新建测试类进行测试
```java
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void test_exception_by_controller() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(RestResponse.error(GlobalCode.UTILS_ERROR));
            System.out.println(json);
            mockMvc.perform(MockMvcRequestBuilders.get("/test/exception"))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                    .andExpect(MockMvcResultMatchers.content().json(json))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

}
```
测试通过证明全局异常生效,进一步完善结果,返回结果为 `{"code":998,"message":"工具类不允许实例化","result":null}` result 的
值为null, 这里我们用`fastJson`替换掉springboot中的`jackson`
```java
@Configuration
public class FastJsonConfig {
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        //1. 需要定义一个converter转换消息的对象
        FastJsonHttpMessageConverter fasHttpMessageConverter = new FastJsonHttpMessageConverter();

        //2. 添加fastjson的配置信息，比如:是否需要格式化返回的json的数据
        com.alibaba.fastjson.support.config.FastJsonConfig fastJsonConfig = new com.alibaba.fastjson.support.config.FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);

        //3. 在converter中添加配置信息
        fasHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fasHttpMessageConverter);
    }
}
```
统一的异常处理就完成了,之前本来打算fastjson进行封装的,现在就不进行处理了,后期觉得有需要在封装.