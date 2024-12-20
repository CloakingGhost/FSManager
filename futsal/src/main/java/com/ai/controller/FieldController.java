package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;


@Slf4j
@Controller
@Api(tags = {"구장관련 API"})
@RequestMapping(value = "/field")
public class FieldController {
   @Autowired
   FieldService service;
   @Autowired
   TeamService tService;
   @Autowired
   ReserveService rService;
   @Autowired
   ReserveListService rlService;
   @Autowired
   MemberService mService;
   @Autowired
   FieldReviewService frService;
   @Autowired
   WeatherService wService;

   @RequestMapping(value = "/click", method = RequestMethod.GET)
   @ApiOperation(value = "구장 클릭", response = FieldDTO.class)
   public String clickToField(@RequestParam("fName") String fName) {
      FieldDTO field = service.findByfName(fName);
      return "redirect:/field/"+field.getId();
   }

   @RequestMapping(path = "/{id}", method = RequestMethod.GET)
   @ApiOperation(value = "구장번호로 검색", response = FieldDTO.class)
   public ModelAndView getField(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
      ModelAndView mav = new ModelAndView();
      FieldDTO field = service.findByid(id);
      ArrayList<FieldDTO> fList = service.findAll();
      ArrayList<String> fNList = new ArrayList<String>();
      ArrayList<String> latList = new ArrayList<String>();
      ArrayList<String> lonList = new ArrayList<String>();
      ArrayList<ReserveDTO> reserveList = new ArrayList<ReserveDTO>();
      String date = null;
      String time = null;
      ReserveDTO reserve = new ReserveDTO();
      TeamDTO homeInfo = new TeamDTO();
      for(int i = 0; i < fList.size(); i++) {
         fNList.add(fList.get(i).getFName());
         latList.add(fList.get(i).getLatitude());
         lonList.add(fList.get(i).getLongitude());
      }
      LinkedHashMap <String, String> timeMap = new LinkedHashMap <String, String>();
      Cookie[] cookies = request.getCookies();
      for(Cookie cookie : cookies) {
         if(cookie.getName().equals("date")) {
            date = cookie.getValue();
            System.out.println("현재 선택된 날짜 : " + date);
         }
         if(cookie.getName().equals("time")) {
            time = cookie.getValue();
            System.out.println("현재 선택된 시간 : " + time);
         }
      }
      ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
      int hours = nowSeoul.getHour();
      String now = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      log.info("현재 서울 시간 : " + hours);
      log.info("현재 날짜 : " +now);
      for(int j = 0; j < 12; j++) {
         timeMap.put(String.format("%d시 - %d시", 2*j, (2*j)+2), "null");
      }
      String[] timeArray = timeMap.keySet().toArray(new String[timeMap.size()]);

      try {
         reserveList = rService.findByField(field.getFName());
         for(int i = 0; i < reserveList.size(); i++) {
            if(reserveList.get(i).getDate().equals(date)) {
               try {
                  if(reserveList.get(i).getType().equalsIgnoreCase("part")) {
                     timeMap.replace(reserveList.get(i).getTime(), reserveList.get(i).getState());
                  }
                  if(reserveList.get(i).getType().equalsIgnoreCase("all") || reserveList.get(i).getState().equalsIgnoreCase("B")) {
                     timeMap.replace(reserveList.get(i).getTime(), "full");
                  }
               } catch (Exception e) {
                  System.out.println("Field Controller Error : " + e.getMessage());
               }
            }
         }
      } catch (Exception e) {
         System.out.println("해당 구장의 예약 정보가 없음!");
      }
      if(now.equals(date)) {
         System.out.println("현재 날짜와 선택된 날짜가 같음!");
         for(int p = 0; p < timeArray.length; p++) {
            String[] timeArray2 = timeArray[p].split("시 - ");
            String[] timeArray3 = timeArray2[1].split("시");
            if(hours >= Integer.parseInt(timeArray3[0].toString())) {
               timeMap.replace(timeArray[p].toString(), "full");
            }
         }
      }
      // 구장의 이름, 선택된 시간, 날짜 정보 조회
      try {
         reserve = rService.findReserve(field.getFName(), date, time);
         homeInfo = tService.findBytName(reserve.getNameA());
      } catch(Exception e) {
         reserve = null;
         homeInfo = null;
      }
      System.out.println("home팀 정보 : " + homeInfo);
      MemberDTO member = new MemberDTO();
      try{
         member = mService.findByid((String)session.getAttribute("userId"));
         mav.addObject("member", member);
      }catch (Exception e){
         mav.addObject("member", null);
      }
      try {
         TeamDTO myteam = tService.findBytName(member.getTName());
         mav.addObject("userTeamInfo", member.getTName());
         mav.addObject("myteam", myteam);
      } catch (Exception e){
         member = null;
         mav.addObject("userTeamInfo", null);
      }

      ArrayList<FieldReviewVO> reviewList = new ArrayList<FieldReviewVO>();
      FieldReviewDTO fieldreivew = new FieldReviewDTO();
      try{
         reviewList = frService.findByid(field.getFName()).getReviews(); // 해당구장리뷰
      }catch(Exception e){
         log.info(field.getFName() + "에 리뷰가 없습니다");
         reviewList = null;
      }
//      log.info("해당 구장 리뷰 : " + reviewList);

      // 해당 구장에 이미 좋아요를 눌렀으면 true, 반대면 false
      boolean alreadylikefield = false;
      try{
         if(member.getLikeFieldList().contains(field.getFName()) == true){
            alreadylikefield = true;
         }
      }catch(Exception e){
         log.info("해당 회원은 이 구장이 좋아요 리스트에 없습니다.");
      }
      WeatherDTO weather = wService.findByid(field.getFName());
      ArrayList<WeatherVO> weatherList = weather.getWeather();
      WeatherVO weathervo = null;
      MemberDTO dto = mService.findByid((String)session.getAttribute("userId"));
      Integer point = null;
      try{
         point = dto.getHadPoint();
      }catch(Exception e){
         log.info(e.getMessage());
      }
      int hour2 =0;
      if(hours >= 22 && hours < 1){
         hour2 = 0;
      }else if(hours < 4){
         hour2 = 3;
      }else if(hours < 7){
         hour2 = 6;
      }else if(hours < 10){
         hour2 = 9;
      }else if(hours < 13){
         hour2 = 12;
      }else if(hours < 16){
         hour2 = 15;
      }else if(hours < 19){
         hour2 = 18;
      }else if(hours < 22){
         hour2 = 21;
      }

      for (int i=0; i<weatherList.size();i++){
         if(weatherList.get(i).getDate().equals(now) && weatherList.get(i).getTime().equals(hour2)){
            weathervo = weatherList.get(i);
         }
      }


      Utils.getAlarms(mav,member);
      mav.addObject("uuid", UUID.randomUUID().toString());
      mav.addObject("member", member);
      mav.addObject("hours", hours);
      mav.addObject("point", point);
      mav.addObject("date", date);
      mav.addObject("now", now);
      mav.addObject("weathervo", weathervo);
      mav.addObject("weatherList", weatherList);
      mav.addObject("alreadylikefield", alreadylikefield);
      mav.addObject("reviewList", reviewList);
      mav.addObject("homeInfo", homeInfo);
      mav.addObject("timeMap", timeMap);
      mav.addObject("fNList", fNList);
      mav.addObject("latList", latList);
      mav.addObject("lonList", lonList);
      mav.addObject("field", field);
      mav.setViewName("/field");
      return mav;
   }

