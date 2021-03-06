package com.min.bunjang.product.service;

import com.min.bunjang.category.exception.NotExistProductCategoryException;
import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.category.repository.FirstProductCategoryRepository;
import com.min.bunjang.category.repository.SecondProductCategoryRepository;
import com.min.bunjang.category.repository.ThirdProductCategoryRepository;
import com.min.bunjang.common.validator.RightRequesterChecker;
import com.min.bunjang.product.dto.request.ProductCreateOrUpdateRequest;
import com.min.bunjang.product.dto.request.ProductDeleteRequest;
import com.min.bunjang.product.dto.request.ProductTradeStateUpdateRequest;
import com.min.bunjang.product.exception.NotExistProductException;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.product.repository.ProductTagRepository;
import com.min.bunjang.security.MemberAccount;
import com.min.bunjang.store.exception.NotExistStoreException;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductTagRepository productTagRepository;
    private final StoreRepository storeRepository;
    private final FirstProductCategoryRepository firstProductCategoryRepository;
    private final SecondProductCategoryRepository secondProductCategoryRepository;
    private final ThirdProductCategoryRepository thirdProductCategoryRepository;

    @Transactional
    public void createProduct(MemberAccount memberAccount, ProductCreateOrUpdateRequest productCreateOrUpdateRequest) {
        Store store = storeRepository.findById(productCreateOrUpdateRequest.getStoreNum()).orElseThrow(NotExistStoreException::new);
        //TODO !?????? ???????????? ???????????? ??????????????? Num????????? ???????????? ?????? ???????????? ???????????? ??? ???????????? ?????????????????? Num??? ?????? ????????? ???????????? ?????????????????? ??????????????? ????????? ????????? ?????? ???????????????. -> V2?????? ????????????.
        FirstProductCategory firstProductCategory = firstProductCategoryRepository.findById(productCreateOrUpdateRequest.getFirstCategoryNum()).orElseThrow(NotExistProductCategoryException::new);
        SecondProductCategory secondProductCategory = secondProductCategoryRepository.findById(productCreateOrUpdateRequest.getSecondCategoryNum()).orElseThrow(NotExistProductCategoryException::new);
        ThirdProductCategory thirdProductCategory = thirdProductCategoryRepository.findById(productCreateOrUpdateRequest.getThirdCategoryNum()).orElseThrow(NotExistProductCategoryException::new);
        RightRequesterChecker.verifyLoginRequest(memberAccount);
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), store);

        Product savedProduct = productRepository.save(Product.createProduct(productCreateOrUpdateRequest, firstProductCategory, secondProductCategory, thirdProductCategory, store));
        productTagRepository.saveAll(productCreateOrUpdateRequest.makeProductTags(savedProduct));
    }

    @Transactional
    public void updateProduct(MemberAccount memberAccount, Long productNum, ProductCreateOrUpdateRequest productCreateOrUpdateRequest) {
        Store store = storeRepository.findById(productCreateOrUpdateRequest.getStoreNum()).orElseThrow(NotExistStoreException::new);
        Product product = productRepository.findById(productNum).orElseThrow(NotExistProductException::new);
        FirstProductCategory firstProductCategory = firstProductCategoryRepository.findById(productCreateOrUpdateRequest.getFirstCategoryNum()).orElseThrow(NotExistProductCategoryException::new);
        SecondProductCategory secondProductCategory = secondProductCategoryRepository.findById(productCreateOrUpdateRequest.getSecondCategoryNum()).orElseThrow(NotExistProductCategoryException::new);
        ThirdProductCategory thirdProductCategory = thirdProductCategoryRepository.findById(productCreateOrUpdateRequest.getThirdCategoryNum()).orElseThrow(NotExistProductCategoryException::new);
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), store);

        product.productUpdate(productCreateOrUpdateRequest, firstProductCategory, secondProductCategory, thirdProductCategory);

        productTagRepository.deleteByProductNum(productNum);
        productTagRepository.saveAll(productCreateOrUpdateRequest.makeProductTags(product));
    }

    @Transactional
    public void updateProductTradeState(MemberAccount memberAccount, Long productNum, ProductTradeStateUpdateRequest productTradeStateUpdateRequest) {
        Product product = productRepository.findById(productNum).orElseThrow(NotExistProductException::new);
        Store store = product.checkAndReturnStore();
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), store);

        product.updateProductTradeState(productTradeStateUpdateRequest.getProductTradeState());
    }

    @Transactional
    public void deleteProduct(MemberAccount memberAccount, ProductDeleteRequest productDeleteRequest) {
        Store store = storeRepository.findById(productDeleteRequest.getStoreNum()).orElseThrow(NotExistStoreException::new);
        Product product = productRepository.findById(productDeleteRequest.getProductNum()).orElseThrow(NotExistProductException::new);
        RightRequesterChecker.verifyMemberAndStoreMatchByEmail(memberAccount.getEmail(), store);

        productTagRepository.deleteByProductNum(product.getNum());
        productRepository.deleteById(productDeleteRequest.getProductNum());
    }
}
