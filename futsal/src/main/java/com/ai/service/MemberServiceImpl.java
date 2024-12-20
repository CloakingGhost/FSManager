package com.ai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.domain.MemberDTO;
import com.ai.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@Component
public class MemberServiceImpl implements MemberService{
   @Autowired
   MemberRepository repo;
   
   @Override
   public MemberDTO insert(MemberDTO joinMember) {
      // TODO Auto-generated method stub
       MemberDTO joinedMember = repo.insert(joinMember); 
       return joinedMember;
      
   }

	@Override
	public MemberDTO findByPhoneNo(String phoneNo) {
		MemberDTO member = repo.findByPhoneNo(phoneNo);
		return member;
	}

	@Override
	public MemberDTO findByNickName(String nickName) {
		MemberDTO member = repo.findByNickName(nickName);
		return member;
	}

	@Override
	public MemberDTO findByid(String _id) {
		MemberDTO member = repo.findByid(_id);
		return member;
	}

	@Override
	public MemberDTO findByidAndPlatform(String _id, String platform) {
		return repo.findByidAndPlatform(_id, platform);
	}

	@Override
	public int save(MemberDTO member) {
		// TODO Auto-generated method stub
		repo.save(member);
		int k = 1;

		return k;
	}

	@Override
	public ArrayList<MemberDTO> findAll() {
		ArrayList<MemberDTO> memList = repo.findAll();
		return memList;
	}


}