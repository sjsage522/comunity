package com.example.comunity.repository;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.Comment;
import com.example.comunity.domain.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("레포지토리 테스트 (comment)")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-pg")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    EntityManager em;

    @BeforeEach
    void before() {
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title", "content");

        em.persist(user);
        em.persist(codingCategory);
        em.persist(board);

        Comment parentComment1 = Comment.of(user, board, "parentComment1");
        em.persist(parentComment1); //count: 1

        Comment parentComment2 = Comment.of(user, board, "parentComment2");
        em.persist(parentComment2); //count: 2
        em.persist(Comment.of(user, board, "parentComment3")); //count : 3

        Comment childComment1 = Comment.of(user, board, "childComment1");
        em.persist(childComment1); //count: 4

        Comment childComment2 = Comment.of(user, board, "childComment2");
        em.persist(childComment2); //count: 5

        Comment childComment3 = Comment.of(user, board, "childComment3");
        em.persist(childComment3); //count: 6

        parentComment1.addChildComment(childComment1);
        parentComment1.addChildComment(childComment2);
        parentComment2.addChildComment(childComment3);
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 아이디로 모든 답글 조회")
    void _01_findAllByBoardIdIfParentIsNullWithPaging_succeed_test() {
        final List<Comment> all = commentRepository.findAll();
        final Comment comment = all.get(0);
        System.out.println(comment.getBoard().getId());

        List<Comment> commentList = commentRepository.findAllByBoardId(1L);

        assertThat(commentList.size()).isEqualTo(6);
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 아이디로 부모답글이 없는 모든 답글 페이징 조회")
    void _02_findAllByBoardIdIfParentIsNullWithPaging_succeed_test() {
        List<Comment> parentCommentList = commentRepository.findAllByBoardIdIfParentIsNullWithPaging(2L, PageRequest.of(0, 2))
                .stream()
                .collect(Collectors.toList());

        Comment parentComment1 = parentCommentList.get(0);
        Comment parentComment2 = parentCommentList.get(1);

        List<Comment> comment1Children = parentComment1.getChildren();
        List<Comment> comment2Children = parentComment2.getChildren();

        assertThat(parentCommentList.size()).isEqualTo(2);
        assertThat(comment1Children.size()).isEqualTo(2);
        assertThat(comment2Children.size()).isEqualTo(1);
    }

    /**
     * id 리스트를 매개변수로 넘겨서 in 절로 삭제할 경우 (delete from comment where id in (...))
     * h2 는 참조 무결성 제약조건 에러 발생
     * pg 는 DML 적용됨
     * 따라서, 해당 테스트 클래스의 프로파일을 test-pg 로 활성화
     */
    @Test
    @DisplayName("[성공 테스트] 답글 아이디 리스트로 게시글들 삭제")
    void _03_deleteWithIds_succeed_test() {
        System.out.println(commentRepository.findAll());

        //given
        commentRepository.deleteWithIds(List.of(13L, 14L, 15L, 16L, 17L, 18L));

        //when
        List<Comment> all = commentRepository.findAll();

        //then
        assertThat(all.size()).isEqualTo(0);
    }
}


