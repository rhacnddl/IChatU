package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.ProfileDTO;
import org.springframework.lang.Nullable;

public interface MemberService {

    MemberDTO getOne(Long memberId);
    Long signup(MemberDTO memberDTO);
    MemberDTO login(MemberDTO memberDTO);
    void logout(Long id);
    String updateMember(MemberDTO memberDTO);

    default Member dtoToEntity(MemberDTO dto){

        /* JoinDTO -> Join */

        /* ProfileDTO -> Profile */
        ProfileDTO profileDTO = dto.getProfileDTO();
        Profile profile = profileDTO != null?
                Profile.builder().id(profileDTO.getProfileId()).name(profileDTO.getName()).path(profileDTO.getPath()).build()
                : null;

        return Member.builder()
                .id(dto.getId())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .loginDate(dto.getLoginDate())
                .role(dto.getRole())
                .available(dto.getAvailable())
                .profile(profile)
                .build();
    }

    default MemberDTO entityToDTO(@Nullable Member member, @Nullable Profile profile){

        /* Join -> JoinDTO */

        /* Profile -> ProfileDTO */
        ProfileDTO profileDTO = profile != null?
                ProfileDTO.builder().profileId(profile.getId()).name(profile.getName()).path(profile.getPath()).memberId(member.getId()).build()
                : null;

        /* Notification -> NotificationDTO */

        return member != null? MemberDTO.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                //.password(member.getPassword())
                .email(member.getEmail())
                .loginDate(member.getLoginDate())
                .role(member.getRole())
                .available(member.getAvailable())
                .profileDTO(profileDTO)
                .build()
                : null;
    }
}
