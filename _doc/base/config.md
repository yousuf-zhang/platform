# 基础配置
- 数据库配置 选用的数据库连接池为springboot自带的hikari
```yaml
spring:
  datasource:
    url: url 
    username: username 
    password: pasword
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-test-query: select 1
      max-lifetime: 1800000
      connection-timeout: 30000
      pool-name: DatebookHikariCP
```
- jpa配置
```yaml
jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    open-in-view: true
    properties:
      hibernate:
        format_sql: true
```

