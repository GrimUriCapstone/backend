package grimuri.backend.domain.image;

import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.diary.DiaryRepository;
import grimuri.backend.domain.image.dto.ImageRequestDto;
import grimuri.backend.domain.user.User;
import grimuri.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService {

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public void saveImageWithDiary(ImageRequestDto.Complete request) {
        Diary findDiary = diaryRepository.findById(request.getDiaryId()).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary가 존재하지 않습니다.");
        });

        User writerUser = userRepository.findById(request.getUserEmail()).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User가 존재하지 않습니다.");
        });

        String tagStr = request.getTags().stream()
                .map(tag -> tag.getEng() + ":" + tag.getKor())
                .collect(Collectors.joining(","));
        findDiary.saveTags(tagStr);
        findDiary.setImageCreated(true);

        request.getImages().forEach(url -> {
            Image eachImage = Image.builder()
                    .sourceUrl(url)
                    .diary(findDiary)
                    .build();

            imageRepository.save(eachImage);
        });
    }
}
