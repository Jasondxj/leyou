package com.leyou.order.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Slf4j
@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class Jwtproperties {

    private String pubKeyPath;
    private String cookieName;
    private PublicKey publicKey;


    //类一旦加载，就应该读取公钥和私钥
    @PostConstruct
    public void init() throws Exception {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
