package com.example.comunity.service;

import com.example.comunity.domain.*;
import com.example.comunity.dto.board.BoardUpdateRequest;
import com.example.comunity.dto.board.BoardUploadRequest;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.CommentRepository;
import com.example.comunity.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@DisplayName("서비스 테스트 (board)")
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    FileUtils fileUtils;

    @Test
    @DisplayName("[성공 테스트] 모든 게시글 페이지리스트 조회")
    void findAll_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board1 = Board.of(user, codingCategory, "title1", "content1");
        Board board2 = Board.of(user, codingCategory, "title2", "content2");
        Board board3 = Board.of(user, codingCategory, "title3", "content3");
        int page = 0;

        //when
        when(boardRepository.findAllWithPaging(PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(List.of(board1, board2, board3), PageRequest.of(page, 10), 3));

        List<Board> boardList = boardService.findAll(page);

        //then
        assertThat(boardList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 업로드")
    void upload_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");

        BoardUploadRequest boardUploadRequest = BoardUploadRequest.of(board.getTitle(), board.getContent(), board.getCategory().getCategoryName().getEn());

        //when
        when(categoryRepository.findByCategoryName(codingCategory.getCategoryName()))
                .thenReturn(Optional.of(codingCategory));
        when(boardRepository.save(any()))
                .thenReturn(board);
        when(fileUtils.uploadFiles(null, board))
                .thenReturn(Collections.emptyList());
        Board newBoard = boardService.upload(boardUploadRequest, user, null);

        //then
        assertThat(newBoard.getUser().getUserId()).isEqualTo("testId");
        assertThat(newBoard.getCategory().getCategoryName().getEn()).isEqualTo("CODING");
        assertThat(newBoard.getTitle()).isEqualTo("title1");
    }

    @Test
    @DisplayName("[성공 테스트] 카테고리로 모든 게시글리스트 조회")
    void findAllWithCategory_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board1 = Board.of(user, codingCategory, "title1", "content1");
        Board board2 = Board.of(user, codingCategory, "title2", "content2");
        Board board3 = Board.of(user, codingCategory, "title3", "content3");

        Category gameCategory = Category.from("game");
        Board board4 = Board.of(user, gameCategory, "title4", "content4");

        int page = 0;

        //when
        when(boardRepository.findAllByCategoryNameWithPaging(CategoryName.CODING, PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(List.of(board1, board2, board3), PageRequest.of(page, 10), 3));

        List<Board> boardList = boardService.findAllWithCategory("coding", page);

        //then
        assertThat(boardList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 아이디와 카테고리로 게시글 단건 조회")
    void findByIdWithCategory_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        //when
        when(boardRepository.findByBoardIdAndCategoryName(board.getId(), codingCategory.getCategoryName()))
                .thenReturn(Optional.of(board));

        Board findBoard = boardService.findByIdWithCategory(board.getId(), codingCategory.getCategoryName().getEn());

        //then
        assertThat(findBoard.getTitle()).isEqualTo("title1");
        assertThat(findBoard.getCategory().getCategoryName().getEn()).isEqualTo("CODING");
        assertThat(findBoard.getUser().getUserId()).isEqualTo("testId");
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 아이디로 게시글 단건 조회")
    void findById_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        //when
        when(boardRepository.findById(board.getId()))
                .thenReturn(Optional.of(board));

        Board findBoard = boardService.findById(board.getId());

        //then
        assertThat(findBoard.getTitle()).isEqualTo("title1");
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 삭제")
    void delete_succeed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        Comment comment1 = Comment.of(user, board, "content1");
        Comment comment2 = Comment.of(user, board, "comment2");
        ReflectionTestUtils.setField(comment1, "id", 1L);
        ReflectionTestUtils.setField(comment2, "id", 2L);

        //when
        when(boardRepository.findByBoardIdAndCategoryName(board.getId(), codingCategory.getCategoryName()))
                .thenReturn(Optional.of(board));
        when(commentRepository.findAllByBoardId(board.getId()))
                .thenReturn(List.of(comment1, comment2));

        boardService.delete(1L, codingCategory.getCategoryName().getEn(), user);

        //then
        then(commentRepository).should().deleteWithIds(Arrays.asList(1L, 2L));
    }

    @Test
    @DisplayName("[실패 테스트] 게시글 삭제 - 다른 사용자의 게시글은 삭제할 수 없음")
    void delete_failed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        User otherUser = User.of("otherId", "person", "person", "1234", "person@gmail.com");

        //when
        when(boardRepository.findByBoardIdAndCategoryName(board.getId(), codingCategory.getCategoryName()))
                .thenReturn(Optional.of(board));

        //then
        Assertions.assertThrows(NoMatchUserInfoException.class,
                () -> boardService.delete(1L, codingCategory.getCategoryName().getEn(), otherUser)
        );
        then(commentRepository).should(times(0)).findAllByBoardId(1L);
        then(commentRepository).should(times(0)).deleteWithIds(Collections.emptyList());
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 수정 - 카테고리 변경 하기")
    void update_succeed_test1() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        CategoryName changeCategoryName = CategoryName.GAME;

        BoardUpdateRequest request = BoardUpdateRequest.builder()
                .categoryName(changeCategoryName.getEn())
                .build();

        //when
        when(boardRepository.findByBoardIdAndCategoryName(1L, codingCategory.getCategoryName()))
                .thenReturn(Optional.of(board));
        when(categoryRepository.findByCategoryName(changeCategoryName))
                .thenReturn(Optional.of(Category.from(changeCategoryName.getEn())));

        Board changedBoard = boardService.update(1L, codingCategory.getCategoryName().getEn(), request, user);

        //then
        assertThat(changedBoard.getCategory().getCategoryName().getEn()).isEqualTo("GAME");
    }

    @Test
    @DisplayName("[성공 테스트] 게시글 수정 - 제목, 내용, 카테고리 변경하기")
    void update_succeed_test2() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        CategoryName changeCategoryName = CategoryName.GAME;

        BoardUpdateRequest request = BoardUpdateRequest.builder()
                .title("UPDATE TITLE")
                .content("UPDATE CONTENT")
                .categoryName(changeCategoryName.getEn())
                .build();

        //when
        when(boardRepository.findByBoardIdAndCategoryName(1L, codingCategory.getCategoryName()))
                .thenReturn(Optional.of(board));
        when(categoryRepository.findByCategoryName(changeCategoryName))
                .thenReturn(Optional.of(Category.from(changeCategoryName.getEn())));

        Board changedBoard = boardService.update(1L, codingCategory.getCategoryName().getEn(), request, user);

        //then
        assertThat(changedBoard.getTitle()).isEqualTo("UPDATE TITLE");
        assertThat(changedBoard.getContent()).isEqualTo("UPDATE CONTENT");
        assertThat(changedBoard.getCategory().getCategoryName().getEn()).isEqualTo("GAME");
    }

    @Test
    @DisplayName("[실패 테스트] 게시글 수정 - 다른 사람의 게시글은 수정할 수 없음")
    void update_failed_test() {
        //given
        User user = User.of("testId", "tester", "testNickname", "1234", "tester@gmail.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "title1", "content1");
        ReflectionTestUtils.setField(board, "id", 1L);

        User otherUser = User.of("otherId", "person", "person", "1234", "person@gmail.com");

        CategoryName changeCategoryName = CategoryName.GAME;

        BoardUpdateRequest request = BoardUpdateRequest.builder()
                .title("UPDATE TITLE")
                .content("UPDATE CONTENT")
                .categoryName(changeCategoryName.getEn())
                .build();

        //when
        when(boardRepository.findByBoardIdAndCategoryName(1L, codingCategory.getCategoryName()))
                .thenReturn(Optional.of(board));

        //then
        Assertions.assertThrows(NoMatchUserInfoException.class,
                () -> boardService.update(1L, codingCategory.getCategoryName().getEn(), request, otherUser)
        );
        assertThat(board.getTitle()).isEqualTo("title1");
        assertThat(board.getContent()).isEqualTo("content1");
        assertThat(board.getCategory().getCategoryName().getEn()).isEqualTo("CODING");
    }
}