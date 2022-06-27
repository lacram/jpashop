package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Lim");

        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test()
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("Lim");

        Member member2 = new Member();
        member2.setName("Lim");

        //when
        memberService.join(member1);

        //then
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class,
                ()-> memberService.join(member2));
        Assertions.assertEquals(thrown.getMessage(), "이미 존재하는 회원입니다.");
    }
}
