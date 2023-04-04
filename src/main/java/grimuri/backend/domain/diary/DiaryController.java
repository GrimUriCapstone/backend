package grimuri.backend.domain.diary;

import grimuri.backend.domain.diary.dto.DiaryResponseDto;
import grimuri.backend.domain.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final ImageService imageService;
    private final DiaryService diaryService;

    /**
     * 후보 이미지 목록 조회
     * @param diaryId
     * @return List<DiaryResponseDto.CandidateImageUrl>
     */
    @GetMapping("/{diaryId}/images")
    public List<DiaryResponseDto.CandidateImageUrl> getCandidateImageList(@PathVariable Long diaryId) {

        // TODO: 인증로직 추가 후 diaryId가 사용자의 것인지 확인

        return diaryService.getCandidateImageList(diaryId);
    }

}
