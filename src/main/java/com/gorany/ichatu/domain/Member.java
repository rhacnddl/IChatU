package com.gorany.ichatu.domain;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString(exclude = {"joinList", "notificationList"})
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", unique = true)
    private Long id;

    @Column
    private String nickname;

    @Column
    private String password;

    private String email;

    private LocalDateTime loginDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "boolean default true")
    private Boolean available;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    @Builder.Default
    private List<Join> joinList = new ArrayList<>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "receiver")
//    @Builder.Default
//    private List<Notification> notificationList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    private Profile profile;

    /* Static Factory Method */
    public static Member createMember(Member member, @Nullable Profile profile){

        if(profile != null)
            profile.changeMember(member);

        return Member.builder()
                .id(member.getId())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .available(member.getAvailable())
                .role(member.getRole())
                .profile(profile)
                .loginDate(member.getLoginDate())
                .build();
    }

    /* Change Method */
    public void update(Member member){

        String email = member.getEmail();
        String password = member.getPassword();
        Role role = member.getRole();
        Boolean available = member.getAvailable();
        Profile profile = member.getProfile();

        if(email != null && !email.equals("")) changeEmail(email);
        if(password != null && !password.equals("")) changePassword(password);
        if(role != null) changeRole(role);
        if(available != null) changeAvailable(available);
        if(profile != null) changeProfile(profile);
    }
    private void changeEmail(String email){
        this.email = email;
    }
    private void changeRole(Role role){
        this.role = role;
    }
    private void changeAvailable(Boolean available){
        this.available = available;
    }
    public void changePassword(String password){
        /* 추후 암호화 적용 */
        this.password = password;
    }
    public void changeProfile(Profile profile){
        this.profile = profile;
        profile.changeMember(this);
    }

    /* Parameter의 비밀번호가 맞는지 Check */
    public Boolean isCorrectPassword(String password){
        return this.password.equals(password);
    }

    /* Login시, 적용 */
    public void updateLoginDate(){
        this.loginDate = LocalDateTime.now();
    }

    /* 회원 탈퇴 */
    public void deleteUser(){
        this.available = false;
        this.nickname = "탈퇴한 사용자";
        this.email = null;
        this.password = null;

        Profile profile = this.profile;
        if(profile != null)
            profile.delete();
    }
}
