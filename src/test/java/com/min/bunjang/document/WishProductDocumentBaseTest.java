package com.min.bunjang.document;

import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.ProductHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.config.DocumentBaseTest;
import com.min.bunjang.token.jwt.TokenProvider;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.token.dto.TokenValuesDto;
import com.min.bunjang.wishproduct.controller.WishProductControllerPath;
import com.min.bunjang.wishproduct.controller.WishProductViewControllerPath;
import com.min.bunjang.wishproduct.dto.request.WishProductCreateRequest;
import com.min.bunjang.wishproduct.dto.request.WishProductsDeleteRequest;
import com.min.bunjang.wishproduct.model.WishProduct;
import com.min.bunjang.wishproduct.repository.WishProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Arrays;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WishProductDocumentBaseTest extends DocumentBaseTest {
    @Autowired
    private WishProductRepository wishProductRepository;

    @DisplayName("????????? ?????? ???????????????")
    @Test
    public void wishProduct_create() throws Exception {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "visitor@naver.com";
        String writerPassword = "password!visitor";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(ownerEmail, ownerPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));

        Product product = ProductHelper.????????????(owner, firstCategory, secondCategory, thirdCategory, productRepository);

        WishProductCreateRequest wishProductCreateRequest = new WishProductCreateRequest(owner.getNum(), product.getNum());

        //when & then
        mockMvc.perform(post(WishProductControllerPath.WISH_PRODUCT_CREATE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(wishProductCreateRequest))
                        .header(TokenProvider.ACCESS_TOKEN_KEY_NAME, loginResult.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("wishProduct-create",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("storeNum").description("????????? ?????? ????????? ????????? ?????? ??????"),
                                fieldWithPath("productNum").description("????????? ????????? ????????? ?????? ??????.")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("?????? ??? ?????? ????????? ?????? ??????.")
                        )
                ));
    }

    @DisplayName("????????? ?????? ???????????????")
    @Test
    public void wishProduct_findAll_byStore() throws Exception {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "visitor@naver.com";
        String writerPassword = "password!visitor";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(ownerEmail, ownerPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));

        Product product = ProductHelper.????????????(owner, firstCategory, secondCategory, thirdCategory, productRepository);

        WishProductCreateRequest wishProductCreateRequest = new WishProductCreateRequest(owner.getNum(), product.getNum());

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(WishProductViewControllerPath.WISH_PRODUCT_FIND_BY_STORE, owner.getNum())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(TokenProvider.ACCESS_TOKEN_KEY_NAME, loginResult.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("wishProduct-findAll-byStore",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("storeNum").description("????????? ?????? ????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        )
                ));
    }

    @DisplayName("????????? ?????? ???????????????")
    @Test
    public void wishProduct_delete() throws Exception {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "visitor@naver.com";
        String writerPassword = "password!visitor";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(ownerEmail, ownerPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));

        Product product = ProductHelper.????????????(owner, firstCategory, secondCategory, thirdCategory, productRepository);

        wishProductRepository.save(new WishProduct(owner, product));

        WishProductsDeleteRequest wishProductsDeleteRequest = new WishProductsDeleteRequest(Arrays.asList(1L), owner.getNum());

        //when & then
        mockMvc.perform(delete(WishProductControllerPath.WISH_PRODUCT_DELETE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(wishProductsDeleteRequest))
                        .header(TokenProvider.ACCESS_TOKEN_KEY_NAME, loginResult.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("wishProduct-delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("wishProductNumsForDelete").description("????????? ?????? id ?????? ?????? ??????"),
                                fieldWithPath("storeNum").description("????????? ?????? ????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("?????? ??? ?????? ????????? ?????? ??????.")
                        )
                ));
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}
