package com.gorany.ichatu.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString(exclude = {"sender, receiver"})
public class Notification extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private Long targetId;

    private Character confirm;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member receiver;

    /*
    * Domain Business Methods
    * */
    //알림 미확인 -> 확인
    public void confirm(){
        this.confirm = '1';
    }
}
