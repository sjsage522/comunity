package com.example.comunity.repository.board;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class BoardRepositoryCustomImplTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("사용자가 작성한 모든 게시글들 조회")
    void findAllWithUser() {
        //given
        User newUser = userRepository.join(User.createUser("user1", "user", "user", "1234", "user@test.com"));

        Category category = categoryRepository.findByName("게임");
        Board newBoard = boardRepository.upload(Board.createBoard(newUser, category, "user_title", "user_content"));

        //when
        List<Board> boards = boardRepository.findAllWithUser(newUser.getUserId());

        //then
        Assertions.assertThat(boards.size()).isEqualTo(1);
    }
}