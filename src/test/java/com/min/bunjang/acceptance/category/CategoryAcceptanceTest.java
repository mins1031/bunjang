package com.min.bunjang.acceptance.category;

import com.fasterxml.jackson.core.type.TypeReference;
import com.min.bunjang.acceptance.common.AcceptanceTestConfig;
import com.min.bunjang.category.controller.CategoryViewControllerPath;
import com.min.bunjang.category.dto.response.AllCategoryListResponse;
import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.category.repository.FirstProductCategoryRepository;
import com.min.bunjang.category.repository.SecondProductCategoryRepository;
import com.min.bunjang.category.repository.ThirdProductCategoryRepository;
import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.ProductHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.dto.response.ProductSimpleResponse;
import com.min.bunjang.product.dto.response.ProductSimpleResponses;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.store.model.Store;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

public class CategoryAcceptanceTest extends AcceptanceTestConfig {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    private SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    private ThirdProductCategoryRepository thirdProductCategoryRepository;

    @TestFactory
    Stream<DynamicTest> dynamicTestStream() throws InterruptedException {
        String email = "urisegea@naver.com";
        String password = "password";
        Member member = MemberHelper.????????????(email, password, memberRepository, bCryptPasswordEncoder);
        Member member2 = MemberHelper.????????????("temp@emial", password, memberRepository, bCryptPasswordEncoder);
        Store store = StoreHelper.????????????(member, storeRepository);
        Store store2 = StoreHelper.????????????(member2, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        FirstProductCategory firstCategory2 = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate2"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        SecondProductCategory secondCategory2 = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate2", firstCategory));
        SecondProductCategory secondCategory3 = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate3", firstCategory2));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));
        ThirdProductCategory thirdCategory2 = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate2", secondCategory));
        ThirdProductCategory thirdCategory3 = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate3", secondCategory2));
        ThirdProductCategory thirdCategory4 = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate4", secondCategory3));

        Product product1 = ProductHelper.????????????(store, firstCategory, secondCategory, thirdCategory, productRepository);
        Thread.sleep(2000);
        Product product2 = ProductHelper.????????????(store, firstCategory, secondCategory, thirdCategory, productRepository);
        Thread.sleep(2000);
        Product product3 = ProductHelper.????????????(store, firstCategory2, secondCategory3, thirdCategory4, productRepository);
        Thread.sleep(2000);
        Product product4 = ProductHelper.????????????(store2, firstCategory, secondCategory, thirdCategory, productRepository);
        Thread.sleep(2000);
        Product product5 = ProductHelper.????????????(store2, firstCategory2, secondCategory3, thirdCategory4, productRepository);

