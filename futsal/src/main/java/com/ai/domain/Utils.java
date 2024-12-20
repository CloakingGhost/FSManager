package com.ai.domain;

import com.ai.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Slf4j
public class Utils {

    @Autowired
    static MemberService mService;

    public static final int reportCount = 3;

    public static ArrayList<String> fNList;
    public static ArrayList<String> latList;
    public static ArrayList<String> lonList;

    // 로그인 시 세션 유저 정보 - 돈 관련되서 +, - 될때 본인 값 변경시켜주면 됨.
    public static MemberDTO userInfo;


    public static void getAlarms(ModelAndView mav, MemberDTO member) {
        ArrayList<AlarmVO> readalarms = new ArrayList<>();
        ArrayList<AlarmVO> unreadalarms = new ArrayList<>();
        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("읽음")) {
                    readalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            readalarms = null;
        }
        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("안읽음")) {
                    unreadalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            unreadalarms = null;
        }
        mav.addObject("unreadalarms", unreadalarms);
        mav.addObject("readalarms", readalarms);
    }
}
