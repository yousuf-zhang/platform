package com.yousuf.platform.common.util;

import com.yousuf.platform.common.core.ApplicationContextHelper;
import com.yousuf.platform.exception.RsaException;
import com.yousuf.platform.exception.code.GlobalCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * ClassName: RSAUtils
 * <p> Description: RSA加密辅助类
 *
 * @author zhangshuai 2019/11/7
 */
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
    /**
     * <p> Title: encrypt
     * <p> Description: 通过RSA加密数据
     *
     * @param data 明文数据
     *
     * @return java.lang.String
     *
     * @author zhangshuai 2019/11/7
     *
     */
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

    /**
     * <p> Title: decrypt
     * <p> Description: 解密数据
     *
     * @param data 加密数据
     *
     * @return java.lang.String
     *
     * @author zhangshuai 2019/11/7
     *
     */
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
    /**
     * <p> Title: getKeyPair
     * <p> Description: 生成密钥对
     *
     * @throws Exception 获取加密异常
     *
     * @author zhangshuai 2019/11/7 
     *
     */
    public static void generateRsaKeyFile(String publicKeyName, String privateKeyName) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(rsaConfig.getKeySize());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        FileUtils.writeObject(keyPair.getPublic(), publicKeyName);
        FileUtils.writeObject(keyPair.getPrivate(),privateKeyName);
    }

    /**
     * ClassName: RsaConfig
     * <p> Description: RSA参数配置
     *
     * @author zhangshuai 2019/11/7
     */
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
