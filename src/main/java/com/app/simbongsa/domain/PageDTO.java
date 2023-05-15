package com.app.simbongsa.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@Slf4j
public class PageDTO {
    private int startPage;
    private int endPage;
    private int total;
    private boolean next, prev;
    private int pageNum;
    private final int AMOUNT = 5;
    private int startNum;

    public PageDTO (Page<?> list) {
        this.pageNum = list.getNumber() + 1;
        this.total = (int)list.getTotalElements();
        //시작페이지, 마지막페이지 계산
        this.endPage = (int)(Math.ceil(pageNum / (double)AMOUNT)) * AMOUNT;
        this.startPage = this.endPage - 4;

        int listSize = list.getSize();

        if(total < 5) {
            log.info("???");
            listSize = total;
        }
        int realEnd = (int) Math.ceil(Math.ceil(total * 1.0) / listSize);

        if(realEnd < this.endPage) {
            this.endPage = realEnd;
        }

        //이전, 다음 버튼 표출 여부 결정
        this.prev = this.startPage > 1;
        this.next = this.endPage < realEnd;
    }
}