   @RequestMapping(value = "/search", method = RequestMethod.POST)
   @ApiOperation(value = "구장주소 및 구장명으로 검색", response = FieldDTO.class)
   @ResponseBody
   public Page<FieldDTO> searchFields(@RequestParam("searchField") String fName, @PageableDefault(size = 8) Pageable pageable) {
      ArrayList<FieldDTO> dtoList = service.findAll();
      List<FieldDTO> result = new ArrayList<>();
      int size = dtoList.size();
      for (int i = 0; i < size; i++) {
         if (dtoList.get(i).getFName().contains(fName) || dtoList.get(i).getFAddress().contains(fName)) {
            result.add(dtoList.get(i));
         }
         if(dtoList.get(i).getLikeListCount() < 0){
            dtoList.get(i).setLikeListCount(0);
            service.save(dtoList.get(i));
         }
      }
      int start = (int) pageable.getOffset();
      int end = Math.min(start + pageable.getPageSize(), result.size());
      List<FieldDTO> subList = start >= end ? new ArrayList<>() : result.subList(start, end);
      Page<FieldDTO> pResult = new PageImpl<>(subList, pageable, result.size());


        /*SearchMainDTO searchMain = new SearchMainDTO();
        searchMain.setNAfields(result);*/
      return pResult;
   }

