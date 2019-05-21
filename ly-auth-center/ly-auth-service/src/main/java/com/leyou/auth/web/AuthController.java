package com.leyou.auth.web;

import com.leyou.auth.config.Jwtproperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(Jwtproperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private Jwtproperties prop;

    /**
     * 登录授权
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        //登录
        String token = authService.login(username, password);
        if (StringUtils.isBlank(token)) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //将token存入cookie
        CookieUtils.newBuilder(response).httpOnly().request(request).build(prop.getCookieName(),token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN")String token,
            HttpServletRequest request,
            HttpServletResponse response
            ){
        if (StringUtils.isBlank(token)) {
            //如果没有token，证明未登录，返回403
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        try {
            //解析token
            UserInfo info = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            //刷新token，重新写入token
            String newToken = JwtUtils.generateToken(info, prop.getPrivateKey(), prop.getExpire());
            //写入cookie
            CookieUtils.newBuilder(response).httpOnly().request(request).build(prop.getCookieName(),token);
            //已登录，返回用户信息
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            //token失效或者token被篡改
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
