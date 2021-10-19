package com.gorany.ichatu.repository.query;

import com.gorany.ichatu.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class AsideQueryRepositoryImpl implements AsideQueryRepository{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Object[]> getAsideChatRoomsWithJoinByMemberUsingNativeSQL(Member member) {
        String nativeSQL = "select " +
                "cr.chat_room_id id, cr.name, cr.reg_date, " +
                "m.member_id memberId, m.nickname, " +
                "p.profile_id profileId, p.name profileName, p.path profilePath, " +
                "r.region_id regionId, count(n.notification_id) cnt, " +
                "(select content from chat where chat_room_id = j.chat_room_id order by chat_id desc limit 1) content, " +
                "(select reg_date from chat where chat_room_id = j.chat_room_id order by chat_id desc limit 1) contentRegDate, " +
                "j.member_id " +
                "from joins j " +
                "left join chat_room cr on j.chat_room_id = cr.chat_room_id " +
                "left join member m on cr.member_id = m.member_id " +
                "left join profile p on m.member_id = p.member_id " +
                "left join region r on cr.region_id = r.region_id " +
                "left join notification n on cr.chat_room_id = n.target_id and :member_id = n.receiver_member_id and n.confirm = '0' " +
                "group by " +
                "cr.chat_room_id, cr.name, cr.reg_date, " +
                "m.member_id, m.nickname, " +
                "p.profile_id, p.name, p.path, " +
                "r.region_id, " +
                "content, contentRegDate, " +
                "j.member_id " +
                "having j.member_id = :member_id " +
                "order by cr.chat_room_id desc";

        return em.createNativeQuery(nativeSQL)
                .setParameter("member_id", member.getId())
                .getResultList();
    }
}
