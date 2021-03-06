package com.min.bunjang.store.service;

import com.min.bunjang.security.MemberAccount;
import com.min.bunjang.store.dto.response.StoreDetailResponse;
import com.min.bunjang.store.exception.NotExistStoreException;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreViewService {
    private final StoreRepository storeRepository;

    @Transactional
    public StoreDetailResponse findStore(MemberAccount requesterEmail, Long storeNum) {
        Store store = storeRepository.findById(storeNum).orElseThrow(NotExistStoreException::new);
        store.addHitsCount(requesterEmail);
        return StoreDetailResponse.of(store);
    }
}
