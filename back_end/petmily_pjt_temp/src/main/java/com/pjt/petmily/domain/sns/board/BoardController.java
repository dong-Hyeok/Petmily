package com.pjt.petmily.domain.sns.board;

import com.pjt.petmily.domain.pet.dto.PetInfoEditDto;
import com.pjt.petmily.domain.sns.board.dto.BoardRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/board/save")
    @Operation(summary = "게시글 작성", description = "SNS 게시글 작성&저장")
    public ResponseEntity<String> PetInfoSave(@RequestPart BoardRequestDto boardRequestDto,
                                              @RequestPart(value="file") List<MultipartFile> boardImgFiles) throws Exception {

        boardService.boardSave(boardRequestDto, boardImgFiles);

        return new ResponseEntity<>("게시글 저장 성공", HttpStatus.OK);
    }

    @PutMapping("/board/{boardId}")
    @Operation(summary = "반려동물 정보 수정", description = "반려동물 정보 수정")
    public ResponseEntity<String> PetInfoSave(@PathVariable Long boardId,
                                              @RequestPart BoardRequestDto boardRequestDto,
                                              @RequestPart(value="file") List<MultipartFile> boardImgFiles) throws Exception {
        boardService.boardUpdate(boardId, boardRequestDto, boardImgFiles);

        return new ResponseEntity<>("반려동물 정보 수정 성공", HttpStatus.OK);
    }
}
