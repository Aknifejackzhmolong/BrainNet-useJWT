package com.brainsci.springsecurity.controller;

import com.brainsci.common.ResultBean;
import com.brainsci.common.ResultEnum;
import com.brainsci.common.ResultHandler;
import com.brainsci.form.CommonResultForm;
import com.brainsci.springsecurity.JwtAuthenticationToken;
import com.brainsci.springsecurity.JwtUserDetails;
import com.brainsci.springsecurity.entity.User;
import com.brainsci.springsecurity.execption.BaseException;
import com.brainsci.springsecurity.repository.UserRepository;
import com.brainsci.springsecurity.util.PasswordEncoder;
import com.brainsci.springsecurity.util.SecurityUtils;
import com.brainsci.springsecurity.vo.LoginBean;
import com.brainsci.springsecurity.vo.RegBean;
import com.brainsci.springsecurity.vo.TokenVo;
import com.brainsci.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.net.URLDecoder;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MailUtils mailUtils;

    @PostMapping(value = "/live")
    ResultBean live(){
        return ResultHandler.ok("active");
    }

    @PostMapping(value = "/login")
    ResultBean login(HttpSession session, @RequestBody LoginBean loginBean, HttpServletRequest request){
        String email = loginBean.getEmail();
        String passwd = loginBean.getPasswd();

        User user = userRepository.findUserByEmail(email);
        if (user == null) user = userRepository.findUserByName(email);

        System.out.println(loginBean);
        System.out.println(user);

        if(user==null)
            throw new BaseException(ResultEnum.LOGIN_ADMIN_NOT_FOUND);

        if (user.getValid()==0) {
            throw new BaseException(ResultEnum.LOGIN_ACCOUNT_FREEZED);
        }

        //session.setAttribute();
        // 系统登录认证

        System.out.println(String.format("rawPass=%s encodePass=%s",passwd,new PasswordEncoder(user.getSalt()).encode(passwd) ));

        if(!new PasswordEncoder(user.getSalt()).encode(passwd).toString().equals(user.getPassword()))
            throw new BaseException(ResultEnum.LOGIN_WRONG_PASSWD);

        JwtAuthenticationToken token = SecurityUtils.login(request,email , passwd, authenticationManager);

        return ResultHandler.ok(new TokenVo(token,user.getEmail()));
    }

    @PutMapping(value = "/register")
    ResultBean register(HttpSession httpSession, @RequestBody RegBean registerUser) {

        String verifyCode = (String) httpSession.getAttribute("verifyCode");

        if (verifyCode == null)
            throw new BaseException(ResultEnum.REGISTER_VERIFYCODE_EMPTY);
        else if (!verifyCode.equals(registerUser.getVerifyCode()+registerUser.getEmail()))
            throw new BaseException(ResultEnum.REGISTER_VERIFYCODE_ERROR);

        System.out.println("/register："+registerUser);

        String email = registerUser.getEmail();

        String name = registerUser.getName();

        User _userRes = userRepository.findUserByEmail(email);

        if(_userRes!=null)
            throw new BaseException(ResultEnum.REGISTER_ACCOUNT_EXISTED);

        _userRes = userRepository.findUserByName(name);

        if(_userRes!=null)
            throw new BaseException(ResultEnum.REGISTER_ACCOUNT_EXISTED);

        User user = new User();
        user.setAvatar(registerUser.getAvatar());
        user.setName(name);
        user.setEmail(email);
        String passwd = registerUser.getPassword();
        String salt = "./"+registerUser.getName();
        String passwdEncoded = new PasswordEncoder(salt).encode(passwd);
        user.setSalt(salt);
        user.setPassword(passwdEncoded);
        User userRes = userRepository.save(user);
        if(userRes == null)
            throw new BaseException(ResultEnum.FAIL);
        return ResultHandler.ok(user.getEmail());
    }
    /**
     * 获取验证码，答案放入session内。
     */
    @GetMapping(value = "/verifyMail")
    public CommonResultForm sendVerMail(@RequestParam String email, HttpSession httpSession) throws IOException, FontFormatException {
        String random = ((int)((Math.random()*9+1)*100000))+"";
        httpSession.setAttribute("verifyCode", random + URLDecoder.decode(email, "UTF-8"));
        mailUtils.sendVerifyMail(URLDecoder.decode(email, "UTF-8"), "Brain Sci Tools", random);
        return CommonResultForm.of204("已生成验证码");
    }

    @GetMapping(value = "/user/info")
    ResultBean adminInfo(HttpSession session, HttpServletRequest request){
        if(request.getHeader("Authorization")==null)
            throw new BaseException(ResultEnum.AUTHORIZATION_REQUIRED);
        System.out.println("Header:" + request.getHeader("Authorization"));
        System.out.println("JWT:" + SecurityContextHolder.getContext().getAuthentication());

        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = jwtUserDetails.getUsername();

        User user = userRepository.findUserByEmail(email);

        return ResultHandler.ok(user);
    }

}
