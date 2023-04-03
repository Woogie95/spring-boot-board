package com.practice.springbootboard.service;

import com.practice.springbootboard.dto.BoardDTO;
import com.practice.springbootboard.entity.BoardEntity;
import com.practice.springbootboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        // 파일 첨부 여부에 따라서 로직을 분리해야 된다.

        if (boardDTO.getBoardFile().isEmpty()) { // 파일이 없는 경우
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else { // 파일이 있는 경우
            /* 1. DTO 에 담긴 파일을 꺼냄
               2. 파일의 이름 가져옴
               3. 서버 저장용 이름을 만듦
               // 내사진.jpg => 839798375892_내사진.jpg
               4. 저장 경로 설정
               5. 해당 경로에 파일 저장
               6. board_table 에 해당 데이터 save 처리
               7. board_file_table 에 해당 데이터 save 처리
            */
            MultipartFile boardFile = boardDTO.getBoardFile(); // 1
            String originalFilename = boardFile.getOriginalFilename(); // 2
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3
            String savePath = "/Users/sungwook/image/" + storedFileName; // 4 C:/springboot_img/9802398403948_내사진.jpg
            boardFile.transferTo(new File(savePath)); // 5


        }

    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity)); // Entity 의 객체를 DTO 로 변환하고 DTO 리스트에 저장하기
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            return BoardDTO.toBoardDTO(boardEntity);
        } else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        // 실제 사용자가 요청한 값으로 부터 1 을 뺀 값으로 시작한다. 왜냐하면 Page 는 0 부터 시작.
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; // 한 페이지에 보여줄 글 개수
        // 한 페이지 당 3개씩 게시물을 보여주고 정렬 기준은 id 값을 기준으로 내림차순이다.
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 목록 : id, writer, title, hits, createdTime
        return boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(),
                board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));
    }
}
