作为前后端分离项目需要规范返回数据结构，通常情况下返回的数据结构包含状态码和描述信息和操作数据
- 成功时返回信息：
```json
{
  "code": 0,
  "message": "success",
  "result": {
    "username": "test",
    "likeName": "aaaa"
  }
}
```
对应的java代码为:
```java
public class RestResponse<T> implements Serializable {
    private static final long serialVersionUID = 7545652265259278927L;
    private Integer code;
    private String message;
    private T result;
    
    public RestResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public RestResponse() {
        this(GlobalCode.SUCCESS.getCode(), GlobalCode.SUCCESS.getText());
    }
    
    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }
    public static <T> RestResponse<T> error(RestCode code) {
        return new RestResponse<>(code.getCode(),code.getText());
    }

}
```
通过静态方法快速生成成功和失败的返回信息,其中 `restCode` 为一个接口,该接口继承了BaseEnum接口, 所有返回状态码都继承 
`RestCode`接口, 把不同的业务码通过不同Enum来封装.
```java
public interface RestCode extends BaseEnum<Integer> {

}
```
公用的状态码通过 `GlobalCode` 来封装
```java
public enum  GlobalCode implements RestCode {
    /**全局状态码*/
    SUCCESS(0, "Success"),
    ;

    private Integer code;
    private String text;
}
   
```
为了能够方便的操作枚举,项目中所有枚举类型都必须继BaseEnum接口, BaseEnum对象封装了操作枚举的常规方法.
```java
public interface BaseEnum<V> {
    V getCode();
    String getText();
    
    static <T extends Enum & BaseEnum> Optional<T> find(Class<T> type, Predicate<T> predicate) {
        if (type.isEnum()) {
            return Arrays.stream(type.getEnumConstants())
                    .filter(predicate).findFirst();
        }
        return Optional.empty();
    }
    
     static <T extends Enum & DictEnum<?>> Optional<T> findOptionalByCode(Class<T> type, Object code) {
        return find(type, e -> e.getCode() == code || e.getCode().equals(code)
                || String.valueOf(e.getCode()).equalsIgnoreCase(String.valueOf(code)));
    }
    
    // .......

}
```
其中 `findOptionalByCode` 方法是根据返回码获取到对应的枚举类型.