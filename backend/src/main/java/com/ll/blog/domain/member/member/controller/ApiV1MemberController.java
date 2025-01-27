package com.ll.blog.domain.member.member.controller;

import com.ll.blog.domain.member.member.dto.MemberDto;
import com.ll.blog.domain.member.member.entity.Member;
import com.ll.blog.domain.member.member.service.MemberService;
import com.ll.blog.global.excepations.ServiceException;
import com.ll.blog.global.rq.Rq;
import com.ll.blog.global.rsData.RsData;
import com.ll.blog.standard.base.Empty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    record MemberJoinReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password,
            @NotBlank
            String nickname
    ) {
    }

    @PostMapping("/join")
    public RsData<MemberDto> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ) {
        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }

    record MemberLoginReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password
    ) {
    }

    record MemberLoginResBody(
            @NonNull
            MemberDto item,
            @NonNull
            String apiKey,
            @NonNull
            String accessToken
    ) {
    }

    @PostMapping("/login")
    public RsData<MemberLoginResBody> login(
            @RequestBody @Valid MemberLoginReqBody reqBody
    ) {
        Member member = memberService
                .findByUsername(reqBody.username)
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 사용자입니다."));

        if (!member.matchPassword(reqBody.password))
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("accessToken", accessToken);
        rq.setCookie("apiKey", member.getApiKey());

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getName()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }


    @DeleteMapping("/logout")
    public RsData<Empty> logout() {
        rq.deleteCookie("accessToken");
        rq.deleteCookie("apiKey");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }
}