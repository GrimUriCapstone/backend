package grimuri.backend.domain.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.diary.DiaryRepository;
import grimuri.backend.domain.diary.dto.DiaryMessageDto;
import grimuri.backend.domain.diary.dto.DiaryRequestDto;
import grimuri.backend.domain.diary.dto.DiaryResponseDto;
import grimuri.backend.domain.image.Image;
import grimuri.backend.domain.image.ImageRepository;
import grimuri.backend.domain.user.User;
import grimuri.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiaryService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;

    private final SqsSenderService senderService;

    private final static String BUCKET_NAME = "grim-uri";
    private final static String BUCKET_PATH = "save/";

    public Page<DiaryResponseDto.Recent> getRecentDiaries(Pageable pageable) {
        Page<Diary> findDiaryPage = diaryRepository.findByOpenAndSelected(true, true, pageable);

        return findDiaryPage.map(DiaryResponseDto.Recent::of);
    }

    /**
     * 일기의 제목과 내용을 수정한다. 이미지는 재생성되지 않는다.
     * @param email 사용자의 email (PK)
     * @param diaryId 수정하려는 일기의 diaryId
     * @param requestDto DiaryRequestDto.ModifyRequest 수정 제목과 수정 내용
     * @return DiaryResponseDto.DiaryResponse 수정된 일기의 내용
     */
    public DiaryResponseDto.DiaryResponse modifyDiary(String email, Long diaryId, DiaryRequestDto.ModifyRequest requestDto) {
        Diary findDiary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 diaryId의 Diary가 존재하지 않습니다.");
        });

        if (!findDiary.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User의 Diary가 아닙니다.");
        }

        findDiary.setTitle(requestDto.getTitle());
        findDiary.setOriginalContent(requestDto.getContent());
        findDiary.setOpen(requestDto.getOpen());

        if (findDiary.getSelected()) {
            return DiaryResponseDto.DiaryResponse.imageSelectedOf(findDiary);
        } else {
            return DiaryResponseDto.DiaryResponse.imageUnSelectedOf(findDiary);
        }
    }

    /**
     * 사용자의 email 주소와 일기의 diaryId를 통해 사용자의 단건 일기를 삭제한다.
     * @param email 사용자의 email (PK)
     * @param diaryId 삭제하려는 일기의 diaryId
     */
    public void deleteDiary(String email, Long diaryId) {
        Diary findDiary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 diaryId의 Diary가 존재하지 않습니다.");
        });

        if (!findDiary.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User의 Diary가 아닙니다.");
        }

        diaryRepository.delete(findDiary);
    }
    
    /**
     * 일기의 후보 이미지들 중 하나를 대표 이미지로 선택한다. diaryId로 일기와 일기의 후보 이미지 목록을 조회한다.
     * 이후 imageId에 해당하는 이미지만 남기고 나머지 후보 이미지들은 제거한다.
     * 일기의 selected는 true로 바꾼다.
     * @param email 사용자의 email (PK)
     * @param diaryId 대표 이미지를 선택하려는 일기의 diaryId
     * @param imageId 대표 이미지로 선택하려는 이미지의 imageId
     */
    public void selectDiaryImage(String email, Long diaryId, Long imageId) {
        // diaryId로 Diary 조회
        Diary findDiary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            log.debug("\t해당 diaryId의 Diary가 존재하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 diaryId의 Diary가 존재하지 않습니다.");
        });

        if (!findDiary.getUser().getEmail().equals(email)) {
            log.debug("\tUser의 Diary가 아닙니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User의 Diary가 아닙니다.");
        }

        // 아직 후보 이미지가 생성되지 않은 경우 exception
        if (!findDiary.getImageCreated()) {
            log.debug("\t아직 후보 이미지들이 생성되지 않았습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 후보 이미지들이 생성되지 않았습니다.");
        }

        List<Image> imageList = findDiary.getImageList();

        // imageId 존재하는지 확인
        boolean imageExists = imageList.stream().anyMatch(image -> image.getId().equals(imageId));
        if (!imageExists) {
            log.debug("\t해당 이미지가 일기에 존재하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 이미지가 일기에 존재하지 않습니다");
        }

        Storage storage = StorageOptions.newBuilder().build().getService();
        List<BlobId> blobIds = new ArrayList<>();

        // imageList에서 imageId 빼고 나머지 후보들 제거
        for (Image image : imageList) {
            if (!image.getId().equals(imageId)) {

                BlobId blobId = BlobId.of(BUCKET_NAME, BUCKET_PATH + image.getFilename());
                blobIds.add(blobId);

                imageRepository.delete(image);
            }
        }

        List<Boolean> deleteResultList = storage.delete(blobIds);
        log.debug("delete Storage Image Results: {}", deleteResultList);

        findDiary.setSelected(true);
    }

    /**
     * 사용자의 email 주소와 일기의 diaryId를 통해 사용자의 단건 diary를 조회한 뒤 DiaryResponseDto.DiaryResponse를 반환한다.
     * @param email 사용자의 email (PK)
     * @param diaryId 조회하려는 일기의 diaryId
     * @return DiaryResponseDto.DiaryResponse
     */
    public DiaryResponseDto.DiaryResponse getDiary(String email, Long diaryId) {
        // diaryId로 Diary 조회
        Diary findDiary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            log.debug("\t해당 diaryId의 Diary가 존재하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 diaryId의 Diary가 존재하지 않습니다.");
        });

        Boolean isOpen = findDiary.getOpen();
        Boolean isOwned = findDiary.getUser().getEmail().equals(email);

        if (isOwned || isOpen) {
            // 공개이거나 자신의 것인 경우

            if (findDiary.getSelected()) {
                return DiaryResponseDto.DiaryResponse.imageSelectedOf(findDiary);
            } else {
                return DiaryResponseDto.DiaryResponse.imageUnSelectedOf(findDiary);
            }
        } else {
            // 공개도 아니고 자신의 것도 아닌 경우
            log.debug("\tUser의 Diary가 아닙니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User의 Diary가 아닙니다.");
        }
    }

    /**
     * user의 email(ID)과 DiaryRequestDto.Create를 이용해 Diary를 생성하고, 생성된 Diary의 diaryId와
     * Diary를 이용해 DiaryResponseDto.Create 반환. 메시지 큐에 이미지 생성 요청 정보를 저장한다.
     * @param email user의 id
     * @param requestDto Diary 생성을 요청할 때 Body에 있는 값 (제목과 내용)
     * @return DiaryResponseDto.Create
     * @throws ResponseStatusException
     */
    public DiaryResponseDto.Create createDiary(String email, DiaryRequestDto.CreateRequest requestDto) {
        User writer = userRepository.findById(email).orElseThrow(() -> {
            log.debug("\tUser가 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user가 없습니다.");
        });

        Diary newDiary = Diary.builder()
                .title(requestDto.getTitle())
                .originalContent(requestDto.getContent())
                .selected(false)
                .imageCreated(false)
                .user(writer)
                .open(requestDto.getOpen())
                .build();
        diaryRepository.save(newDiary);

        try {
            log.debug("\tSender Service SendMessage 호출.");
            senderService.sendMessage(DiaryMessageDto.Generate.of(newDiary, requestDto.getThemeId(), requestDto.getStyleId()));
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return DiaryResponseDto.Create.of(newDiary);
    }

    /**
     * email(ID)과 diaryId를 이용해 해당 diaryId의 대표 이미지 URL을 반환함.
     * @param email User의 id
     * @param diaryId Diary의 Id
     * @return String
     */
    public String getMainImageUrl(String email, Long diaryId) {
        //       diaryId로 Diary 조회
        Diary diary = diaryRepository.findById(diaryId)
                        .orElseThrow(() -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "diaryId에 해당하는 Diary가 없습니다.");
                        });

        //        해당 diaryId가 user의 것인지 확인
        boolean userCheck = diary.getUser().getEmail().equals(email);
        if (!userCheck) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user의 Diary가 아닙니다.");
        }

        //        diaryId가 selected인지 아닌지 확인 (expected true)
        if (!diary.getSelected()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 대표 이미지가 선택되지 않았습니다.");
        }

        //        diaryId가 갖는 대표 이미지 URL 조회
        return diary.getImageList().get(0).getSourceUrl();
    }

    /**
     *
     * @param email User의 id
     * @param pageable Controller를 통해 입력된 Page 정보
     * @return Page of DiaryResponseDto.DiaryResponse
     */
    public Page<DiaryResponseDto.DiaryResponse> getDiaryResponsePage(String email, Pageable pageable) {
        Page<Diary> diaryPage = diaryRepository.findByUser_Email(email, pageable);

        return diaryPage
                .map(diary -> diary.getSelected() ?
                        DiaryResponseDto.DiaryResponse.imageSelectedOf(diary)
                        : DiaryResponseDto.DiaryResponse.imageUnSelectedOf(diary));
    }

    /**
     *
     * @param email User의 Id
     * @return List of DiaryResponseDto.DiaryResponse
     */
    public List<DiaryResponseDto.DiaryResponse> getDiaryListAll(String email) {

        // email에 해당하는 User가 갖는 Diary들의 List
        List<Diary> diaryList = diaryRepository.findByUser_Email(email);

        // Diary의 selected에 따라서 매핑을 다르게 함.
        return diaryList.stream()
                .map(diary -> diary.getSelected() ?
                        DiaryResponseDto.DiaryResponse.imageSelectedOf(diary)
                        : DiaryResponseDto.DiaryResponse.imageUnSelectedOf(diary))
                .collect(Collectors.toList());
    }

    /**
     * 대표 이미지를 선택하지 않은 경우 diaryId의 이미지 URL 목록을 반환한다.
     *
     * @param email
     * @param diaryId diary의 ID
     * @return List of DiaryResponseDto.ImageUrl
     */
    public List<DiaryResponseDto.ImageUrl> getCandidateImageList(String email, Long diaryId) {

        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 diaryId의 Diary가 존재하지 않습니다.");
        });

        if (!diary.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User의 Diary가 아닙니다.");
        }

        // 아직 후보 이미지가 생성되지 않은 경우 exception
        if (!diary.getImageCreated()) {
            log.debug("\t아직 후보 이미지들이 생성되지 않았습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 후보 이미지들이 생성되지 않았습니다.");
        }

        // 해당 diaryId가 대표 이미지를 선택했는지 여부 조회
        Boolean selectedImage = diaryRepository.existsByIdAndSelectedTrue(diaryId);
        if (selectedImage) {
            // 이미 선택 완료한 경우 throw Exception
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 이미지 선택된 diaryId 입니다.");
        }

        // diaryId의 Image 들을 CandidateImageUrl List로 변환하여 리턴
        return imageRepository.findByDiaryId(diaryId)
                .stream()
                .map(DiaryResponseDto.ImageUrl::of)
                .collect(Collectors.toList());
    }
}