        return Stream.of(
                DynamicTest.dynamicTest("?????? ???????????? ??????.", () -> {
                    //when
                    AllCategoryListResponse allCategoryListResponse = ??????????????????_??????_??????(CategoryViewControllerPath.CATEGORY_FIND_ALL);

                    //then
                    ??????????????????_??????_??????_??????(firstCategory, firstCategory2, secondCategory, secondCategory2, secondCategory3, thirdCategory, thirdCategory2, thirdCategory3, thirdCategory4, allCategoryListResponse);
                }),

                DynamicTest.dynamicTest("?????? ??????????????? ?????? ??????.", () -> {
                    //when
                    ProductSimpleResponses productSimpleResponses = ??????_????????????_??????_??????_??????(firstCategory);

                    //then
                    ??????_????????????_??????_??????_??????_??????(product1, product2, product4, productSimpleResponses);
                }),

                DynamicTest.dynamicTest("Second ??????????????? ?????? ??????.", () -> {
                    //when
                    ProductSimpleResponses productSimpleResponses = Second_????????????_??????_??????_??????(secondCategory3);

                    //then
                    Second_????????????_??????_??????_??????_??????(product3, product5, productSimpleResponses);
                }),

                DynamicTest.dynamicTest("Third ??????????????? ?????? ??????.", () -> {
                    //when
                    ProductSimpleResponses productSimpleResponses = Third_????????????_??????_??????_??????(thirdCategory);

                    //then
                    Third_????????????_??????_??????_??????_??????(product1, product2, product4, productSimpleResponses);
                })
        );
    }

    private AllCategoryListResponse ??????????????????_??????_??????(String categoryFindAll) {
        return getRequest(categoryFindAll, "", new TypeReference<RestResponse<AllCategoryListResponse>>() {
        }).getResult();
    }

    private void ??????????????????_??????_??????_??????(FirstProductCategory firstCategory, FirstProductCategory firstCategory2, SecondProductCategory secondCategory, SecondProductCategory secondCategory2, SecondProductCategory secondCategory3, ThirdProductCategory thirdCategory, ThirdProductCategory thirdCategory2, ThirdProductCategory thirdCategory3, ThirdProductCategory thirdCategory4, AllCategoryListResponse allCategoryListResponse) {
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList()).hasSize(2);
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList()).hasSize(2);
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getCategoryName()).isEqualTo(firstCategory.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(1).getSecondProductCategoryResponseList()).hasSize(1);
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(1).getCategoryName()).isEqualTo(firstCategory2.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(0).getThirdProductCategoryResponses())
                .hasSize(2);
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(0).getCategoryName())
                .isEqualTo(secondCategory.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(1).getCategoryName())
                .isEqualTo(secondCategory2.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(1).getThirdProductCategoryResponses())
                .hasSize(1);
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(1).getSecondProductCategoryResponseList().get(0).getThirdProductCategoryResponses())
                .hasSize(1);
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(1).getSecondProductCategoryResponseList().get(0).getCategoryName())
                .isEqualTo(secondCategory3.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(0).getThirdProductCategoryResponses().get(0).getCategoryName())
                .isEqualTo(thirdCategory.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(0).getThirdProductCategoryResponses().get(1).getCategoryName())
                .isEqualTo(thirdCategory2.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(0).getSecondProductCategoryResponseList().get(1).getThirdProductCategoryResponses().get(0).getCategoryName())
                .isEqualTo(thirdCategory3.getCategoryName());
        Assertions.assertThat(allCategoryListResponse.getFirstProductCategoryResponseList().get(1).getSecondProductCategoryResponseList().get(0).getThirdProductCategoryResponses().get(0).getCategoryName())
                .isEqualTo(thirdCategory4.getCategoryName());
    }

    private ProductSimpleResponses ??????_????????????_??????_??????_??????(FirstProductCategory firstCategory) {
        String path = CategoryViewControllerPath.CATEGORY_FIND_BY_FIRST.replace("{firstCategoryNum}", String.valueOf(firstCategory.getNum()));
        ProductSimpleResponses productSimpleResponses = getRequest(path, "", new TypeReference<RestResponse<ProductSimpleResponses>>() {
        }).getResult();
        return productSimpleResponses;
    }

    private void ??????_????????????_??????_??????_??????_??????(Product product1, Product product2, Product product4, ProductSimpleResponses productSimpleResponses) {
        List<ProductSimpleResponse> productSimpleResponseList = productSimpleResponses.getProductSimpleResponses();
        Assertions.assertThat(productSimpleResponseList).hasSize(3);
        Assertions.assertThat(productSimpleResponseList.get(0).getProductNum()).isEqualTo(product4.getNum());
        Assertions.assertThat(productSimpleResponseList.get(1).getProductNum()).isEqualTo(product2.getNum());
        Assertions.assertThat(productSimpleResponseList.get(2).getProductNum()).isEqualTo(product1.getNum());
    }

    private ProductSimpleResponses Second_????????????_??????_??????_??????(SecondProductCategory secondCategory3) {
        String path = CategoryViewControllerPath.CATEGORY_FIND_BY_SECOND.replace("{secondCategoryNum}", String.valueOf(secondCategory3.getNum()));
        return getRequest(path, "", new TypeReference<RestResponse<ProductSimpleResponses>>() {
        }).getResult();
    }

    private void Second_????????????_??????_??????_??????_??????(Product product3, Product product5, ProductSimpleResponses productSimpleResponses) {
        List<ProductSimpleResponse> productSimpleResponseList = productSimpleResponses.getProductSimpleResponses();
        Assertions.assertThat(productSimpleResponseList).hasSize(2);
        Assertions.assertThat(productSimpleResponseList.get(0).getProductNum()).isEqualTo(product5.getNum());
        Assertions.assertThat(productSimpleResponseList.get(1).getProductNum()).isEqualTo(product3.getNum());
    }

    private ProductSimpleResponses Third_????????????_??????_??????_??????(ThirdProductCategory thirdCategory) {
        String path = CategoryViewControllerPath.CATEGORY_FIND_BY_THIRD.replace("{thirdCategoryNum}", String.valueOf(thirdCategory.getNum()));
        return getRequest(path, "", new TypeReference<RestResponse<ProductSimpleResponses>>() {
        }).getResult();
    }

    private void Third_????????????_??????_??????_??????_??????(Product product1, Product product2, Product product4, ProductSimpleResponses productSimpleResponses) {
        List<ProductSimpleResponse> productSimpleResponseList = productSimpleResponses.getProductSimpleResponses();
        Assertions.assertThat(productSimpleResponseList).hasSize(3);
        Assertions.assertThat(productSimpleResponseList.get(0).getProductNum()).isEqualTo(product4.getNum());
        Assertions.assertThat(productSimpleResponseList.get(1).getProductNum()).isEqualTo(product2.getNum());
        Assertions.assertThat(productSimpleResponseList.get(2).getProductNum()).isEqualTo(product1.getNum());
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}