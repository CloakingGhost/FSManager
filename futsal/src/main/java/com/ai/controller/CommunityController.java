package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import com.google.api.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Controller
@Api(tags = {"커뮤니티관련 API"})
@RequestMapping(value = "/community")
public class CommunityController {
    @Autowired
    FieldService fService;
    @Autowired
    MemberService mService;
    @Autowired
    TeamService tService;
    @Autowired
    ReservationService rvService;
    @Autowired
    CommunityService cService;
    @Autowired
    SequenceGeneratorService sService;

    @RequestMapping(value = "/board")
    @ApiOperation(value = "메인 게시판으로 이동", response = CommunityDTO.class)
    public ModelAndView getBoard(@PageableDefault(page = 0, size = 10, sort = "idx", direction = Sort.Direction.DESC) Pageable pageable,
                                 HttpSession session) {
        Page<CommunityDTO> contents = cService.findAll(pageable);
        int nowPage = contents.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 9, 1);
        if (startPage < 0) startPage = 1;
        int endPage = Math.min(nowPage + 9, contents.getTotalPages());
        ModelAndView mav = new ModelAndView();


        Utils.getAlarms(mav, Utils.userInfo);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("member", member(session));
        mav.addObject("point", member(session).getHadPoint());
        mav.addObject("contents", contents);
        mav.addObject("startPage", startPage);
        mav.addObject("endPage", endPage);
        mav.addObject("nowPage", nowPage);
        return mav;
/*        ModelAndView mav = new ModelAndView();
        ArrayList<CommunityDTO> contents = cService.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), contents.size());
        List<CommunityDTO> subList = start >= end ? new ArrayList<>() : contents.subList(start, end);
        Page<CommunityDTO> pDto = new PageImpl<>(subList, pageable, contents.size());
        mav.addObject("point", Utils.userInfo.getHadPoint());
        mav.addObject("contents", pDto);
        mav.addObject("start", start);
        mav.addObject("end", end);*/
    }

    @RequestMapping(value = "/myBoard")
    @ApiOperation(value = "내글보기 게시판으로 이동", response = CommunityDTO.class)
    public ModelAndView getMyBoard(@PageableDefault(page = 0, size = 10, sort = "idx", direction = Sort.Direction.DESC) Pageable pageable,
                                   HttpSession session) {
        String nickName = member(session).getNickName();
        Page<CommunityDTO> contents = cService.findByWriter(nickName, pageable);
        int nowPage = contents.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        if (startPage < 0) startPage = 1;
        int endPage = Math.min(nowPage + 5, contents.getTotalPages());
        ModelAndView mav = new ModelAndView();

        Utils.getAlarms(mav, Utils.userInfo);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("member", member(session));
        mav.addObject("point", member(session).getHadPoint());
        mav.addObject("contents", contents);
        mav.addObject("startPage", startPage);
        mav.addObject("endPage", endPage);
        mav.addObject("nowPage", nowPage);
        mav.setViewName("/community/board");
        return mav;
    }

    @RequestMapping(value = "/write")
    @ApiOperation(value = "게시판에 글 작성으로 이동", response = CommunityDTO.class)
    public ModelAndView doWrite(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        MemberDTO member = Utils.userInfo;
        String writer = member.getNickName();
        TeamDTO myTeam = null;
        ArrayList<ReservationVO> new_reser = new ArrayList<>();
        ArrayList<ReservationVO> reservation;

        try {
            myTeam = tService.findBytName(member.getTName());
            reservation = rvService.findByid(writer).getInfo();

            int size = reservation.size();
            for (int i = 0; i < size; i++) {
                if (reservation.get(i).getState().equals("매칭 대기중") && reservation.get(i).getType().equals("홈")) {
                    new_reser.add(reservation.get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        Utils.getAlarms(mav, Utils.userInfo);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("member", member(session));
        mav.addObject("point", member(session).getHadPoint());
        mav.addObject("myTeam", myTeam);
        mav.addObject("new_reser", new_reser);
        mav.addObject("writer", writer);

        return mav;
    }

    @RequestMapping(value = "/doWrite")
    @ApiOperation(value = "게시판에 글 작성", response = CommunityDTO.class)
    public String insertBoard(CommunityDTO community) {
        community.setIdx(sService.generateSequence(CommunityDTO.SEQUENCE_NAME));
        community.setReply(new ArrayList<>());
        community.setReportNickNames(new ArrayList<String>());
        community.setReportMessages(new ArrayList<String>());
        cService.insert(community);
        return "redirect:/community/board";
    }

    @RequestMapping(value = "/communication/{targetBoard}")
    public ModelAndView doCommunication(@PathVariable(value = "targetBoard") Integer targetBoard, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        CommunityDTO community = cService.findByIdx(targetBoard);
        if (!community.getWriter().equals(member(session).getNickName())) {
            community.setViews(community.getViews() + 1);
            cService.save(community);
        }
        TeamDTO myTeam = tService.findBytName(community.getTName());
        Integer fldPrc = -1;
        String[] rsvFld = community.getRsvFld().split(",");
        if (!(rsvFld[0].contains("없습니다.") | rsvFld[0].contains("선택"))) {
            fldPrc = fService.findByfName(rsvFld[0]).getFPrice1();
        }

        Utils.getAlarms(mav, Utils.userInfo);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("member", member(session));
        mav.addObject("point", member(session).getHadPoint());
        mav.addObject("myTeam", myTeam);
        mav.addObject("community", community);
        mav.addObject("rsvFld", rsvFld); // 구장명, 날짜, 시간
        mav.addObject("fldPrc", fldPrc); // 구장사용 가격

        mav.setViewName("/community/communication");
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/fetchDeleteBoard")
    public ArrayList<CommunityDTO> getDeleteBoard(@RequestParam(value = "idx") Integer idx, @RequestParam(value = "nickName") String nickName) {
        ArrayList<CommunityDTO> dto = cService.findByWriter(nickName);
        for (int i = dto.size() - 1; i >= 0; i--) {
            if (dto.get(i).getIdx().equals(idx)) {
                cService.deleteByIdx(idx);
                break;
            }
        }
        return dto;
    }

    @RequestMapping(value = "/fetchModifyBoard/{idx}/{teamName}/{nickName}")
    public ModelAndView getModifyBoard(@PathVariable("idx") Integer idx, @PathVariable("teamName") String teamName,
                                       @PathVariable("nickName") String nickName, HttpSession session) {
        ModelAndView mav = new ModelAndView();

        // write 페이지가 열리려면 TeamDTO(myTeam)가 있어야함
        CommunityDTO board = cService.findByIdx(idx);
        TeamDTO myTeam = tService.findBytName(teamName);
        ArrayList<ReservationVO> new_reser = new ArrayList<>();
        ArrayList<ReservationVO> reservation;
        Integer point = null;
        try {
            point = mService.findByNickName(nickName).getHadPoint();
            reservation = rvService.findByid(myTeam.get_id()).getInfo();
            for (int i = reservation.size() - 1; i >= 0; i--) {
                if (reservation.get(i).getState().equals("매칭 대기중") && reservation.get(i).getType().equals("홈")) {
                    new_reser.add(reservation.get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Utils.getAlarms(mav, Utils.userInfo);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("member", member(session));
        mav.addObject("point", member(session).getHadPoint());
        mav.addObject("myTeam", myTeam);
        mav.addObject("writer", nickName);
        mav.addObject("board", board);
        mav.addObject("new_reser", new_reser);
        mav.setViewName("/community/write");

        return mav;
    }

    @RequestMapping(value = "/modifyBoard")
    public String doModifyBoard(CommunityDTO community) {
//        log.info("community : {}", community);
        String title = community.getTitle();
        String message = community.getMessage();
        String rsvFld = community.getRsvFld();
        CommunityDTO modifyBoard = cService.findByIdx(community.getIdx());

        modifyBoard.setTitle(title);
        modifyBoard.setMessage(message);
        modifyBoard.setRsvFld(rsvFld);
        cService.save(modifyBoard);

        return "redirect:/community/myBoard";
    }

    @ResponseBody
    @RequestMapping(value = "/fetchSearchBoard")
    public ModelAndView getSearchBoard(@RequestParam(value = "word") String word, @RequestParam(value = "num") int num,
                                       @RequestParam(value = "searchResult") String searchResult, HttpSession session,
                                       @PageableDefault(page = 0, size = 10, sort = "idx", direction = Sort.Direction.DESC) Pageable pageable) {
        ModelAndView mav = new ModelAndView("jsonView");
        ArrayList<CommunityDTO> myDto = cService.findByWriter(member(session).getNickName());
        Page<CommunityDTO> pDto = null;
        ArrayList<CommunityDTO> dto = new ArrayList<>();
        if (num == 0) {
            switch (searchResult) {
                case "제목":
                    pDto = cService.findByTitleRegex(word, pageable);
                    break;
                case "팀이름":
                    pDto = cService.findBytNameRegex(word, pageable);
                    break;
                case "작성자":
                    pDto = cService.findByWriterRegex(word, pageable);
                    break;
            }
        } else if (num == 1) {
            for (int i = myDto.size() - 1; i >= 0; i--) {
                try {
                    switch (searchResult) {
                        case "제목":
                            if (myDto.get(i).getTitle().contains(word)) dto.add(myDto.get(i));
                            break;
                        case "팀이름":
                            if (myDto.get(i).getTName().contains(word)) dto.add(myDto.get(i));
                            break;
                        case "작성자":
                            if (myDto.get(i).getWriter().contains(word)) dto.add(myDto.get(i));
                            break;
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), dto.size());
            List<CommunityDTO> subList = start >= end ? new ArrayList<>() : dto.subList(start, end);
            pDto = new PageImpl<>(subList, pageable, dto.size());
        }
        Utils.getAlarms(mav, Utils.userInfo);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("member", member(session));
        mav.addObject("point", member(session).getHadPoint());
        mav.addObject("dto", pDto);
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/fetchReply", method = RequestMethod.POST)
    public ArrayList<CommunityVO> getReply(CommunityVO vo, @RequestParam(value = "bNo") Integer bNo) {
        vo.setIdx(sService.generateSequence(vo.SEQUENCE_NAME));
        CommunityDTO dto = cService.findByIdx(bNo);
        ArrayList<CommunityVO> replies = dto.getReply();
        ArrayList<CommunityVO> newReply = new ArrayList<>();
        if (replies == null) {
            newReply.add(vo);
            dto.setReply(newReply);
            cService.save(dto);
        } else {
            replies.add(vo);
            dto.setReply(replies);
            cService.save(dto);
        }
        return dto.getReply();
    }

    @ResponseBody
    @PostMapping(value = "/deleteReply")
    public ArrayList<CommunityVO> getDeleteReply(@RequestParam("bNo") Integer bNo, @RequestParam("idx") Integer idx) {
        CommunityDTO dto = cService.findByIdx(bNo);
        ArrayList<CommunityVO> replies = dto.getReply();
        for (int i = replies.size() - 1; i >= 0; i--) {
            if (replies.get(i).getIdx().equals(idx)) {
                replies.remove(i);
                cService.save(dto);
                break;
            }
        }
        return replies;
    }

    @ResponseBody
    @PostMapping(value = "/modifyReply")
    public ArrayList<CommunityVO> getModifyReply(@RequestParam("editReply") String editReply,
                                                 @RequestParam("bNo") Integer bNo, @RequestParam("idx") Integer idx) {
        CommunityDTO dto = cService.findByIdx(bNo);
        ArrayList<CommunityVO> replies = dto.getReply();
        for (int i = replies.size() - 1; i >= 0; i--) {
            if (replies.get(i).getIdx().equals(idx)) {
                replies.get(i).setMessage(editReply);
                cService.save(dto);
                break;
            }
        }
        return replies;
    }

    public MemberDTO member(HttpSession session) {
        return mService.findByid((String) session.getAttribute("userId"));
    }
}