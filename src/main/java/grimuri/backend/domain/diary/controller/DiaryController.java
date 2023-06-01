package grimuri.backend.domain.diary.controller;

import grimuri.backend.domain.diary.dto.DiaryRequestDto;
import grimuri.backend.domain.diary.dto.DiaryResponseDto;
import grimuri.backend.global.PageableAsParameter;
import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

public interface DiaryController {

    @Operation(
            summary = "이미지는 생성되었으나 대표 이미지를 선택하지 않은 일기 개수 조회",
            description = "이미지는 생성되었으나 대표 이미지를 선택하지 않은 일기 개수 조회",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Long> getCount();

    @Operation(
            summary = "공개 및 대표이미지 선택 완료 일기 페이지 조회",
            description = "페이지의 항목 개수, 페이지 인덱스, ASC/DESC를 지정하여 일기 항목 페이지를 조회한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok. content의 내용은 Recent 스키마를 참조.",
                    content = @Content(schema = @Schema(implementation = Page.class, subTypes = DiaryResponseDto.Recent.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PageableAsParameter
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Page<DiaryResponseDto.Recent>> getRecentDiaries(
            @Parameter(schema = @Schema(hidden = true)) Pageable pageable);

    @Operation(
            summary = "일기 수정",
            description = "일기의 제목과 내용을 수정한다. 생성된 기존 이미지는 재생성하지 않는다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok.",
                    content = @Content(schema = @Schema(implementation = DiaryResponseDto.DiaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Diary가 사용자의 것이 아님.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found. diaryId에 해당하는 일기 존재하지 않음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<DiaryResponseDto.DiaryResponse> modifyDiary(
            @Parameter(description = SchemaDescriptionUtils.Diary.diaryId, in = ParameterIn.PATH)
            @PathVariable Long diaryId, @RequestBody DiaryRequestDto.ModifyRequest request);

    @Operation(
            summary = "일기 단건 삭제",
            description = "diaryId를 입력받아 단건 일기를 삭제한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Diary가 사용자의 것이 아님.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found. diaryId에 해당하는 일기 존재하지 않음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<String> deleteDiary(
            @Parameter(description = SchemaDescriptionUtils.Diary.diaryId, in = ParameterIn.PATH)
            @PathVariable Long diaryId);

    @Operation(
            summary = "대표 이미지 선택",
            description = "diaryId와 imageId를 입력받아, 일기의 후보 이미지 중에 하나를 대표 이미지로 선택한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Diary가 사용자의 것이 아님.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found. diaryId에 해당하는 일기 존재하지 않거나, 이미지가 일기에 존재하지 않음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> selectDiaryImage(
            @Parameter(description = SchemaDescriptionUtils.Diary.diaryId, in = ParameterIn.PATH)
            @PathVariable Long diaryId,
            @Parameter(description = SchemaDescriptionUtils.Image.imageId, in = ParameterIn.PATH)
            @PathVariable Long imageId
    );

    @Operation(
            summary = "개별 일기 조회",
            description = "diaryId를 입력받아 개별 일기를 조회한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok.",
                    content = @Content(schema = @Schema(implementation = DiaryResponseDto.DiaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Diary가 사용자의 것이 아님.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found. diaryId에 해당하는 일기 존재하지 않음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<DiaryResponseDto.DiaryResponse> getDiary(
            @Parameter(description = SchemaDescriptionUtils.Diary.diaryId, in = ParameterIn.PATH)
            @PathVariable Long diaryId);

    @Operation(
            summary = "일기 생성",
            description = "제목(title)과 일기 내용(content)를 입력받아, Diary를 생성하고 저장한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created, 정상적으로 저장되었음.",
                content = @Content(schema = @Schema(implementation = DiaryResponseDto.Create.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found, 존재하지 않는 사용자임.",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<DiaryResponseDto.Create> createDiary(@RequestBody @Valid DiaryRequestDto.CreateRequest requestDto);

    @Operation(
            summary = "일기 대표 이미지 액세스",
            description = "diaryId에 해당하는 일기의 대표 이미지 URL로 redirect 한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "301", description = "Moved Permanently, 대표 이미지 URL로 정상 redirect 됨",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Bad Request, 사용자의 Diary가 아니거나, 아직 대표 이미지가 선택되지 않음",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found, 존재하지 않는 사용자임.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    ResponseEntity<?> redirectToMainImage(
            @Parameter(description = SchemaDescriptionUtils.Diary.diaryId, in = ParameterIn.PATH) @PathVariable Long diaryId);

    @Operation(
            summary = "일기 목록 페이지 조회",
            description = "페이지의 항목 개수, 페이지 인덱스, ASC/DESC를 지정하여 일기 항목 페이지를 조회한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok. content의 내용은 DiaryResponse 스키마를 참조.",
                    content = @Content(schema = @Schema(implementation = Page.class, subTypes = DiaryResponseDto.DiaryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PageableAsParameter
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Page<DiaryResponseDto.DiaryResponse>> getDiaryResponsePage(
            @Parameter(schema = @Schema(hidden = true)) Pageable pageable);

    @Operation(
            summary = "일기 목록 전체 조회",
            description = "페이징 없이 일기 목록 전체를 조회한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DiaryResponseDto.DiaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<DiaryResponseDto.DiaryResponse>> getDiaryListAll();

    @Operation(
            summary = "일기 후보 이미지 목록 조회",
            description = "일기의 후보 이미지 목록을 조회한다.",
            tags = { "DiaryController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DiaryResponseDto.ImageUrl.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request, 이미 대표 이미지가 선택된 Diary임",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 권한 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<DiaryResponseDto.ImageUrl>> getCandidateImageList(
            @Parameter(description = SchemaDescriptionUtils.Diary.diaryId, in = ParameterIn.PATH) @PathVariable Long diaryId);
}
