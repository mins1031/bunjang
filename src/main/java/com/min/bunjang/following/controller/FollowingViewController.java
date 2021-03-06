package com.min.bunjang.following.controller;

import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.following.dto.response.FollowingListResponse;
import com.min.bunjang.following.service.FollowingViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class FollowingViewController {
    private final FollowingViewService followingViewService;

    @GetMapping(FollowingViewControllerPath.FOLLOWINGS_FIND_BY_STORE)
    public RestResponse<FollowingListResponse> findFollowingsByStore(
            @NotNull @PathVariable Long storeNum
    ) {
        FollowingListResponse followingListResponse = followingViewService.findFollowingsOfStore(storeNum);
        return RestResponse.of(HttpStatus.OK, followingListResponse);
    }

    @GetMapping(FollowingViewControllerPath.FOLLOWERS_FIND_BY_STORE)
    public RestResponse<FollowingListResponse> findFollowersByStore(
            @NotNull @PathVariable Long storeNum,
            @PageableDefault(sort = "num", direction = Sort.Direction.DESC, size = 30) Pageable pageable
    ) {
        FollowingListResponse followerListResponse = followingViewService.findFollowersOfStore(storeNum, pageable);
        return RestResponse.of(HttpStatus.OK, followerListResponse);
    }
}
