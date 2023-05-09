package com.app.simbongsa.entity.inquiry;

import com.app.simbongsa.audit.Period;
import com.app.simbongsa.entity.member.Member;
import com.app.simbongsa.type.InquiryType;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter @ToString(exclude = "member")
@Table(name = "TBL_INQUIRY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Inquiry extends Period {
    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull private String inquiryTitle;
    @NotNull private String inquiryContent;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'답변대기'")
    @NotNull private InquiryType inquiryStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Member member;

//    단위 테스트용 생성자 생성
    public Inquiry(String inquiryTitle, String inquiryContent, Member member) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.member = member;
    }
}
