package com.gorany.ichatu.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString(exclude = "member")
public class Profile extends BaseEntity{

//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "profile_id")
//    private Long id;

    @Id @Column(name = "profile_id")
    private String id;

    private String name;
    private String path;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /* Change Method */
    public void update(Profile profile){

        String name = profile.getName();
        String path = profile.getPath();

        if(name != null && !name.equals(""))
            changeName(name);
        if(path != null && !path.equals(""))
            changePath(path);
    }
    private void changeName(String name){
        this.name = name;
    }
    private void changePath(String path){
        this.path = path;
    }
    public void changeMember(Member member){
        this.member = member;
    }
}
