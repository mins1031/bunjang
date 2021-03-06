package com.min.bunjang.member.controller;

import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.following.exception.NotExistFollowingException;
import com.min.bunjang.join.confirmtoken.exception.WrongConfirmEmailToken;
import com.min.bunjang.member.dto.MemberBirthDayUpdateRequest;
import com.min.bunjang.member.dto.MemberGenderUpdateRequest;
import com.min.bunjang.member.dto.MemberPhoneUpdateRequest;
import com.min.bunjang.member.exception.NotExistMemberException;
import com.min.bunjang.member.service.MemberService;
import com.min.bunjang.security.MemberAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    @PatchMapping(MemberControllerPath.MEMBER_CHANGE_GENDER)
    public RestResponse<Void> changeGender(
            @Validated @RequestBody MemberGenderUpdateRequest memberGenderUpdateRequest,
            @AuthenticationPrincipal MemberAccount memberAccount
    ) {
        memberService.changeGender(memberGenderUpdateRequest, memberAccount);
        return RestResponse.of(HttpStatus.OK, null);
    }

    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    @PatchMapping(MemberControllerPath.MEMBER_CHANGE_BIRTHDAY)
    public RestResponse<Void> changeBirthDay(
            @Validated @RequestBody MemberBirthDayUpdateRequest memberBirthDayUpdateRequest,
            @AuthenticationPrincipal MemberAccount memberAccount
    ) {
        memberService.changeBirthDay(memberBirthDayUpdateRequest, memberAccount);
        return RestResponse.of(HttpStatus.OK, null);
    }

    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    @PatchMapping(MemberControllerPath.MEMBER_CHANGE_PHONE)
    public RestResponse<Void> changePhone(
            @Validated @RequestBody MemberPhoneUpdateRequest memberPhoneUpdateRequest,
            @AuthenticationPrincipal MemberAccount memberAccount
    ) {
        memberService.changePhone(memberPhoneUpdateRequest, memberAccount);
        return RestResponse.of(HttpStatus.OK, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = NotExistMemberException.class)
    public RestResponse<Void> notExistMemberExceptionHandler(NotExistMemberException e) {
        return RestResponse.error(HttpStatus.BAD_REQUEST, e.getMessage() + Arrays.asList(e.getStackTrace()));
    }
}
