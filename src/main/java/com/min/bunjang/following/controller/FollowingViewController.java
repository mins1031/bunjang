package com.min.bunjang.following.controller;

import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.following.service.FollowingViewService;
import com.min.bunjang.store.dto.StoreSimpleResponses;
import lombok.RequiredArgsConstructor;
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
    public RestResponse<StoreSimpleResponses> findFollowingByStore(
            @NotNull @PathVariable Long storeNum
    ) {
        StoreSimpleResponses storeSimpleResponses = followingViewService.findFollowingByStore(storeNum);
        return RestResponse.of(HttpStatus.OK, storeSimpleResponses);
    }
}
