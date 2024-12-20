package com.ai.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.domain.FieldDTO;
import com.ai.domain.MemberDTO;
import com.ai.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@Api(tags = {"에러 처리관련 API"})

public class ErrorPageController implements ErrorController {
   private static final String VIEW_PATH = "/errors/";
   @Autowired
   MemberService mService;

   @RequestMapping(value ="/error")
   @ApiOperation(value = "에러별로 나눠서 처리")
   public String handleError(HttpServletRequest request) {
      Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

      if(status != null){
         int statusCode = Integer.valueOf(status.toString());

         if(statusCode == HttpStatus.NOT_FOUND.value()){
            return VIEW_PATH + "404";
         }
         if(statusCode == HttpStatus.FORBIDDEN.value()){
            return VIEW_PATH + "500";
         }
      }

      return "error";
   }


   @RequestMapping(value ="/errors/404")
   @ApiOperation(value = "404에러 처리")
   @ResponseBody
   public void error404(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
      ModelAndView mav = new ModelAndView();
      MemberDTO member = mService.findByid((String)session.getAttribute("userId"));
      Integer point = null;
      try{
         point = member.getHadPoint();
      }catch(Exception e){
         log.info(e.getMessage());
      }
      mav.addObject("point", point);
   }

   @RequestMapping(value ="/errors/500")
   @ApiOperation(value = "500에러 처리")
   @ResponseBody
   public void error500(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
      ModelAndView mav = new ModelAndView();
      MemberDTO member = mService.findByid((String)session.getAttribute("userId"));
      Integer point = null;
      try{
         point = member.getHadPoint();
      }catch(Exception e){
         log.info(e.getMessage());
      }
      mav.addObject("point", point);
   }
}