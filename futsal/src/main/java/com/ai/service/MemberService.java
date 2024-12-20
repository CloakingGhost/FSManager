package com.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.ai.domain.MemberDTO;

import java.util.ArrayList;

@Service
public interface MemberService {
   public MemberDTO insert(MemberDTO joinMember);
   public MemberDTO findByPhoneNo(@RequestParam("phoneNo") String phoneNo);
   public MemberDTO findByNickName(@RequestParam("nickName") String nickName);
   public MemberDTO findByid(@RequestParam("_id") String _id);

   public MemberDTO findByidAndPlatform(@RequestParam ("_id") String _id, @RequestParam("platform") String platform);
   public int save(MemberDTO member);
   public ArrayList<MemberDTO> findAll();
}