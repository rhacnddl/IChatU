package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.MemberDropRequest;
import com.gorany.ichatu.dto.MemberPasswordRequest;
import com.gorany.ichatu.dto.MemberProfileDTO;
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

        memberRepository.updateMember(member);

        Profile profile = member.getProfile();

        return profile != null? profile.getId() : "null";
    }

    @Override
    public MemberProfileDTO getMemberInfo(Long id) {
        Optional<Member> temporary = memberRepository.findMemberWithProfile(Member.builder().id(id).build());

        if(temporary.isEmpty()) return null;

        return MemberProfileDTO.createMemberProfileDTO(temporary.get());
    }

    @Override
    @Transactional
    public Boolean updatePassword(MemberPasswordRequest request) {

        /* 기존 멤버를 찾는다. */
        Member member = memberRepository.findMemberWithProfile(Member.builder().id(request.getId()).build()).get();
        /* 기존 비밀번호와 동일한지 체크한다. */
        Boolean isCorrect  = member.isCorrectPassword(request.getOldPassword());
        /* 다를 때 */
        if(!isCorrect){
            return false;
        }
        /* 동일할 때 */
        member.changePassword(request.getNewPassword());
        return true;
    }

    @Override
    @Transactional
    public Boolean dropMember(MemberDropRequest request) {

        Member source = Member.builder().id(request.getId()).build();
        /* 멤버 정보 가져오기 */
        Member member = memberRepository.findMemberWithProfile(source).get();

        /* 요청자의 비밀번호가 일치한지? */
        Boolean isCorrect = member.isCorrectPassword(request.getPassword());

        /* TRUE: 탈퇴 */
        if(isCorrect){

            member.deleteUser();

            return true;
        }
        /* FALSE: .. */
        else{
            return false;
        }
    }
}
