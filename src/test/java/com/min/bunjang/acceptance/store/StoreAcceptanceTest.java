package com.min.bunjang.acceptance.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.min.bunjang.acceptance.common.AcceptanceTestConfig;
import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.category.repository.FirstProductCategoryRepository;
import com.min.bunjang.category.repository.SecondProductCategoryRepository;
import com.min.bunjang.category.repository.ThirdProductCategoryRepository;
import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.ProductHelper;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.store.controller.StoreControllerPath;
import com.min.bunjang.store.controller.StoreViewControllerPath;
import com.min.bunjang.store.dto.request.StoreCreateOrUpdateRequest;
import com.min.bunjang.store.dto.response.StoreCreateResponse;
import com.min.bunjang.store.dto.response.StoreDetailResponse;
import com.min.bunjang.store.dto.request.StoreIntroduceUpdateRequest;
import com.min.bunjang.store.dto.request.StoreNameUpdateRequest;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.token.dto.TokenValuesDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

public class StoreAcceptanceTest extends AcceptanceTestConfig {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    private SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    private ThirdProductCategoryRepository thirdProductCategoryRepository;

    @TestFactory
    Stream<DynamicTest> dynamicTestStream() throws JsonProcessingException {
        String email = "urisegea@naver.com";
        String password = "password";
        Member member = MemberHelper.회원가입(email, password, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.인수테스트_로그인(email, password).getResult();

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));

        return Stream.of(
                DynamicTest.dynamicTest("상점 생성.", () -> {
                    //given
                    String storeName = "storeName";
                    String introduceContent = "introduceContent";
                    StoreCreateOrUpdateRequest storeCreateOrUpdateRequest = new StoreCreateOrUpdateRequest(storeName, introduceContent, null, null, null, null);
                    //when
                    StoreCreateResponse storeCreateResponse = 상점생성_요청(loginResult, storeCreateOrUpdateRequest);

                    //then
                    상점생성_요청_검증(storeName, introduceContent, storeCreateResponse);
                }),

                //!nulP
                DynamicTest.dynamicTest("상점 단건조회.", () -> {
                    //given
                    Store store = storeRepository.findAll().get(0);
                    Product product1 = ProductHelper.상품생성(store, firstCategory, secondCategory, thirdCategory, productRepository);
                    Product product2 = ProductHelper.상품생성(store, firstCategory, secondCategory, thirdCategory, productRepository);
                    Product product3 = ProductHelper.상품생성(store, firstCategory, secondCategory, thirdCategory, productRepository);

                    //when
                    StoreDetailResponse storeDetailResponse = 상점_단건조회_요청(loginResult, store);

                    //then
                    상점_단건조회_응답_검증(store, storeDetailResponse);
                }),

                DynamicTest.dynamicTest("상점 소개글 변경.", () -> {
                    //given
                    Store store = storeRepository.findAll().get(0);
                    String introduceContent = "updateIntroduceContent";

                    StoreIntroduceUpdateRequest storeIntroduceUpdateRequest = new StoreIntroduceUpdateRequest(introduceContent);
                    //when
                    상점소개글_변경_요청(loginResult, storeIntroduceUpdateRequest);

                    //then
                    상점소개글_변경_요청_검증(store, introduceContent);
                }),

                DynamicTest.dynamicTest("상점이름 변경.", () -> {
                    //given
                    Store store = storeRepository.findAll().get(0);
                    String updateStoreName = "updateStoreName";
                    StoreNameUpdateRequest storeNameUpdateRequest = new StoreNameUpdateRequest(updateStoreName);

                    //when
                    상점이름_변경_요청(loginResult, storeNameUpdateRequest);

                    //then
                    상점이름_변경_요청_검증(store, updateStoreName);
                })
        );
    }


    private StoreCreateResponse 상점생성_요청(TokenValuesDto loginResult, StoreCreateOrUpdateRequest storeCreateOrUpdateRequest) throws JsonProcessingException {
        return postRequest(StoreControllerPath.STORE_CREATE, storeCreateOrUpdateRequest, new TypeReference<RestResponse<StoreCreateResponse>>() {
        }, loginResult.getAccessToken()).getResult();
    }

    private void 상점생성_요청_검증(String storeName, String introduceContent, StoreCreateResponse storeCreateResponse) {
        Assertions.assertThat(storeCreateResponse.getStoreId()).isNotNull();
        Assertions.assertThat(storeCreateResponse.getStoreName()).isEqualTo(storeName);
        Assertions.assertThat(storeCreateResponse.getIntroduceContent()).isEqualTo(introduceContent);
    }

    private StoreDetailResponse 상점_단건조회_요청(TokenValuesDto loginResult, Store store) {
        String path = StoreViewControllerPath.STORE_FIND.replace("{storeNum}", String.valueOf(store.getNum()));
        StoreDetailResponse storeDetailResponse = getRequest(path, loginResult.getAccessToken(), new TypeReference<RestResponse<StoreDetailResponse>>() {
        }).getResult();
        return storeDetailResponse;
    }

    private void 상점_단건조회_응답_검증(Store store, StoreDetailResponse storeDetailResponse) {
        Assertions.assertThat(storeDetailResponse.getStoreNum()).isEqualTo(store.getNum());
        Assertions.assertThat(storeDetailResponse.getStoreThumbnail()).isNull();
        Assertions.assertThat(storeDetailResponse.getStoreName()).isEqualTo(store.getStoreName());
        Assertions.assertThat(storeDetailResponse.getHits()).isEqualTo(store.getHits() + 1);
        Assertions.assertThat(storeDetailResponse.getIntroduceContent()).isEqualTo(store.getIntroduceContent());
    }

    private RestResponse<Void> 상점소개글_변경_요청(TokenValuesDto loginResult, StoreIntroduceUpdateRequest storeIntroduceUpdateRequest) throws JsonProcessingException {
        return patchRequest(StoreControllerPath.STORE_INTRODUCE_CONTENT_UPDATE, storeIntroduceUpdateRequest, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());
    }

    private void 상점소개글_변경_요청_검증(Store store, String introduceContent) {
        String updatedIntroduceContent = storeRepository.findById(store.getNum()).get().getIntroduceContent();
        Assertions.assertThat(updatedIntroduceContent).isEqualTo(introduceContent);
    }

    private void 상점이름_변경_요청(TokenValuesDto loginResult, StoreNameUpdateRequest storeNameUpdateRequest) throws JsonProcessingException {
        patchRequest(StoreControllerPath.STORE_NAME_UPDATE, storeNameUpdateRequest, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());
    }

    private void 상점이름_변경_요청_검증(Store store, String updateStoreName) {
        Store updatedStore = storeRepository.findById(store.getNum()).get();
        Assertions.assertThat(updatedStore.getStoreName()).isEqualTo(updateStoreName);
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}
