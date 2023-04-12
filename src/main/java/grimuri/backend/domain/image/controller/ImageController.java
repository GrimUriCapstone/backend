package grimuri.backend.domain.image.controller;

import grimuri.backend.domain.image.dto.ImageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface ImageController {

    @Operation(
            summary = "후보 이미지 생성 후 이미지 정보 전송",
            description = "이미지 생성 서버에서 후보 이미지들을 생성하고 S3 저장소에 업로드한 뒤, " +
                    "해당 이미지들의 S3 접근 URL을 다른 정보들과 함께 웹 서버에 전송한다.",
            tags = { "ImageController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created, 정상적으로 저장되었음.",
                    content = @Content(schema = @Schema(implementation = String.class, example = "Success"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found, 사용자가 존재하지 않거나 Diary가 존재하지 않음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<String> imageGenerateComplete(@RequestBody ImageRequestDto.Complete request);
}
