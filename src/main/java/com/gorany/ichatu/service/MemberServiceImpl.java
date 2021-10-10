package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.MemberDropRequest;
import com.gorany.ichatu.dto.MemberPasswordRequest;
import com.gorany.ichatu.dto.MemberProfileDTO;
import com.gorany.ichatu.repository.MemberRepository;
import com.gorany.ichatu.repository.ProfileRepository;
import com.gorany.ichatu.repository.jpaRepository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    //private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Override
    public MemberDTO getOne(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoResultException("결과가 없습니다."));

        return entityToDTO(member, null);
    }

    @Override
    @Transactional
    public Long signup(MemberDTO memberDTO) {

        /* 중복된 닉네임 CHECK */
        if(memberRepository.findCountByNickname(memberDTO.getNickname()) > 0L) {
            return -1L;
        }

        /* init */
        memberDTO.setAvailable(Boolean.TRUE);
        memberDTO.setRole(Role.USER);

        /* transform */
        Member member = dtoToEntity(memberDTO);

        /* persist */
        memberRepository.save(member);

        return member.getId();
    }

    @Override
    @Transactional
    /* DTO -> login -> entity -> DTO */
    public MemberDTO login(MemberDTO memberDTO) {

        Member member = dtoToEntity(memberDTO);
        Optional<Member> temp = memberRepository.match(member.getNickname(), member.getPassword());

        temp.ifPresent(Member::updateLoginDate);

        return temp.map(m -> entityToDTO(m, m.getProfile())).orElse(null);
    }

    @Override
    public void logout(Long id) {


    }

    @Override
    @Transactional
    public void updateMember(MemberDTO memberDTO) {
        Member requester = dtoToEntity(memberDTO);

        Member member = memberRepository.findMemberWithProfile(requester).orElseThrow(() ->  new NoResultException("결과가 없습니다."));

        Profile profile = requester.getProfile();
        Profile originProfile = member.getProfile();

        if(profile != null && originProfile != null)
            profileRepository.delete(originProfile);

        member.update(requester);
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
        Member member = memberRepository.findById(request.getId()).orElseThrow(NoResultException::new);
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
        Member member = memberRepository.findMemberWithProfile(source).orElseThrow(NoResultException::new);

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
