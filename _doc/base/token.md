## 整合JWT TOKEN
选择的jwt jar包为 `jjwt`, token采用RSA加密,来提升token的安全性.  
首先我们来看一下RSA加密类( `RsaHelper`), 基本思路就是公钥私钥放到`resources`目录下,
初始化的时候加载秘钥. 其中有一个内部类 `RsaConfig`对外暴露配置参数,设置了默认值和必填值 
```java
@Slf4j
@Component
@DependsOn("applicationContextHelper")
public class RsaHelper {
    private static final String KEY_ALGORITHM = "RSA";
    private static RsaConfig rsaConfig;
    public static  RSAPublicKey PUBLIC_KEY;
    public static RSAPrivateKey PRIVATE_KEY;
    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {
        rsaConfig = ApplicationContextHelper.getBean(RsaConfig.class);
        PUBLIC_KEY = (RSAPublicKey) FileUtils.readObject(rsaConfig.getPublicKeyPath());
        PRIVATE_KEY = (RSAPrivateKey) FileUtils.readObject(rsaConfig.getPrivateKeyPath());

    }
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY);
            byte[] dataBytes = cipher.doFinal(data.getBytes(rsaConfig.getChatSet()));
            return Base64.getEncoder().encodeToString(dataBytes);
        } catch (Exception e) {
            log.warn("加密数据错误", e);
            throw new RsaException(GlobalCode.RSA_ERROR, "加密数据错误");
        }

    }
    public static String decrypt(String data) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, PRIVATE_KEY);
            return new String(cipher.doFinal(bytes),rsaConfig.getChatSet());
        } catch (Exception e) {
            log.warn("解密数据错误", e);
            throw new RsaException(GlobalCode.RSA_ERROR, "解密数据错误");
        }

    }

    public static void generateRsaKeyFile(String publicKeyName, String privateKeyName) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(rsaConfig.getKeySize());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        FileUtils.writeObject(keyPair.getPublic(), publicKeyName);
        FileUtils.writeObject(keyPair.getPrivate(),privateKeyName);
    }

    @Data
    @Validated
    @Configuration
    @ConfigurationProperties(prefix = "platform.rsa")
    public static class RsaConfig {
        @NotNull
        private String privateKeyPath;
        @NotNull
        private String publicKeyPath;
        // 秘钥长度
        private Integer keySize = 2048;
        //签名算法
        private String signatureAlgorithm = "SHA256withRSA";
        private String chatSet = "UTF-8";
    }

}
```
配置参数
```yaml
platform:
  jwt:
    expiration: 40
  rsa:
    private-key-path: /static/rsa/rsa-private-key.dat
    public-key-path: /static/rsa/rsa-public-key.dat
    key-size: 3072
```
RsaHelper测试
```java
@SpringBootTest
class RsaHelperTest {

    @Test
    void getKeyPair() throws Exception {
        RsaHelper.generateRsaKeyFile("D:/rsa-public-key.dat", "D:/rsa-private-key.dat");
    }

    @Test
    void testSign() {
        String test = RsaHelper.encrypt("asdfasdfasdfdsf");
        System.out.println(test);
        System.out.println(RsaHelper.decrypt(test));
        System.out.println(RsaHelper.decrypt("xbBilrMKSEJ+DqOCgh+vhb1AW6xxTKG2J+DEMNwrHy91vBy3ck+QbNuotDlAuHHTcqBGKvHMJQUDGoL9oILtft4Di9L1OxnnU8eOsV4p1WGBuW3X9zyYgiMEicqXBF/mXO/3qPuS245Gi3JiS9dm+R0no1TGwBL6IPiwPvbAtSfMaZGpR9Qn76sV5d1zGD58Ts9+c4dr5mqxGlnNILhiAvRMScYyKH0MZSX1E5HTf74Q0VwwUDoc2sJ6tZHopjMoRhJ4yKnpunsbpWJZTZ+4RsDjP4age8L9VGPdg2FzPSkXskOOFhmF+fqeUhXClEb6efvPCi2FF7+7aNVErCNHixp6p3kr5DXG4shyVMI7N/Za0UrjyXebXb3dDwBVcNrgE04pzA2Mn4QMqG9jjdz+AZWX3hr0ldOc21aQ6a7eWeiOPWOSkWt8sjoH8vYauEKE+MULd+os8H28Q7QUgGOFSr3WyOuNRVZqJG+/SoaTQl1Y+vhD4kV0QyQcHup4/jY5"));

    }
}
```
RSA加解密完成后我们开始整合jwt, `JwtTokenHelper`设计思路和RSA差不多,这里需要说明一下 `parseToken()`方法感觉设计的
不是太合理,当token过期时直接返回null值, 返回类型为 `Pair<String, String>`,这里返回两个值 `userId`和`username`,
暂时先这样设计,以后感觉不好用在重构.
```java
@Component
@DependsOn({"applicationContextHelper", "rsaHelper"})
public class JwtTokenHelper {
    private static JwtTokenConfig jwtTokenConfig;
    @PostConstruct
    public void init() {
        jwtTokenConfig = ApplicationContextHelper.getBean(JwtTokenConfig.class);
    }

    public static String generateToken(String tokenId, String tokenName) {
        return Jwts.builder()
                .claim(jwtTokenConfig.getTokenId(), tokenId)
                .claim(jwtTokenConfig.getTokenName(), tokenName)
                .setSubject(tokenName)
                .setExpiration(DateUtils.localDateTimeToDateConverter(
                    LocalDateTime.now()
                    .plusMinutes(jwtTokenConfig.getExpiration().toMinutes())
                ))
                .signWith(SignatureAlgorithm.RS256, RsaHelper.PRIVATE_KEY)
                .setIssuedAt(new Date())
                .compact();
    }

    public static Pair<String, String> parseToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(RsaHelper.PUBLIC_KEY).parseClaimsJws(token);
        if (claims.getBody().getExpiration().before(new Date())) {
            return null;
        }
        return Pair.of(Objects.toString(claims.getBody().get(jwtTokenConfig.getTokenId())),
                Objects.toString(claims.getBody().get(jwtTokenConfig.getTokenName())));
    }

    public static String getHeader() {
        return jwtTokenConfig.getHeader();
    }

    public static String getPrefix() {
        return jwtTokenConfig.getPrefix();
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "platform.jwt")
    public static class JwtTokenConfig {
        private String header = "Authorization";
        private String prefix = "Bearer ";
        private String tokenId = "userId";
        private String tokenName = "username";
        @DurationUnit(ChronoUnit.MINUTES)
        private Duration expiration = Duration.ofMinutes(120);
    }
}
```
测试类
```java
@SpringBootTest
class JwtTokenHelperTest {

    @Test
    void generateToken() {
        String token = JwtTokenHelper.generateToken("test", "username");
        Pair<String, String > pair = JwtTokenHelper.parseToken(token);
        Assertions.assertNotNull(pair);
        Assertions.assertEquals(pair.getLeft(), "test");
        Assertions.assertEquals(pair.getRight(), "username");

    }
}
```
jwt token算是整合完了,明天开始进行鉴权和认证操作,打算写两个版本一个基于 `spring security`一个自己手动写鉴权和认证操作,
感兴趣的可以关注一下我的github.