   @RequestMapping(value = "/getHomeInfoToAjax", method = RequestMethod.POST)
   @ApiOperation(value = "해당 매치의 홈팀 정보 가져오기", response = FieldDTO.class)
   @ResponseBody
   public TeamDTO searchHomeTeam(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time){
      log.info("searchHomeTeam에 들어옴");
      TeamDTO homeTeam = new TeamDTO();
      log.info("getHomeInfoToAjax field : " + field);
      log.info("getHomeInfoToAjax date : " + date);
      log.info("getHomeInfoToAjax time : " + time);
      try {
         homeTeam = tService.findBytName(rService.findReserve(field, date, time).getNameA()); //2시 - 4시  2022-11-29  1
      } catch (Exception e){
         log.info(e.getMessage());
      }
      log.info("homeTeam 로고패스 : " + homeTeam.getLogoPath());
      return homeTeam;
   }

   @RequestMapping(value = "/insertReview", method = RequestMethod.POST)
   @ApiOperation(value = "구장 리뷰 작성", response = FieldDTO.class)
   @ResponseBody
   public FieldReviewVO insertReviewAjax(@RequestParam("field") String field, @RequestParam("name") String name, @RequestParam("review") String review) throws Exception {
      log.info("InsertReview컨트롤러에 들어옴");

      log.info("구장명 : " + field);
      log.info("작성자 : " + name);
      log.info("리뷰내용 : " + review);
      Integer reviewscnt = 0;
      ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
      String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
      // 구장명으로 해당하는 getreviews  -> VO객체
      // 그 VO객체를 구장명에 해당하는 DTO객체에 add
      // DTO객체가 null 이면 첫번째 인덱스에 set
      // else - 그 DTO객체에 add
      // 그 DTO객체 save
      FieldReviewVO vo = new FieldReviewVO(name, review, parsedcurDateTime); // VO객체에 담기
      FieldReviewDTO fieldreview = new FieldReviewDTO(); //빈 DTO객체 생성
      ArrayList<FieldReviewVO> volist = null; // 빈 vo객체리스트생성
      FieldDTO pilde = service.findByfName(field);
      // 기존의 dto객체중 id가 field랑 같은게 존재하면 해당dto객체의 vo리스트에 vo객체 add 그리고 save
      // 존재하지 않으면 빈 dto 객체에 set 인덱스 0번째의 vo리스트에 vo객체 그리고 save
      try{
         reviewscnt = frService.findByid(field).getReviews().size();
         volist = frService.findByid(field).getReviews(); // 해당구장에 리뷰가 있으면 담아라

      }catch(Exception e){ //해당구장에 리뷰가 없으면
         log.info("해당 구장에 리뷰는 존재하지 않아요 : " + e.getMessage());
         volist = new ArrayList<FieldReviewVO>();
         if(reviewscnt == null){
            reviewscnt = volist.size();
         }
      }
      log.info("reviewscnt : " + reviewscnt);
      volist.add(vo);
      reviewscnt += 1;
      pilde.setReviewsCount(reviewscnt);
      pilde.setReviews(volist);
      fieldreview.setId(field);
      fieldreview.setReviews(volist);
      frService.save(fieldreview);
      service.save(pilde);
//      log.info("ajax다시보낼 vo : " + vo);

      return vo;
   }

   @RequestMapping(value = "/deleteReviewAjax", method = RequestMethod.POST)
   @ApiOperation(value = "구장 리뷰 삭제", response = FieldDTO.class)
   @ResponseBody
   public ArrayList<FieldReviewVO> deleteReview(@RequestParam("field") String field, @RequestParam("nickName") String nickName, @RequestParam("regDate") String regDate){
      log.info("deleteReviewAjax 컨트롤러 들어옴");
      log.info("deleteReviewAjax field파라미터 : " + field);
      log.info("deleteReviewAjax nickName파라미터 : " + nickName);
      log.info("deleteReviewAjax regDate파라미터 : " + regDate);

      FieldReviewDTO fr = frService.findByid(field);
      FieldDTO fi = service.findByfName(field);
      Integer reviewscnt = service.findByfName(field).getReviewsCount();
      ArrayList<FieldReviewVO> volist = new ArrayList<>();

      for(int i=0; i<fr.getReviews().size(); i++){ //해당구장의리뷰다가져온걸 하나씩 돌려서 리뷰리스트안에 vo리스트안에 인덱스i번째것들의 작성자와 작성일자가 같은 인덱스i번째놈들을 vo리스트에서 지우고 세이브
         if(fr.getReviews().get(i).getNickName().equals(nickName) && fr.getReviews().get(i).getRegDate().equals(regDate)){
            log.info("삭제 하는 해당 vo 객체 : " + fr.getReviews().get(i));
            fr.getReviews().remove(i);
            fi.getReviews().remove(i);
            reviewscnt = fr.getReviews().size();
            fi.setReviewsCount(reviewscnt);
            frService.save(fr);
            service.save(fi);
            log.info("삭제 성공 후 로그찍어요 ");
         }
      }
      volist = fr.getReviews();
      return volist;
   }

