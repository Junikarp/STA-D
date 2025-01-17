package com.klpc.stadspring.global.auth.controller;

import com.klpc.stadspring.domain.user.service.UserService;
import com.klpc.stadspring.domain.user.service.command.LogoutCommand;
import com.klpc.stadspring.global.auth.controller.request.AppLoginRequest;
import com.klpc.stadspring.global.auth.controller.request.WebJoinRequest;
import com.klpc.stadspring.global.auth.controller.request.WebLoginRequest;
import com.klpc.stadspring.global.auth.controller.response.LoginResult;
import com.klpc.stadspring.global.auth.jwt.AccessToken;
import com.klpc.stadspring.global.auth.jwt.AuthToken;
import com.klpc.stadspring.global.response.CommonResponseEntity;
import com.klpc.stadspring.global.response.SuccessCode;
import com.klpc.stadspring.util.Auth;
import com.klpc.stadspring.util.CookieProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.klpc.stadspring.global.response.CommonResponseEntity.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {

    private final CookieProvider cookieProvider;
    private final UserService userService;

    /**
     * 플러터 로그인
     * @param request
     *     String nickname;
     *     String name;
     *     String email;
     *     String profileImage;
     * @return body AT, header RT
     */
    @PostMapping("/applogin")
    @Operation(summary = "앱 로그인 (일반)", description = "로그인 후 토큰 생성")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AuthToken.class)))
    public ResponseEntity<?> appLogin(@RequestBody AppLoginRequest request, HttpServletResponse response) {
        log.info("AppLoginRequest: " + request);
        LoginResult result = userService.appLogin(request.toCommand());
        tokenInsert(response, result);
        return ResponseEntity.ok().build();
    }

    /**
     * 웹 로그인
     * @param request
     *     String nickname;
     *     String name;
     *     String email;
     *     String profileImage;
     * @return body AT, header RT
     */
    @PostMapping("/weblogin")
    @Operation(summary = "웹 로그인 (기업)", description = "로그인 후 토큰 생성")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AuthToken.class)))
    public ResponseEntity<?> webLogin(@RequestBody WebLoginRequest request, HttpServletResponse response) {
        log.info("WebLoginRequest: " + request);
        LoginResult result = userService.webLogin(request.toCommand());
        tokenInsert(response, result);
        return ResponseEntity.ok().build();
    }

    /**
     * 웹 회원가입
     * @param request
     *     String nickname;
     *     String name;
     *     String email;
     *     String profileImage;
     * @return body AT, header RT
     */
    @PostMapping("/webjoin")
    @Operation(summary = "웹 회원가입 (기업)", description = "회원가입 후 토큰 생성")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AuthToken.class)))
    public ResponseEntity<?> webJoin(@RequestBody WebJoinRequest request, HttpServletResponse response) {
        log.info("WebJoinRequest: " + request);
        LoginResult result= userService.JoinCompanyUser(request.toCommand());

        tokenInsert(response, result);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃이 성공적으로 진행되었습니다.")
    public ResponseEntity<?> logout(@RequestParam("userId") Long userId) {
        LogoutCommand command = LogoutCommand.builder()
                .userId(userId)
                .build();
        userService.logout(command);
        return ResponseEntity.ok().build();
    }

    @Auth
    @GetMapping("/test")
    @Operation(summary = "로그인 여부 확인 테스트", description = "로그인 여부 확인 테스트")
    public ResponseEntity<CommonResponseEntity> test() {
      return getResponseEntity(SuccessCode.OK, "테스트입니다.");
    }

    /**
     * 헤더에 토큰 삽입
     * @param response
     * @param result
     */
    private void tokenInsert(HttpServletResponse response, LoginResult result) {
        //토큰 get & cookie 생성
        AccessToken accessToken = result.getAccessToken();

        //헤더에 jwt 삽입
        Cookie cookie = cookieProvider.createRefreshTokenCookie(result.getRefreshToken());
        response.addCookie(cookie);
        response.setHeader("Authorization", "Bearer " + accessToken.getAccessToken());
    }
}
