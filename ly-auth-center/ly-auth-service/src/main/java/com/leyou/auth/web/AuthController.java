package com.leyou.auth.web;

import com.leyou.auth.config.Jwtproperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        //将token存入cookie
        CookieUtils.newBuilder(response).httpOnly().request(request).build(prop.getCookieName(),token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
