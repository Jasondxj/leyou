package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.Jwtproperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@EnableConfigurationProperties(Jwtproperties.class)
@Service
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private Jwtproperties prop;
    public String login(String username, String password) {
        //校验用户名和密码
        try {
            User user = userClient.queryUserByUsernameAndPassword(username, password);
            if (user==null){
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username), prop.getPrivateKey(), prop.getExpire());
            return token;
        } catch (Exception e) {
            log.info("[授权中心]用户名或密码有误，用户名称:{}",username,e);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }
}