   @RequestMapping(value = "/modifyReviewAjax", method = RequestMethod.POST)
   @ApiOperation(value = "구장 리뷰 수정중", response = FieldDTO.class)
   @ResponseBody
   public ArrayList<FieldReviewVO> modifyReview(@RequestParam("field") String field, @RequestParam("nickName") String nickName,
                                                @RequestParam("review") String review, @RequestParam("regDate") String regDate){
      log.info("modifyReviewAjax 컨트롤러 들어옴");
      log.info("modifyReviewAjax field파라미터 : " + field);
      log.info("modifyReviewAjax nickName파라미터 : " + nickName);
      log.info("modifyReviewAjax 수정전regDate파라미터 : " + regDate);
      log.info("modifyReviewAjax 수정전review파라미터 : " + review);

      FieldReviewDTO fr = frService.findByid(field);
      FieldDTO fi = service.findByfName(field);
      ArrayList<FieldReviewVO> volist = new ArrayList<>();
      for(int i=0; i<fr.getReviews().size(); i++){
         if(fr.getReviews().get(i).getNickName().equals(nickName) && fr.getReviews().get(i).getRegDate().equals(regDate)){
            fr.getReviews().remove(i);
            fi.getReviews().remove(i);
         }
      }
      volist = fr.getReviews();
      return volist;
   }

   @RequestMapping(value = "/realModifyReviewAjax", method = RequestMethod.POST)
   @ApiOperation(value = "구장 리뷰 수정확정", response = FieldDTO.class)
   @ResponseBody
   public ArrayList<FieldReviewVO> realModifyReview(@RequestParam("field") String field, @RequestParam("nickName") String nickName,
                                                    @RequestParam("review") String review, @RequestParam("regDate") String regDate){
      log.info("realModifyReviewAjax 컨트롤러 들어옴");
      log.info("realModifyReviewAjax field파라미터 : " + field);
      log.info("realModifyReviewAjax nickName파라미터 : " + nickName);
      log.info("realModifyReviewAjax 수정전regDate파라미터 : " + regDate);
      log.info("realModifyReviewAjax 수정후review파라미터 : " + review);

      FieldReviewDTO fr = frService.findByid(field);
      FieldDTO fi = service.findByfName(field);
      ArrayList<FieldReviewVO> volist = new ArrayList<>();
      for(int i=0; i<fr.getReviews().size(); i++){
         if(fr.getReviews().get(i).getNickName().equals(nickName) && fr.getReviews().get(i).getRegDate().equals(regDate)){
            fr.getReviews().get(i).setReview(review);
            fi.getReviews().get(i).setReview(review);
         }
      }
      frService.save(fr);
      service.save(fi);
      volist = fr.getReviews();
      log.info("volist첫번째리스트의 리뷰 : " + volist.get(0).getReview());
      return volist;
   }

   @RequestMapping(value = "/backToModalAjax", method = RequestMethod.POST)
   @ApiOperation(value = "구장 리뷰 수정중 돌아가기", response = FieldDTO.class)
   @ResponseBody
   public ArrayList<FieldReviewVO> backtomodal(@RequestParam("field") String field){
      log.info("backToModalAjax 컨트롤러 들어옴");
      log.info("backToModalAjax field파라미터 : " + field);

      FieldReviewDTO fr = frService.findByid(field);
      ArrayList<FieldReviewVO> volist = new ArrayList<>();
      volist = fr.getReviews();
      return volist;
   }

