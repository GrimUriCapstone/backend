package grimuri.backend.domain.fcm;

import grimuri.backend.domain.diary.dto.DiaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface FCMTokenController {

    @Operation(
            summary = "Notification 테스트",
            description = "로그인된 사용자에게 Notification을 테스트한다. Diary는 존재하지 않아도 된다.",
            tags = { "FCMTokenController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> testNotification(@RequestParam Long diaryId, @RequestParam String diaryTitle);
}
