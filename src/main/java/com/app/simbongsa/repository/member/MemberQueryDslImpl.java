package com.app.simbongsa.repository.member;

import com.app.simbongsa.entity.member.QMember;
import com.app.simbongsa.entity.support.QSupport;
import com.app.simbongsa.search.admin.AdminMemberSearch;
import com.app.simbongsa.entity.member.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.app.simbongsa.entity.member.QMember.member;
import static com.app.simbongsa.entity.support.QSupport.support;
import static com.app.simbongsa.entity.rice.QRicePayment.ricePayment;


@RequiredArgsConstructor
public class MemberQueryDslImpl implements MemberQueryDsl {
    private final JPAQueryFactory query;

//    회원 전체 조회(페이징)
//    검색 포함(관리자)
    @Override
    public Page<Member> findAllWithPaging(AdminMemberSearch adminMemberSearch, Pageable pageable) {
        BooleanExpression memberEmailLike = adminMemberSearch.getMemberEmail() == null ? null : member.memberEmail.like("%" + adminMemberSearch.getMemberEmail() + "%");
        BooleanExpression memberAddressLike = adminMemberSearch.getMemberAddress() == null ? null : member.memberAddress.like("%" + adminMemberSearch.getMemberAddress() + "%");

        List<Member> foundMember = query.select(member)
                .from(member)
                .where(memberEmailLike, memberAddressLike)
                .orderBy(member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(member.count())
                .from(member)
                .fetchOne();

        return new PageImpl<>(foundMember, pageable, count);
    }

    @Override
//    @Modifying(clearAutomatically = true)
//    ??
    public void updateMember(Long id, String memberName, String memberAddress, String memberInterest, int memberVolunteerTime) {
        query.update(member)
                .set(member.memberName, memberName)
                .set(member.memberAddress, memberAddress)
                .set(member.memberInterest, memberInterest)
                .set(member.memberVolunteerTime, memberVolunteerTime)
                .where(member.id.eq(id))
                .execute();
    }

    @Override
    public List<Member> findMemberWithVolunteerTime() {
        return query.select(member).from(member).orderBy(member.memberVolunteerTime.desc()).limit(8).fetch();
    }

    //    비밀 번호 찾기
    @Override
    public Optional<Member> findByMemberEmailForPassword(String memberEmail) {
        return Optional.ofNullable(query.select(member).from(member).where(member.memberEmail.eq(memberEmail)).fetchOne());
    }

    //    비밀 번호 변경
    @Override
    public void updatePassword(Long id, String memberPassword) {
        query.update(member).set(member.memberPassword, memberPassword).where(member.id.eq(id)).execute();

    }

//    해당 후원 명단 조회
    @Override
    public List<Member> findSupportByRequestId(Long id) {
        return query.select(support.member)
                .from(support)
                .where(support.supportRequest.id.eq(id))
                .fetch();
    }

    /* 내 공양미 환전 요청*/
    @Override
    public void updateChangeRiceByMemberId(Long memberId, int changeRice) {
        query.update(member)
                .set(member.memberRice, member.memberRice.subtract(changeRice))
                .where(member.id.eq(memberId))
                .execute();
    }

    /* 공양미 충전 */
    @Override
    public void updateChargeRiceByMemberId(Long memberId, int chargeRice) {
        query.update(member)
                .set(member.memberRice, member.memberRice.add(chargeRice))
                .where(member.id.eq(memberId))
                .execute();
    }

    /* 이메일 로그인 */
    @Override
    public Optional<Member> login(String memberEmail, String memberPassword) {
        return Optional.ofNullable(
                query.select(member)
                        .from(member)
                        .where(member.memberEmail.eq(memberEmail)
                                .and(member.memberPassword.eq(memberPassword)))
                        .fetchOne());
    }

    /*이메일 중복 검사*/
    @Override
    public Optional<Member> overlapByMemberEmail(String memberEmail) {
        return Optional.ofNullable(query.select(member).from(member).where(member.memberEmail.eq(memberEmail)).fetchOne());
    }
}