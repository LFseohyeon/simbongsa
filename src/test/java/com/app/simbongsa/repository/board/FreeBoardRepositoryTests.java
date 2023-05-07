package com.app.simbongsa.repository.board;

import com.app.simbongsa.entity.board.FreeBoard;
import com.app.simbongsa.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
@Slf4j
public class FreeBoardRepositoryTests {
    @Autowired
    private FreeBoardRepository freeBoardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveTest() {
        for(int i = 1; i <= 5; i++) {
            FreeBoard freeBoard = new FreeBoard("제목" + i, "내용" + i, userRepository.findById(542L).get());
            freeBoardRepository.save(freeBoard);
        }
    }

    @Test
    public void findAllWithPaging() {
        Page<FreeBoard> foundFreeBoard = freeBoardRepository.findAllWithPaging(PageRequest.of(0, 5));
        foundFreeBoard.stream().map(FreeBoard::toString).forEach(log::info);
        log.info("=====================" + foundFreeBoard.getTotalElements());
    }


    /* 유저별 자유게시판 목록 조회 (페이징처리) */
    @Test
    public void findByUserIdTest(){
        PageRequest pageRequest = PageRequest.of(0,4);
        Page<FreeBoard> freeBoards = freeBoardRepository.findByUserId(pageRequest, 146L);
        freeBoards.stream().map(FreeBoard::toString).forEach(log::info);
        log.info("----------------------유저 146L의 리뷰게시판 목록 수 --------------------" + freeBoards.getTotalElements());
    }

    /* 자유게시판 상세 조회 */
    @Test
    public void findByIdTest(){
        freeBoardRepository.findById(146L);
    }
}