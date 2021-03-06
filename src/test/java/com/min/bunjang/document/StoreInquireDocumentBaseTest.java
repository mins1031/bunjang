package com.min.bunjang.document;

import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.config.DocumentBaseTest;
import com.min.bunjang.token.jwt.TokenProvider;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.store.repository.StoreRepository;
import com.min.bunjang.storeinquire.controller.StoreInquireControllerPath;
import com.min.bunjang.storeinquire.controller.StoreInquireViewControllerPath;
import com.min.bunjang.storeinquire.dto.request.InquireCreateRequest;
import com.min.bunjang.storeinquire.model.StoreInquire;
import com.min.bunjang.storeinquire.repository.StoreInquireRepository;
import com.min.bunjang.token.dto.TokenValuesDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StoreInquireDocumentBaseTest extends DocumentBaseTest {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreInquireRepository storeInquireRepository;

    @DisplayName("???????????? ?????? ???????????????")
    @Test
    void storeInquire_create() throws Exception {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String visitorEmail = "visitor@naver.com";
        String visitorPassword = "password!visitor";
        Member visitorMember = MemberHelper.????????????(visitorEmail, visitorPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(visitorEmail, visitorPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store visitor = StoreHelper.????????????(visitorMember, storeRepository);

        InquireCreateRequest inquireCreateRequest = new InquireCreateRequest(owner.getNum(), visitor.getNum(), "????????????", null);

        //when & then
        mockMvc.perform(post(StoreInquireControllerPath.CREATE_INQUIRY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(TokenProvider.ACCESS_TOKEN_KEY_NAME, loginResult.getAccessToken())
                        .content(objectMapper.writeValueAsString(inquireCreateRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("storeInquiry-create",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("ownerNum").description("???????????? ?????? ????????? ?????? ??????."),
                                fieldWithPath("writerNum").description("????????? ?????? ????????? ?????? ??????."),
                                fieldWithPath("inquireContent").description("?????? ?????? ?????? ??????"),
                                fieldWithPath("mentionedStoreNumForAnswer").description("??????????????? ????????? ?????? ?????? ?????? ?????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result.inquireNum").description("????????? ????????? ??????. ????????? ??????????????? ????????? ?????? ??????"),
                                fieldWithPath("result.writerName").description("????????? ????????? ??????. ??????????????? ?????? ?????? ??????"),
                                fieldWithPath("result.inquireContent").description("????????? ????????? ??????. ????????? ???????????? ?????? ??????")
                        )
                ));
    }

    @DisplayName("???????????? ???????????? ???????????????")
    @Test
    void storeInquire_findByOwner() throws Exception {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "writer@naver.com";
        String writerPassword = "password!writer";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(writerEmail, writerPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

        storeInquireRepository.saveAll(Arrays.asList(
                StoreInquire.of(owner.getNum(), writer, "content1"),
                StoreInquire.of(owner.getNum(), writer, "content2")
        ));

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(StoreInquireViewControllerPath.INQUIRIES_FIND_RELATED_STORE, owner.getNum())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("storeInquiry-findByOwner",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("storeNum").description("??????????????? ?????? ????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        )
                ));
    }

    @DisplayName("???????????? ?????? ???????????????")
    @Test
    void storeInquire_delete() throws Exception {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String visitorEmail = "visitor@naver.com";
        String visitorPassword = "password!visitor";
        Member visitorMember = MemberHelper.????????????(visitorEmail, visitorPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(visitorEmail, visitorPassword).getResult();

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store visitor = StoreHelper.????????????(visitorMember, storeRepository);

        StoreInquire storeInquire = StoreInquire.of(owner.getNum(), visitor, "????????????");
        StoreInquire savedStoreInquiry = storeInquireRepository.save(storeInquire);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(StoreInquireControllerPath.DELETE_INQUIRY, savedStoreInquiry.getNum())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(TokenProvider.ACCESS_TOKEN_KEY_NAME, loginResult.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("storeInquiry-delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        pathParameters(
                                parameterWithName("inquireNum").description("??????????????? ??????????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("?????? ???????????? ????????????, ?????? ????????? JSON ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("????????? ?????? ???????????????. 201?????? ??????, 500??? ?????? ??????."),
                                fieldWithPath("message").description("?????? ????????? ????????? ?????? ??????."),
                                fieldWithPath("result").description("????????? ????????? ??????. ")
                        )
                ));
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}
