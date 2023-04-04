package com.practice.springbootboard.dto;

import com.practice.springbootboard.entity.BoardEntity;
import com.practice.springbootboard.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private long boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private List<MultipartFile> boardFile; // save.html -> Controller 로 넘어 올 때 file 을 담는 용도
    private List<String> originalFileName; // 원본 파일 이름
    private List<String> storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부 (첨부 : 1, 미첨부 : 0)

    public BoardDTO(Long id, String boardWriter, String boardTitle, long boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    // Entity -> DTO
    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());

        if (boardEntity.getFileAttached() == 0) {
            boardDTO.setFileAttached(boardDTO.getFileAttached()); // 0
        } else {
            List<String> originalFileNames = new ArrayList<>();
            List<String> storedFileNames = new ArrayList<>();
            boardDTO.setFileAttached(boardDTO.getFileAttached()); // 1
            // 추가로 파일 이름을 가져가야 함.
            // 파일 이름을 가져가야 함.
            // originalFileName, storedFileName : board_file_table(BoardFileEntity)
            // join
            // select * from board_table b, board_file_table bf where b.id=bf.board_id
            // and where b.id=?
            for (BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()) {
                originalFileNames.add(boardFileEntity.getOriginalFileName());
                storedFileNames.add(boardFileEntity.getStoredFileName());
            }
            boardDTO.setOriginalFileName(originalFileNames);
            boardDTO.setStoredFileName(storedFileNames);
        }
        return boardDTO;
    }

}
