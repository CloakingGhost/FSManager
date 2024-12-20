package com.ai.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.domain.WeatherDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.ai.domain.FieldDTO;
import com.ai.domain.MemberDTO;
import com.ai.service.FieldService;
import com.ai.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@Api(tags = {"회원관련 API"})
@RequestMapping(path = "/member")
public class MemberController {
   @Autowired
   MemberService service;
   @Autowired
   FieldService fservice;

   // AJAX
   @RequestMapping(value = "/nickNameAjax")
   @ApiOperation(value = "닉네임 중복검사", response = MemberDTO.class)
   public void AjaxToNickName(@RequestParam("nickName") String nickName, HttpServletResponse response)
           throws Exception {
      System.out.println("Checking nickName.....");
      MemberDTO member = service.findByNickName(nickName);
      PrintWriter out = response.getWriter();
      if (member != null) {
         out.print("true");
      } else {
         out.print("false");
      }
   }

   // AJAX
   @RequestMapping(value = "/phoneNoAjax", method = RequestMethod.POST)
   @ApiOperation(value = "연락처 중복검사", response = MemberDTO.class)
   public void AjaxToPhoneNo(@RequestParam("phoneNo") String phoneNo, HttpServletResponse response) throws Exception {
      System.out.println("Checking phoneNo.....");
      MemberDTO member = service.findByPhoneNo(phoneNo);
      PrintWriter out = response.getWriter();
      if (member != null) {
         out.print("true");
      } else {
         out.print("false");
      }
   }

   @RequestMapping(value = "/likeFieldAjax", method = RequestMethod.POST)
   @ApiOperation(value = "구장 좋아요", response = MemberDTO.class)
   public void likeField(@RequestParam("field")String field, HttpServletResponse response,
                         HttpSession session) throws Exception {
      System.out.println("Checking likeFieldAjax..." + field);
      ModelAndView mav = new ModelAndView();
      MemberDTO member =  service.findByid((String)session.getAttribute("userId"));
      PrintWriter out = response.getWriter();
      FieldDTO pilde = fservice.findByfName(field);
      ArrayList<String> likefList = null;
      ArrayList<String> list = null;
      Integer likefListcnt = 0;
      // 회원의 찜 구장들 시작
      try{
         list = member.getLikeFieldList();
         if(list.contains(field) == true){
            for(int i=0; i< list.size(); i++){
               if(list.get(i).equals(field)){
                  list.remove(i);
                  out.print("fail");
               }
            }
         }else if(list.contains(field) == false){
            list.add(field);
            out.print("success");
         }
      }catch(Exception e){
         log.info("해당유저는 찜 목록이 없습니다");
      }if(list == null){
         list = new ArrayList<String>();
         list.add(field);
         out.print("success");
      }
      member.setLikeFieldList(list);
      service.save(member);
      // 회원의 찜 구장들 완료
      // 구장의 찜누른회원목록 시작
      try{
         likefList = pilde.getLikeList();
         likefListcnt = pilde.getLikeListCount();
         if(likefList.contains(member.getNickName()) == true){
            for(int i=0;i< likefList.size();i++){
               if(likefList.get(i).equals(member.getNickName())){
                  likefList.remove(i);
                  likefListcnt -= 1;
               }
            }
         }else if(likefList.contains(member.getNickName()) == false){
            likefList.add(member.getNickName());
            likefListcnt += 1;
         }
      }catch(Exception e){
         log.info("해당구장은 찜을 누른사람이 없습니다");
         if(likefList == null){
            likefList = new ArrayList<String>();
            likefList.add(member.getNickName());
         }if (likefListcnt == null){
            likefListcnt = likefList.size();
         }
      }
      log.info("liefListcnt : " + likefListcnt);
      pilde.setLikeList(likefList);
      pilde.setLikeListCount(likefListcnt);
      log.info("해당구장의 좋아요누른사람의 수 : " + pilde.getLikeListCount());
      fservice.save(pilde);
      //구장의 찜 누른회원목록 끝
   }

   @RequestMapping(value = "/join", method = RequestMethod.POST)
   @ApiOperation(value = "회원가입", response = MemberDTO.class)
   public ModelAndView insert(@RequestParam("name") String name, @RequestParam("nickName") String nickName,
                              @RequestParam("sex") String sex, @RequestParam("phoneNo") String phoneNo,
                              @RequestParam("tName") String tName, @RequestParam("hadPoint") int hadPoint, HttpSession session,
                              HttpServletRequest request, ArrayList<String> likeFieldList) {
      MemberDTO joinMember = new MemberDTO();
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String now = sdf.format(date);
      ModelAndView mav = new ModelAndView();
      Cookie[] cookies = request.getCookies();
      Cookie cookie = cookies[1];
      String platform = cookie.getValue().toString();
      String userId = (String) session.getAttribute("userId");
      joinMember.setId(userId);
      joinMember.setPlatform(platform);
      joinMember.setRegDate(now);
      joinMember.setName(name);
      joinMember.setNickName(nickName);
      joinMember.setSex(sex);
      joinMember.setPhoneNo(phoneNo);
      joinMember.setTName(tName);
      joinMember.setHadPoint(hadPoint);
      joinMember.setLikeFieldList(likeFieldList);

      //
      MemberDTO joinedMember = service.insert(joinMember); // set한거 DB에넣고, joinedMember에 담기
      mav.addObject("joinedMember", joinedMember);
      mav.setViewName("redirect:/main");
      System.out.println("userId : " + userId);
      System.out.println("platform : " + platform);
      System.out.println("Name : " + joinedMember.getName());
      System.out.println("nickName : " + joinedMember.getNickName());
      System.out.println("sex : " + joinedMember.getSex());
      System.out.println("phoneNo : " + joinedMember.getPhoneNo());
      System.out.println("tName : " + joinedMember.getTName());
      System.out.println("Point : " + joinedMember.getHadPoint());
      System.out.println("likeFieldList : " + joinedMember.getLikeFieldList());
      return mav;
   }
   @RequestMapping(value = "/addpoints", method = RequestMethod.POST)
   @ApiOperation(value = "결제 후 해당회원 포인트추가", response = MemberDTO.class)
   @ResponseBody
   public void addpoint(@RequestParam("point") Integer howmuch, HttpSession session)throws Exception{
      log.info("addpoint컨트롤러 들어옴 ,,, 받은 값 : " + howmuch);
      MemberDTO member = service.findByid((String)session.getAttribute("userId"));
      log.info("추가되기 전 회원의 포인트 : " + member.getHadPoint());
      member.setHadPoint(member.getHadPoint()+howmuch);
      service.save(member);
      log.info("추가된 후 회원의 포인트 : " + member.getHadPoint());
   }

}