   @RequestMapping(value = "/insertReviewModifying", method = RequestMethod.POST)
   @ApiOperation(value = "구장 리뷰 수정 중 작성", response = FieldDTO.class)
   @ResponseBody
   public ArrayList<FieldReviewVO> insertReviewModifyingAjax(@RequestParam("field") String field, @RequestParam("name") String name, @RequestParam("review") String review) throws Exception {
      log.info("insertReviewModifying 컨트롤러 들어옴");
      log.info("insertReviewModifying field파라미터 : " + field);
      log.info("insertReviewModifying name파라미터 : " + name);
      log.info("insertReviewModifying review파라미터 : " + review);

      FieldReviewDTO fr = frService.findByid(field);
      FieldDTO fi = service.findByfName(field);
      Integer reviewscnt = 0;
      ArrayList<FieldReviewVO> volist = fr.getReviews();
      ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
      String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
      FieldReviewVO vo = new FieldReviewVO(name, review, parsedcurDateTime);
      fr.getReviews().add(vo);
      fi.getReviews().add(vo);
      reviewscnt = fr.getReviews().size();
      fi.setReviewsCount(reviewscnt);
      frService.save(fr);
      service.save(fi);
      volist = fr.getReviews();
      log.info("insertReviewModifying 컨트롤러 결과 volist첫번째리스트의 리뷰 : " + volist.get(0).getReview());
      return volist;
   }

   @RequestMapping(value = "/plzsuunyAjax", method = RequestMethod.POST)
   @ApiOperation(value = "구장에 선택 날짜 시간에 날씨정보 가져오기", response = FieldDTO.class)
   @ResponseBody
   public WeatherVO plzsunnyAjax(@RequestParam("time") String time, @RequestParam("date") String date,
                                 @RequestParam("field") String field)throws IOException{
      log.info("plzsunnyAjax 컨트롤러 들어옴 ,,,,");
      log.info("plzsunnyAjax 컨트롤러 time 파라미터 : " + time);
      log.info("plzsunnyAjax 컨트롤러 date 파라미터 : " + date);
      log.info("plzsunnyAjax 컨트롤러 field 파라미터 : " + field);
      String fName = service.findByid(field).getFName();
      log.info("plzsunnyAjax 컨트롤러 field 파라미터로 찾은 구장 이름 : " + fName);
//      log.info("plzsunnyAjax 컨트롤러 weathervo객체 : " + wService.findByid(field).getWeather());
      WeatherVO weather = new WeatherVO();
      Integer thistime = 0;
      switch (time){
         case "0시 - 2시":
            thistime = 0;
            break;
         case "2시 - 4시":
            thistime = 0;
            break;
         case "4시 - 6시":
            thistime = 3;
            break;
         case "6시 - 8시":
            thistime = 6;
            break;
         case "8시 - 10시":
            thistime = 6;
            break;
         case "10시 - 12시":
            thistime = 9;
            break;
         case "12시 - 14시":
            thistime = 12;
            break;
         case "14시 - 16시":
            thistime = 12;
            break;
         case "16시 - 18시":
            thistime = 15;
            break;
         case "18시 - 20시":
            thistime = 18;
            break;
         case "20시 - 22시":
            thistime = 18;
            break;
         case "22시 - 24시":
            thistime = 21;
            break;
      }
      WeatherDTO dto = wService.findByid(fName);
      WeatherVO lastvo = dto.getWeather().get(39); // DB에 있는 마지막 날씨정보
      LocalDate localDate1 = LocalDate.parse(lastvo.getDate());
      LocalDate localDate2 = LocalDate.parse(date);
      System.out.println("날짜빼기결과 : " + localDate1.isAfter(localDate2)); // true면 앞에꺼가 뒤에인자보다 뒤에날짜임
      boolean result = localDate1.isAfter(localDate2);
      if(result == true){ // 날씨정보볼수있음
         try{
            for (int i=0; i<dto.getWeather().size();i++){
               if(dto.getWeather().get(i).getDate().equals(date)){
                  if(dto.getWeather().get(i).getTime().equals(thistime)){
                     weather = dto.getWeather().get(i);
                  }
               }
            }
            log.info("컨트롤러 결과 : " + weather);
         }catch (Exception e){
            log.info("plzsunnyAjax 컨트롤러날씨vo객체 찾기 실패 : " + e.getMessage());
         }
      }else{ // 해당날짜에 대한 날씨DB존재 X
         weather = null;
      }
      return weather;
   }


}