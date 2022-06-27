package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    Member member = new Member();
    Book book = new Book();

    @BeforeEach
    public void before(){
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123"));
        memberRepository.save(member);

        book.setName("JPA BOOK");
        book.setPrice(10000);
        book.setStockQuantity(10);
        itemRepository.save(book);
    }
    
    @Test
    public void 상품주문() throws Exception {
        //given
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice());
        assertEquals(8, book.getStockQuantity());

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals(10, book.getStockQuantity());

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        int orderCount = 11;

        //when
        //then
        NotEnoughStockException thrown = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), book.getId(), orderCount));
        assertEquals("need more stock", thrown.getMessage());
    }
}