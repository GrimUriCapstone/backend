package grimuri.backend.domain.image.controller;

import grimuri.backend.domain.image.ImageService;
import grimuri.backend.domain.image.dto.ImageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageControllerImpl implements ImageController {

    private final ImageService imageService;

    @PostMapping("/generate")
    @Override
    public ResponseEntity<String> imageGenerateComplete(@RequestBody ImageRequestDto.Complete request) {
        log.debug("\tRequest Body: {}", request.toString());

        imageService.saveImageWithDiaryAndNotify(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }
}
