package com.brainsci.brainnet;

import com.brainsci.form.CommonResultForm;
import com.brainsci.form.NetAnalysisOption;
import com.brainsci.service.MRIService;
import com.brainsci.service.GretnaService;
import com.brainsci.springsecurity.JwtUserDetails;
import com.brainsci.springsecurity.entity.User;
import com.brainsci.springsecurity.repository.UserRepository;
import com.brainsci.springsecurity.util.GsonPlus;
import com.brainsci.utils.MatlabUtils;
import com.brainsci.websocket.form.WebSocketMessageForm;
import com.brainsci.websocket.server.WebSocketServer;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BrainNetController {

    private final GretnaService gretnaService;
    private final MRIService MRIService;
    private final UserRepository userRepository;

    @Autowired
    public BrainNetController(GretnaService gretnaService, MRIService MRIService, UserRepository userRepository) {
        this.gretnaService = gretnaService;
        this.MRIService = MRIService;
        this.userRepository = userRepository;
    }

    @ApiOperation(value = "网络分析")
    @PostMapping(value = "/gretna/{task}/{token}")
    public CommonResultForm gretnaNetworkAnalysis(@PathVariable("task") String task,@RequestBody NetAnalysisOption para, @PathVariable("token") String token, HttpServletRequest request, HttpSession httpSession){
        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwtUserDetails.getUsername();
        String userHomeDir = userRepository.findUserByEmail(email).getSalt();
        if (MatlabUtils.state.containsKey(userHomeDir)) return CommonResultForm.of204("Tasks are "+MatlabUtils.state.get(userHomeDir)+", please wait");
        MatlabUtils.state.put(userHomeDir, "submitted");
        WebSocketServer.sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("gretnaState", "submitted")),token);
        gretnaService.networkAnalysis(userHomeDir,task, token, para);
        return CommonResultForm.of204("Tasks are queuing");
    }
    @ApiOperation(value = "预处理(fMRI)")
    @PostMapping(value = "/fmri/{task}/{token}")
    public CommonResultForm cpac(@PathVariable("task") String task,@PathVariable("token") String token,@RequestBody Map<String, String> map, HttpServletRequest request, HttpSession httpSession) throws Exception{
//        String userHomeDir = userBaseRepository.findUserBaseListByUserEmail((String) httpSession.getAttribute("username")).get(0).getHomeDirectory();
        String str = map.get("jsonstr");
        WebSocketServer.sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("cpacState", "submitted")),token);
        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwtUserDetails.getUsername();
        String username = userRepository.findUserByEmail(email).getName();
        String userHomeDir = userRepository.findUserByEmail(email).getSalt();
        MRIService.cpac(new HashMap<String, String>(){{
            this.put("username", username);
            this.put("userHomeDir", userHomeDir);
            this.put("email", email);
        }}, task, token, str);
        return CommonResultForm.of204("success");
    }
    @ApiOperation(value = "预处理(sMRI)")
    @PostMapping(value = "/smri/{task}/{token}")
    public CommonResultForm sMRI(@PathVariable("task") String task,@PathVariable("token") String token,@RequestBody Map<String, String> map, HttpServletRequest request, HttpSession httpSession) throws Exception{
//        String userHomeDir = userBaseRepository.findUserBaseListByUserEmail((String) httpSession.getAttribute("username")).get(0).getHomeDirectory();
        String str = map.get("jsonstr");
        WebSocketServer.sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("cpacState", "submitted")),token);
        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwtUserDetails.getUsername();
        String username = userRepository.findUserByEmail(email).getName();
        String userHomeDir = userRepository.findUserByEmail(email).getSalt();
        MRIService.sMRI(new HashMap<String, String>(){{
            this.put("username", username);
            this.put("userHomeDir", userHomeDir);
            this.put("email", email);
        }}, task, token, str);
        return CommonResultForm.of204("success");
    }
    @ApiOperation(value = "预处理(sMRI,并行)")
    @PostMapping(value = "/smriParallel/{task}/{token}")
    public CommonResultForm smriParallel(@PathVariable("task") String task,@PathVariable("token") String token,@RequestBody Map<String, String> map, HttpServletRequest request, HttpSession httpSession) throws Exception{
//        String userHomeDir = userBaseRepository.findUserBaseListByUserEmail((String) httpSession.getAttribute("username")).get(0).getHomeDirectory();
        String str = map.get("jsonstr");
        WebSocketServer.sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("cpacState", "submitted")),token);
        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwtUserDetails.getUsername();
        User user = userRepository.findUserByEmail(email);
        MRIService.sMRI_parallel(new HashMap<String, String>(){{
            this.put("username", user.getName());
            this.put("userHomeDir", user.getSalt());
            this.put("email", email);
        }}, task, token, str);
        return CommonResultForm.of204("success");
    }
    @ApiOperation(value = "DTI")
    @PostMapping(value = "/dti/{task}/{token}")
    public CommonResultForm dti(@PathVariable("task") String task,@PathVariable("token") String token,@RequestBody Map<String, String> map, HttpServletRequest request, HttpSession httpSession) throws Exception{
//        String userHomeDir = userBaseRepository.findUserBaseListByUserEmail((String) httpSession.getAttribute("username")).get(0).getHomeDirectory();
        String str = map.get("jsonstr");
        WebSocketServer.sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("cpacState", "submitted")),token);
        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwtUserDetails.getUsername();
        User user = userRepository.findUserByEmail(email);
        MRIService.dti(new HashMap<String, String>(){{
            this.put("username", user.getName());
            this.put("userHomeDir", user.getSalt());
            this.put("email", email);
        }}, task, token, str);
        return CommonResultForm.of204("success");
    }
    @ApiOperation(value = "网络模块状态")
    @PostMapping(value = "/gretnaState")
    public CommonResultForm gretnaState(HttpSession httpSession){
        JwtUserDetails jwtUserDetails =
                (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwtUserDetails.getUsername();
        String userHomeDir = userRepository.findUserByEmail(email).getSalt();
        if (MatlabUtils.state.containsKey(userHomeDir)) return CommonResultForm.of204("Tasks are "+MatlabUtils.state.get(userHomeDir)+", please wait");
        MatlabUtils.state.put(userHomeDir, "submitted");
        return CommonResultForm.of204("Tasks are queuing");
    }
    @RequestMapping(value = "/")
    public void index(HttpServletResponse response) throws IOException{
        response.sendRedirect("/bsci/index.html");
    }
}