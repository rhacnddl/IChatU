package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.ProfileDTO;
import com.gorany.ichatu.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Long signup(MemberDTO memberDTO) {

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
    /* DTO -> login -> entity -> DTO */
    public MemberDTO login(MemberDTO memberDTO) {

        Member member = dtoToEntity(memberDTO);
        Member result = memberRepository.match(member);

        return entityToDTO(result);
    }

    @Override
    public void logout(Long id) {


    }

    @Override
    public void updateMember(MemberDTO memberDTO, ProfileDTO profileDTO) {

    }
}
