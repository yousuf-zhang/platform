package com.yousuf.platform.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: RsaHelperTest
 * <p> Description: RSA测试类
 *
 * @author zhangshuai 2019/11/7
 */
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
