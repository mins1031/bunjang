package com.min.bunjang.acceptance.wishproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.min.bunjang.acceptance.common.AcceptanceTestConfig;
import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.ProductHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.store.repository.StoreRepository;
import com.min.bunjang.token.dto.TokenValuesDto;
import com.min.bunjang.wishproduct.controller.WishProductControllerPath;
import com.min.bunjang.wishproduct.controller.WishProductViewControllerPath;
import com.min.bunjang.wishproduct.dto.request.WishProductCreateRequest;
import com.min.bunjang.wishproduct.dto.response.WishProductResponses;
import com.min.bunjang.wishproduct.dto.request.WishProductsDeleteRequest;
import com.min.bunjang.wishproduct.model.WishProduct;
import com.min.bunjang.wishproduct.repository.WishProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class WishProductAcceptanceTest extends AcceptanceTestConfig {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishProductRepository wishProductRepository;

    @TestFactory
    Stream<DynamicTest> dynamicTestStream() throws JsonProcessingException {
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "writer@naver.com";
        String writerPassword = "password!writer";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(ownerEmail, ownerPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));

        Product product = ProductHelper.????????????(owner, firstCategory, secondCategory, thirdCategory, productRepository);

        return Stream.of(
                DynamicTest.dynamicTest("???????????? ????????? ??????(??????).", () -> {
                    //given
                    WishProductCreateRequest wishProductCreateRequest = new WishProductCreateRequest(owner.getNum(), product.getNum());

                    //when
                    ?????????_??????_??????(loginResult, wishProductCreateRequest);

                    //then
                    ?????????_??????_??????_??????();
                }),

                DynamicTest.dynamicTest("????????? ????????? ?????? ", () -> {
                    //given
                    Long storeNum = owner.getNum();
                    PageRequest pageRequest = PageRequest.of(0, 10);

                    //when
                    ?????????_??????_??????(loginResult, storeNum);

                    //then
                    ?????????_??????_??????_??????();
                }),

                DynamicTest.dynamicTest("??????????????? ???????????? ??????", () -> {
                    //given
                    WishProduct wishProduct = wishProductRepository.findAll().get(0);
                    WishProductsDeleteRequest wishProductsDeleteRequest = new WishProductsDeleteRequest(Arrays.asList(wishProduct.getNum()), owner.getNum());

                    //when
                    ?????????_??????_??????(loginResult, wishProductsDeleteRequest);

                    //then
                    ?????????_??????_??????_??????();
                })
        );
    }

    private void ?????????_??????_??????_??????() {
        List<WishProduct> wishProducts = wishProductRepository.findAll();
        Assertions.assertThat(wishProducts).hasSize(1);
    }

    private void ?????????_??????_??????(TokenValuesDto loginResult, Long storeNum) {
        String path = WishProductViewControllerPath.WISH_PRODUCT_FIND_BY_STORE.replace("{storeNum}", String.valueOf(storeNum));
        getRequest(path, loginResult.getAccessToken(), new TypeReference<RestResponse<WishProductResponses>>() {});
    }

    private void ?????????_??????_??????(TokenValuesDto loginResult, WishProductCreateRequest wishProductCreateRequest) throws JsonProcessingException {
        postRequest(WishProductControllerPath.WISH_PRODUCT_CREATE, wishProductCreateRequest, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());
    }

    private void ?????????_??????_??????_??????() {
        WishProduct wishProduct = wishProductRepository.findAll().get(0);
        Assertions.assertThat(wishProduct).isNotNull();
    }

    private void ?????????_??????_??????(TokenValuesDto loginResult, WishProductsDeleteRequest wishProductsDeleteRequest) throws JsonProcessingException {
        deleteRequest(WishProductControllerPath.WISH_PRODUCT_DELETE, wishProductsDeleteRequest, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());
    }

    private void ?????????_??????_??????_??????() {
        List<WishProduct> wishProducts = wishProductRepository.findAll();
        Assertions.assertThat(wishProducts).isEmpty();
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}
