package grimuri.backend.domain.diary.controller;

import grimuri.backend.domain.diary.DiaryService;
import grimuri.backend.domain.diary.dto.DiaryRequestDto;
import grimuri.backend.domain.diary.dto.DiaryResponseDto;
import grimuri.backend.domain.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/diary")
public class DiaryControllerImpl implements DiaryController {

    private final ImageService imageService;
    private final DiaryService diaryService;

    @PostMapping("")
    @Override
    public ResponseEntity<DiaryResponseDto.Create> createDiary(@RequestBody DiaryRequestDto.CreateRequest requestDto) {
        // TODO: 인증로직 추가 후 userId를 통해 userSeq 가져와서 사용
        Long userSeq = 1L;

        DiaryResponseDto.Create responseDto = diaryService.createDiary(userSeq, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 대표 이미지 Redirect
     *
     * @param diaryId 대표 이미지를 가져오려는 Diary의 Id
     * @return
     */
    @GetMapping("/{diaryId}/image")
    @Override
    public ResponseEntity<?> redirectToMainImage(@PathVariable Long diaryId) {
        // TODO: 인증로직 추가 후 userId를 통해 userSeq 가져와서 사용
        Long userSeq = 1L;

        String mainImageUrl = diaryService.getMainImageUrl(userSeq, diaryId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(mainImageUrl));

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).body(null);
    }


    /**
     * 로그인된 유저의 일기 목록을 페이징하여 조회함
     * @param pageable Request를 통해 입력된 Page 정보
     * @return Page of DiaryResponseDto.DiaryResponse
     */
    @GetMapping("")
    @Override
    public ResponseEntity<Page<DiaryResponseDto.DiaryResponse>> getDiaryResponsePage(Pageable pageable) {
        // TODO: 인증로직 추가 후 userId를 통해 userSeq 가져와서 사용
        Long userSeq = 1L;

        Page<DiaryResponseDto.DiaryResponse> diaryResponsePage = diaryService.getDiaryResponsePage(userSeq, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(diaryResponsePage);
    }

    /**
     * 로그인된 유저의 일기 목록 전체를 조회함
     * @return List of DiaryResponseDto.DiaryResponse
     */
    @GetMapping("/all")
    @Override
    public ResponseEntity<List<DiaryResponseDto.DiaryResponse>> getDiaryListAll() {

        // TODO: 인증로직 추가 후 userId를 통해 userSeq 가져와서 사용
        Long userSeq = 1L;

        List<DiaryResponseDto.DiaryResponse> diaryResponseList = diaryService.getDiaryListAll(userSeq);

        return ResponseEntity.status(HttpStatus.OK).body(diaryResponseList);
    }


    /**
     * 후보 이미지 목록 조회
     * @param diaryId Diary의 ID
     * @return List of DiaryResponseDto.CandidateImageUrl
     */
    @GetMapping("/{diaryId}/images")
    @Override
    public ResponseEntity<List<DiaryResponseDto.ImageUrl>> getCandidateImageList(@PathVariable Long diaryId) {

        // TODO: 인증로직 추가 후 diaryId가 사용자의 것인지 확인

        List<DiaryResponseDto.ImageUrl> imageUrlList = diaryService.getCandidateImageList(diaryId);

        return ResponseEntity.status(HttpStatus.OK).body(imageUrlList);
    }

}
