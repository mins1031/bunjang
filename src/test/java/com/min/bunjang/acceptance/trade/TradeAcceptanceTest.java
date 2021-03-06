package com.min.bunjang.acceptance.trade;

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
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.dto.request.ProductCreateOrUpdateRequest;
import com.min.bunjang.product.model.DeliveryChargeInPrice;
import com.min.bunjang.product.model.ExchangeState;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.model.ProductQualityState;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.token.dto.TokenValuesDto;
import com.min.bunjang.trade.controller.TradeControllerPath;
import com.min.bunjang.trade.dto.request.TradeCreateRequest;
import com.min.bunjang.trade.dto.response.TradeCreateResponse;
import com.min.bunjang.trade.model.Trade;
import com.min.bunjang.trade.model.TradeState;
import com.min.bunjang.trade.repository.TradeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TradeAcceptanceTest extends AcceptanceTestConfig {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    private SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    private ThirdProductCategoryRepository thirdProductCategoryRepository;

    @TestFactory
    Stream<DynamicTest> dynamicTestStream() throws JsonProcessingException {
        String sellerEmail = "urisegea@naver.com";
        String sellerPassword = "password";
        Member sellerMember = MemberHelper.????????????(sellerEmail, sellerPassword, memberRepository, bCryptPasswordEncoder);

        String buyerEmail = "writer@naver.com";
        String buyerPassword = "password!writer";
        Member buyerMember = MemberHelper.????????????(buyerEmail, buyerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(buyerEmail, buyerPassword).getResult();

        Store seller = StoreHelper.????????????(sellerMember, storeRepository);
        Store buyer = StoreHelper.????????????(buyerMember, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));

        ProductCreateOrUpdateRequest productCreateOrUpdateRequest = new ProductCreateOrUpdateRequest(
                null,
                null,
                "productName",
                firstCategory.getNum(),
                secondCategory.getNum(),
                thirdCategory.getNum(),
                "seoul",
                ProductQualityState.NEW_PRODUCT,
                ExchangeState.IMPOSSIBILITY,
                100000,
                DeliveryChargeInPrice.EXCLUDED,
                "?????? ?????? ?????????.",
                Arrays.asList("tag1", "tag2"),
                1
        );

        Product savedProduct = productRepository.save(Product.createProduct(productCreateOrUpdateRequest, firstCategory, secondCategory, thirdCategory, seller));

        return Stream.of(
                DynamicTest.dynamicTest("?????? ??????.", () -> {
                    //given
                    TradeCreateRequest tradeCreateRequest = new TradeCreateRequest(seller.getNum(), buyer.getNum(), savedProduct.getNum());

                    //when
                    ??????_??????_??????(loginResult, tradeCreateRequest);

                    //then
                    ??????_??????_??????_??????();
                }),

                DynamicTest.dynamicTest("?????? ??????.", () -> {
                    //given
                    Trade trade = tradeRepository.findAll().get(0);

                    //when
                    ??????_??????_??????(loginResult, TradeControllerPath.TRADE_COMPLETE, trade.getNum());

                    //then
                    ??????_??????_??????_??????(trade.getNum(), TradeState.TRADE_COMPLETE);

                }),

                DynamicTest.dynamicTest("?????? ??????.", () -> {
                    //given
                    Long tradeNum = tradeRepository.findAll().get(0).getNum();

                    //when
                    ??????_??????_??????(loginResult, tradeNum);

                    //then
                    ??????_??????_??????_??????(tradeNum, TradeState.TRADE_CANCEL);
                })
        );

    }

    private void ??????_??????_??????(TokenValuesDto loginResult, TradeCreateRequest tradeCreateRequest) throws JsonProcessingException {
        postRequest(TradeControllerPath.TRADE_CREATE, tradeCreateRequest, new TypeReference<RestResponse<TradeCreateResponse>>() {
        }, loginResult.getAccessToken());
    }

    private void ??????_??????_??????_??????() {
        List<Trade> trades = tradeRepository.findAll();
        Assertions.assertThat(trades).hasSize(1);
    }

    private void ??????_??????_??????(TokenValuesDto loginResult, String tradeComplete, Long num) throws JsonProcessingException {
        String path = tradeComplete.replace("{tradeNum}", String.valueOf(num));
        patchRequest(path, null, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());
    }

    private void ??????_??????_??????_??????(Long num, TradeState tradeComplete) {
        Trade completedTrade = tradeRepository.findById(num).get();
        Assertions.assertThat(completedTrade.getTradeState()).isEqualTo(tradeComplete);
    }

    private void ??????_??????_??????(TokenValuesDto loginResult, Long tradeNum) throws JsonProcessingException {
        String path = TradeControllerPath.TRADE_CANCEL.replace("{tradeNum}", String.valueOf(tradeNum));
        deleteRequest(path, null, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());
    }

    private void ??????_??????_??????_??????(Long tradeNum, TradeState tradeCancel) {
        Trade trade = tradeRepository.findById(tradeNum).get();
        Assertions.assertThat(trade.getTradeState()).isEqualTo(tradeCancel);
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}