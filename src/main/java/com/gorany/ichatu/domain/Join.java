package com.gorany.ichatu.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Joins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Join extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "joins_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    /* 팩토리 생성 메서드 */
    public static Join createJoin(Member member, ChatRoom chatRoom){
        return Join.builder()
                .member(member)
                .chatRoom(chatRoom)
                .build();
    }
}
