package com.min.bunjang.storeinquire.controller;

import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.storeinquire.dto.response.StoreInquireListResponses;
import com.min.bunjang.storeinquire.service.StoreInquireViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreInquireViewController {
    private final StoreInquireViewService storeInquireViewService;

    @GetMapping(StoreInquireViewControllerPath.INQUIRIES_FIND_RELATED_STORE)
    public RestResponse<StoreInquireListResponses> getStoreInquiriesRelatedStore(
            @PathVariable Long storeNum,
            @PageableDefault(sort = "num", direction = Sort.Direction.DESC, size = 10) Pageable pageable
    ) {
        StoreInquireListResponses storeInquiriesRelatedStore = storeInquireViewService.findStoreInquiriesRelatedStore(storeNum, pageable);
        return RestResponse.of(HttpStatus.OK, storeInquiriesRelatedStore);
    }
}
