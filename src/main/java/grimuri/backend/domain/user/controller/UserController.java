package grimuri.backend.domain.user.controller;

import grimuri.backend.domain.user.dto.UserRequestDto;
import grimuri.backend.domain.user.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UserController {

    @Operation(
            summary = "로그인한 사용자의 FCM Token 정보 등록",
            description = "로그인한 뒤, Notification을 위해 사용자의 FCM Token을 서버에 등록한다.",
            tags = { "UserController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request, Header가 잘못되었음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 유효하지 않은 토큰임.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found, 사용자를 찾을 수 없음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<?> postLoginFCMToken(@RequestBody UserRequestDto.FCMTokenRequest tokenRequest);

    @Operation(
            summary = "사용자 정보 등록",
            description = "Firebase를 통해 인증 절차를 거치고 난 뒤, Authorization 헤더에 'Bearer {Token}'과 같은 형태로 " +
                    "토큰 정보를 담아 추가적으로 필요한 정보와 함께 사용자 정보 등록을 요청한다.",
            tags = { "UserController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created, 정상적으로 등록되었음.",
                content = @Content(schema = @Schema(implementation = UserResponseDto.AfterSignup.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request, Header가 잘못되었음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 유효하지 않은 토큰임.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<UserResponseDto.AfterSignup> signup(
            @Parameter(description = "Authorization Header", in = ParameterIn.HEADER, required = true)
            @RequestHeader("Authorization") String authorization,
                                                    @RequestBody UserRequestDto.Register registration);


    @Operation(
            summary = "사용자 정보 조회",
            description = "로그인된 사용자의 정보를 조회한다.",
            tags = { "UserController" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.UserInfo.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request, Header가 잘못되었음.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, 유효하지 않은 토큰임.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found, 등록되지 않은 사용자임.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserResponseDto.UserInfo> getUserInfo(
            @Parameter(description = "Authorization Header", in = ParameterIn.HEADER, required = true)
            @RequestHeader("Authorization") String authorization);
}
