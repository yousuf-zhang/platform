# 核心代码设计
最近在学习TDD希望整个工程都用TDD的思路来完成不断提升
自己的编程能力; 该项目为前后端分离的项目，该部分主要涉及后端工程，
采用springboot框架完成，api尽量遵循restful规范。  
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
