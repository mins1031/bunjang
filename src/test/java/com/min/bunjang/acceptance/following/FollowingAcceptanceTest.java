package com.min.bunjang.acceptance.following;

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
import com.min.bunjang.following.controller.FollowingControllerPath;
import com.min.bunjang.following.controller.FollowingViewControllerPath;
import com.min.bunjang.following.dto.request.FollowingCreateRequest;
import com.min.bunjang.following.dto.response.FollowingListResponse;
import com.min.bunjang.following.dto.response.FollowingResponse;
import com.min.bunjang.following.model.Following;
import com.min.bunjang.following.repository.FollowingRepository;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.token.dto.TokenValuesDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FollowingAcceptanceTest extends AcceptanceTestConfig {

    @Autowired
    private FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    private SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    private ThirdProductCategoryRepository thirdProductCategoryRepository;

    @Autowired
    private FollowingRepository followingRepository;

    @TestFactory
    Stream<DynamicTest> dynamicTestStream() throws JsonProcessingException {
        String followerEmail = "urisegea@naver.com";
        String followerPassword = "password";
        Member followerMember = MemberHelper.????????????(followerEmail, followerPassword, memberRepository, bCryptPasswordEncoder);
        TokenValuesDto loginResult = MemberHelper.???????????????_?????????(followerEmail, followerPassword).getResult();

        String followedEmail = "writer@naver.com";
        String followedPassword = "password!writer";
        Member followedMember = MemberHelper.????????????(followedEmail, followedPassword, memberRepository, bCryptPasswordEncoder);

        Store follower = StoreHelper.????????????(followerMember, storeRepository);
        Store followed = StoreHelper.????????????(followedMember, storeRepository);

        FirstProductCategory firstCategory = firstProductCategoryRepository.save(FirstProductCategory.createFirstProductCategory("firstCate"));
        SecondProductCategory secondCategory = secondProductCategoryRepository.save(SecondProductCategory.createSecondCategory("secondCate", firstCategory));
        ThirdProductCategory thirdCategory = thirdProductCategoryRepository.save(ThirdProductCategory.createThirdCategory("thirdCate", secondCategory));


        return Stream.of(
                DynamicTest.dynamicTest("????????? ??????.", () -> {
                    //given
                    FollowingCreateRequest followingCreateRequest = new FollowingCreateRequest(follower.getNum(), followed.getNum());

                    //when
                    postRequest(FollowingControllerPath.FOLLOWING_CREATE, followingCreateRequest, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());

                    //then
                    List<Following> followings = followingRepository.findAll();
                    Assertions.assertThat(followings).hasSize(1);
                }),

                //! nullP
                DynamicTest.dynamicTest("????????? ????????? ???????????? ??????.", () -> {
                    //given
                    followingRepository.save(Following.createFollowing(null, null));

                    //when
                    String path = FollowingViewControllerPath.FOLLOWINGS_FIND_BY_STORE.replace("{storeNum}", String.valueOf(follower.getNum()));
                    List<FollowingResponse> followingResponseList = getRequest(path, loginResult.getAccessToken(), new TypeReference<RestResponse<FollowingListResponse>>() {
                    }).getResult().getFollowingResponseList();

                    //then
                    Assertions.assertThat(followingResponseList).hasSize(1);
                    Assertions.assertThat(followingResponseList.get(0).getStoreSimpleResponses().getStoreNum()).isEqualTo(followed.getNum());
                }),

                DynamicTest.dynamicTest("????????? ????????? ???????????? ??????.", () -> {
                    //given
                    followingRepository.save(Following.createFollowing(null, null));

                    //when
                    String path = FollowingViewControllerPath.FOLLOWERS_FIND_BY_STORE.replace("{storeNum}", String.valueOf(followed.getNum()));
                    List<FollowingResponse> followingResponseList = getRequest(path, loginResult.getAccessToken(), new TypeReference<RestResponse<FollowingListResponse>>() {
                    }).getResult().getFollowingResponseList();

                    //then
                    Assertions.assertThat(followingResponseList).hasSize(1);
                    Assertions.assertThat(followingResponseList.get(0).getStoreSimpleResponses().getStoreNum()).isEqualTo(follower.getNum());
                }),

                DynamicTest.dynamicTest("????????? ??????.", () -> {
                    //given
                    Following following = followingRepository.findAll().get(0);

                    //when
                    String path = FollowingControllerPath.FOLLOWING_DELETE.replace("{storeNum}", String.valueOf(follower.getNum())).replace("{followingNum}", String.valueOf(following.getNum()));
                    deleteRequest(path, null, new TypeReference<RestResponse<Void>>() {}, loginResult.getAccessToken());

                    //then
                    Optional<Following> deletedFollowing = followingRepository.findById(following.getNum());
                    Assertions.assertThat(deletedFollowing.isPresent()).isFalse();
                })
        );

    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}