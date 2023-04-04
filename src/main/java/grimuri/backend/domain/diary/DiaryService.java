package grimuri.backend.domain.diary;

import grimuri.backend.domain.diary.dto.DiaryResponseDto;
import grimuri.backend.domain.image.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;

    public List<DiaryResponseDto.CandidateImageUrl> getCandidateImageList(Long diaryId) {

        // 해당 diaryId가 대표 이미지를 선택했는지 여부 조회
        Boolean selectedImage = diaryRepository.existsByIdAndSelectedTrue(diaryId);
        if (selectedImage) {
            // 이미 선택 완료한 경우 throw Exception
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 이미지 선택된 diaryId 입니다.");
        }

        // diaryId의 Image 들을 CandidateImageUrl List로 변환하여 리턴
        return imageRepository.findByDiaryId(diaryId)
                .stream()
                .map(DiaryResponseDto.CandidateImageUrl::of)
                .collect(Collectors.toList());
    }
}
