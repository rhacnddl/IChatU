package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDTO getOne(Long memberId) {

        Member member = memberRepository.findById(memberId).get();

        return entityToDTO(member, null);
    }

    @Override
    @Transactional
    public Long signup(MemberDTO memberDTO) {

        /* 중복된 닉네임 CHECK */
        if(memberRepository.findByNickname(memberDTO.getNickname()) > 0L){
            return -1L;
        }

        /* init */
        memberDTO.setAvailable(Boolean.TRUE);
        memberDTO.setRole(Role.USER);

        /* transform */
        Member member = dtoToEntity(memberDTO);

        /* persist */
        Long id = memberRepository.save(member);

        return id;
    }

    @Override
    @Transactional
    /* DTO -> login -> entity -> DTO */
    public MemberDTO login(MemberDTO memberDTO) {

        Member member = dtoToEntity(memberDTO);
        Optional<Member> temp = memberRepository.match(member);

        return temp.isPresent()? entityToDTO(temp.get(), temp.get().getProfile()) : null;
    }

    @Override
    public void logout(Long id) {


    }

    @Override
    @Transactional
    public String updateMember(MemberDTO memberDTO) {
        Member member = dtoToEntity(memberDTO);
        System.out.println("member = " + member);
        memberRepository.updateMember(member);

        Profile profile = member.getProfile();

        return profile.getId();
    }
}
