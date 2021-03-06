package com.min.bunjang.storereview.service;

import com.min.bunjang.common.exception.ImpossibleException;
import com.min.bunjang.common.validator.RightRequesterChecker;
import com.min.bunjang.product.exception.NotExistProductException;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.security.MemberAccount;
import com.min.bunjang.store.exception.NotExistStoreException;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.store.repository.StoreRepository;
import com.min.bunjang.storereview.dto.request.StoreReviewCreateRequest;
import com.min.bunjang.storereview.dto.request.StoreReviewUpdateRequest;
import com.min.bunjang.storereview.dto.response.StoreReviewResponse;
import com.min.bunjang.storereview.exception.NotExistStoreReviewException;
import com.min.bunjang.storereview.model.StoreReview;
import com.min.bunjang.storereview.repository.StoreReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreReviewService {
    private final StoreReviewRepository storeReviewRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public StoreReviewResponse createStoreReview(MemberAccount memberAccount, StoreReviewCreateRequest storeReviewCreateRequest) {
        Store owner = storeRepository.findById(storeReviewCreateRequest.getOwnerNum()).orElseThrow(NotExistStoreException::new);
        Store writer = storeRepository.findById(storeReviewCreateRequest.getWriterNum()).orElseThrow(NotExistStoreException::new);
        Product product = productRepository.findById(storeReviewCreateRequest.getProductNum()).orElseThrow(NotExistProductException::new);
        RightRequesterChecker.verifyLoginRequest(memberAccount);
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), writer);
        StoreReview storeReview = StoreReview.createStoreReview(
                owner,
                writer,
                writer.getStoreName(),
                storeReviewCreateRequest.getDealScore(),
                product.getNum(),
                product.getProductName(),
                storeReviewCreateRequest.getReviewContent()
        );

        //TODO !StoreReviewResponse?????? ???????????? ?????? StoreResponse???????????? ?????? ?????? ??? ???????????? ?????? ?????? ?????? ??????????????? StoreReviewResponse??? ??? ?????????????????? ??????
        //TODO -> ?????? ??????????????? ????????? ????????? ????????? ????????? ????????? ????????? ????????? ???????????????. ????????? ????????????????????? ??????????????? ??????. ?????? ????????? ???????????? ????????? ???????????? ?????? ???.
        return StoreReviewResponse.of(storeReviewRepository.save(storeReview));
    }

    @Transactional
    public void updateStoreReview(MemberAccount memberAccount, StoreReviewUpdateRequest storeReviewUpdateRequest) {
        StoreReview storeReview = storeReviewRepository.findById(storeReviewUpdateRequest.getReviewNum()).orElseThrow(NotExistStoreReviewException::new);
        RightRequesterChecker.verifyLoginRequest(memberAccount);
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), storeReview.getWriter());

        storeReview.updateReviewContent(storeReviewUpdateRequest.getUpdatedReviewContent(), storeReviewUpdateRequest.getUpdatedDealScore());
    }

    @Transactional
    public void deleteStoreReview(MemberAccount memberAccount, Long reviewNum) {
        if (reviewNum == null) {
            throw new ImpossibleException("??????????????? ????????? ???????????? null?????????. ????????? ???????????????.");
        }

        StoreReview storeReview = storeReviewRepository.findById(reviewNum).orElseThrow(NotExistStoreReviewException::new);
        RightRequesterChecker.verifyLoginRequest(memberAccount);
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), storeReview.getWriter());

        storeReviewRepository.deleteById(reviewNum);
    }
}
