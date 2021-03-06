package com.min.bunjang.document;

import com.min.bunjang.category.controller.CategoryViewControllerPath;
import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.category.repository.FirstProductCategoryRepository;
import com.min.bunjang.category.repository.SecondProductCategoryRepository;
import com.min.bunjang.category.repository.ThirdProductCategoryRepository;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.ProductHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.config.DocumentBaseTest;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.store.model.Store;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryDocumentBaseTest extends DocumentBaseTest {

    @Autowired
    private FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    private SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    private ThirdProductCategoryRepository thirdProductCategoryRepository;

    @DisplayName("?????? ???????????? ?????? ?????????")
    @Test
    public void category_all_find() throws Exception {
        //given
        FirstProductCategory category1 = FirstProductCategory.createFirstProductCategory("category1");
        SecondProductCategory category2 = SecondProductCategory.createSecondCategory("category2", category1);
        ThirdProductCategory category3 = ThirdProductCategory.createThirdCategory("category3", category2);
        FirstProductCategory category4 = FirstProductCategory.createFirstProductCategory("category4");

        firstProductCategoryRepository.save(category1);
        firstProductCategoryRepository.save(category4);
        secondProductCategoryRepository.save(category2);
        thirdProductCategoryRepository.save(category3);

        //when && then
        mockMvc.perform(get(CategoryViewControllerPath.CATEGORY_FIND_ALL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andDo(document("category-find-all",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("????????? ????????? ??????."),
                                fieldWithPath("result.firstProductCategoryResponseList").description("first ???????????? ?????????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].categoryNum").description("first ???????????? ????????? ??????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].categoryName").description("first ??????????????? ??????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].secondProductCategoryResponseList").description("second ???????????? ?????????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].secondProductCategoryResponseList[0].categoryNum").description("second ???????????? ????????? ??????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].secondProductCategoryResponseList[0].categoryName").description("second ??????????????? ??????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].secondProductCategoryResponseList[0].thirdProductCategoryResponses").description("third ???????????? ?????????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].secondProductCategoryResponseList[0].thirdProductCategoryResponses[0].categoryNum").description("third ???????????? ????????? ??????"),
                                fieldWithPath("result.firstProductCategoryResponseList[0].secondProductCategoryResponseList[0].thirdProductCategoryResponses[0].categoryName").description("third ??????????????? ??????")
                        )
                ));
    }

    @DisplayName("first ??????????????? ?????? ?????? ?????????")
    @Test
    public void category_find_by_firstCategory() throws Exception {
        //given
        FirstProductCategory category1 = FirstProductCategory.createFirstProductCategory("category1");
        SecondProductCategory category2 = SecondProductCategory.createSecondCategory("category2", category1);
        ThirdProductCategory category3 = ThirdProductCategory.createThirdCategory("category3", category2);

        FirstProductCategory saveFirstCate = firstProductCategoryRepository.save(category1);
        SecondProductCategory saveSecondCate = secondProductCategoryRepository.save(category2);
        ThirdProductCategory saveThirdCate = thirdProductCategoryRepository.save(category3);

        Member member = MemberHelper.????????????("email", "password", memberRepository, bCryptPasswordEncoder);
        Store store = StoreHelper.????????????(member, storeRepository);
        Product product1 = ProductHelper.????????????(store, saveFirstCate, saveSecondCate, saveThirdCate, productRepository);

        //when && then
        mockMvc.perform(RestDocumentationRequestBuilders.get(CategoryViewControllerPath.CATEGORY_FIND_BY_FIRST, saveFirstCate.getNum())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andDo(document("category-find-by-firstCategory",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("firstCategoryNum").description("first ???????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("????????? ????????? ??????."),
                                subsectionWithPath("result.productSimpleResponses").description("first ??????????????? ???????????? ?????? ????????? ????????? ??????."),
                                subsectionWithPath("result.pageDto").description("?????????????????? ?????? ????????? ??????.")
                        )
                ));
    }

    @DisplayName("second ??????????????? ?????? ?????? ?????????")
    @Test
    public void category_find_by_secondCategory() throws Exception {
        //given
        FirstProductCategory category1 = FirstProductCategory.createFirstProductCategory("category1");
        SecondProductCategory category2 = SecondProductCategory.createSecondCategory("category2", category1);
        ThirdProductCategory category3 = ThirdProductCategory.createThirdCategory("category3", category2);

        FirstProductCategory saveFirstCate = firstProductCategoryRepository.save(category1);
        SecondProductCategory saveSecondCate = secondProductCategoryRepository.save(category2);
        ThirdProductCategory saveThirdCate = thirdProductCategoryRepository.save(category3);

        Member member = MemberHelper.????????????("email", "password", memberRepository, bCryptPasswordEncoder);
        Store store = StoreHelper.????????????(member, storeRepository);
        Product product1 = ProductHelper.????????????(store, saveFirstCate, saveSecondCate, saveThirdCate, productRepository);

        //when && then
        mockMvc.perform(RestDocumentationRequestBuilders.get(CategoryViewControllerPath.CATEGORY_FIND_BY_SECOND, saveSecondCate.getNum())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andDo(document("category-find-by-secondCategory",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("secondCategoryNum").description("second ???????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("????????? ????????? ??????."),
                                subsectionWithPath("result.productSimpleResponses").description("second ??????????????? ???????????? ?????? ????????? ????????? ??????."),
                                subsectionWithPath("result.pageDto").description("?????????????????? ?????? ????????? ??????.")
                        )
                ));
    }

    @DisplayName("third ??????????????? ?????? ?????? ???????????????")
    @Test
    public void category_find_by_thirdCategory() throws Exception {
        //given
        FirstProductCategory category1 = FirstProductCategory.createFirstProductCategory("category1");
        SecondProductCategory category2 = SecondProductCategory.createSecondCategory("category2", category1);
        ThirdProductCategory category3 = ThirdProductCategory.createThirdCategory("category3", category2);

        FirstProductCategory saveFirstCate = firstProductCategoryRepository.save(category1);
        SecondProductCategory saveSecondCate = secondProductCategoryRepository.save(category2);
        ThirdProductCategory saveThirdCate = thirdProductCategoryRepository.save(category3);

        Member member = MemberHelper.????????????("email", "password", memberRepository, bCryptPasswordEncoder);
        Store store = StoreHelper.????????????(member, storeRepository);
        Product product1 = ProductHelper.????????????(store, saveFirstCate, saveSecondCate, saveThirdCate, productRepository);

        //when && then
        mockMvc.perform(RestDocumentationRequestBuilders.get(CategoryViewControllerPath.CATEGORY_FIND_BY_THIRD, saveSecondCate.getNum())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andDo(document("category-find-by-thirdCategory",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("thirdCategoryNum").description("third ???????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("????????? ????????? ??????."),
                                subsectionWithPath("result.productSimpleResponses").description("third ??????????????? ???????????? ?????? ????????? ????????? ??????."),
                                subsectionWithPath("result.pageDto").description("?????????????????? ?????? ????????? ??????.")
                        )
                ));
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}
