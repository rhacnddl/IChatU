package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.ProfileDTO;
import org.springframework.lang.Nullable;

public interface MemberService {

    Long signup(MemberDTO memberDTO);
    MemberDTO login(MemberDTO memberDTO);
    void logout(Long id);
    void updateMember(MemberDTO memberDTO, @Nullable ProfileDTO profileDTO);

    default Member dtoToEntity(MemberDTO dto){

        /* JoinDTO -> Join */

        /* ProfileDTO -> Profile */

        /* NotificationDTO -> Notification */

        return Member.builder()
                .id(dto.getId())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .loginDate(dto.getLoginDate())
                .role(dto.getRole())
                .available(dto.getAvailable())
                .build();
    }

    default MemberDTO entityToDTO(Member member){

        /* Join -> JoinDTO */

        /* Profile -> ProfileDTO */

        /* Notification -> NotificationDTO */

        return MemberDTO.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                //.password(member.getPassword())
                .email(member.getEmail())
                .loginDate(member.getLoginDate())
                .role(member.getRole())
                .available(member.getAvailable())
                .build();
    }
}
