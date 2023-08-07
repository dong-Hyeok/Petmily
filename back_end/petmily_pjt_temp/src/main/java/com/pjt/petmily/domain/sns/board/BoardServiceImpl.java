package com.pjt.petmily.domain.sns.board;

import com.pjt.petmily.domain.pet.PetException;
import com.pjt.petmily.domain.sns.board.dto.BoardRequestDto;
import com.pjt.petmily.domain.sns.board.dto.ResponseBoardAllDto;
import com.pjt.petmily.domain.sns.board.hashtag.HashTag;
import com.pjt.petmily.domain.sns.board.hashtag.HashTagRepository;
import com.pjt.petmily.domain.sns.board.hashtag.HashTagRequestDto;
import com.pjt.petmily.domain.sns.board.photo.Photo;
import com.pjt.petmily.domain.sns.board.photo.PhotoRepository;
import com.pjt.petmily.domain.user.User;
import com.pjt.petmily.domain.user.repository.UserRepository;
import com.pjt.petmily.global.awss3.service.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private HashTagRepository hashTagRepository;

    private final com.pjt.petmily.global.awss3.service.S3Uploader s3Uploader;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository,
                            PhotoRepository photoRepository,
                            HashTagRepository hashTagRepository,
                            S3Uploader s3Uploader) {
        this.boardRepository = boardRepository;
        this.photoRepository = photoRepository;
        this.hashTagRepository = hashTagRepository;
        this.s3Uploader = s3Uploader;
    }

    @Override
    public void boardSave(BoardRequestDto boardRequestDto, List<MultipartFile> boardImgFiles, HashTagRequestDto hashTagRequestDto) throws Exception {
        User user = userRepository.findByUserEmail(boardRequestDto.getUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다"));

        LocalDateTime currentTime = LocalDateTime.now();

        Board board = Board.builder()
                .user(user)
                .boardContent(boardRequestDto.getBoardContent())
                .boardUploadTime(currentTime)
                .build();

        Board savedBoard = boardRepository.save(board);

        if (boardImgFiles != null && !boardImgFiles.isEmpty()) {
            for (MultipartFile imageFile : boardImgFiles) {
                String BoardImg = imageFile  == null? null : s3Uploader.uploadFile(imageFile, "sns");
                Photo photo = new Photo();
                photo.setPhotoUrl(BoardImg);
                photo.setBoard(savedBoard);
                photoRepository.save(photo);
            }
        }
        // 해시태그 저장 코드
        if (hashTagRequestDto != null && hashTagRequestDto.getHashTagNames() != null) {
            for (String tag : hashTagRequestDto.getHashTagNames()) {
                HashTag hashTag = new HashTag();
                hashTag.setHashTagName(tag);
                hashTag.setBoard(savedBoard);
                hashTagRepository.save(hashTag);
            }
        }
    }

    @Override
    public void boardUpdate(Long boardId, BoardRequestDto boardRequestDto, List<MultipartFile> boardImgFiles, HashTagRequestDto hashTagRequestDto) throws Exception{
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new Exception("게시글" +boardId + "정보가 없습니다."));

        board.setBoardContent(boardRequestDto.getBoardContent());

        board.getPhotoList().clear();

        // 새로운 사진 업로드 및 저장
        if (boardImgFiles != null && !boardImgFiles.isEmpty()) {
            for (MultipartFile file : boardImgFiles) {
                String url = s3Uploader.uploadFile(file, "board");
                Photo photo = new Photo();
                photo.setPhotoUrl(url);
                photo.setBoard(board);
                board.getPhotoList().add(photo);
                photoRepository.save(photo);
            }
        }

        // 기존 해시태그 삭제
        List<HashTag> oldHashTags = hashTagRepository.findByBoard(board);
        hashTagRepository.deleteAll(oldHashTags);

        if (hashTagRequestDto != null && hashTagRequestDto.getHashTagNames() != null) {
            for (String tag : hashTagRequestDto.getHashTagNames()) {
                HashTag hashTag = new HashTag();
                hashTag.setHashTagName(tag);
                hashTag.setBoard(board);
                hashTagRepository.save(hashTag);
            }
        }

        boardRepository.save(board);
    }


    @Override
    public void boardDelete(Long boardId){
        Optional<Board> boardOptional = boardRepository.findByBoardId(boardId);
        if (boardOptional.isPresent()) {
            try {
                boardRepository.delete(boardOptional.get());
            } catch (Exception e) {
                throw new PetException.PetDeletionException("게시글 삭제 실패 " + boardId);
            }
        } else {
            throw new PetException.PetNotFoundException("게시글이 존재하지 않음" + boardId);
        }

    }

    @Override
    @Transactional
    public List<ResponseBoardAllDto> getAllBoard(String currentUserEmail){
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(board -> ResponseBoardAllDto.fromBoardEntity(board, currentUserEmail))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseBoardAllDto getOneBoard(Long boardId, String currentUserEmail){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException.BoardNotFoundException("게시글" +boardId + "정보가 없습니다."));
        return ResponseBoardAllDto.fromBoardEntity(board, currentUserEmail);
    }

}
