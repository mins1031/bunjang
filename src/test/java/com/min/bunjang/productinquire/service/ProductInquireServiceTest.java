package com.min.bunjang.productinquire.service;

import com.min.bunjang.category.model.FirstProductCategory;
import com.min.bunjang.category.model.SecondProductCategory;
import com.min.bunjang.category.model.ThirdProductCategory;
import com.min.bunjang.category.repository.FirstProductCategoryRepository;
import com.min.bunjang.category.repository.SecondProductCategoryRepository;
import com.min.bunjang.category.repository.ThirdProductCategoryRepository;
import com.min.bunjang.common.exception.ImpossibleException;
import com.min.bunjang.config.ServiceBaseTest;
import com.min.bunjang.helpers.MemberHelper;
import com.min.bunjang.helpers.StoreHelper;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.product.dto.request.ProductCreateOrUpdateRequest;
import com.min.bunjang.product.model.DeliveryChargeInPrice;
import com.min.bunjang.product.model.ExchangeState;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.model.ProductQualityState;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.productinquire.dto.request.ProductInquireCreateRequest;
import com.min.bunjang.productinquire.model.ProductInquire;
import com.min.bunjang.productinquire.repository.ProductInquireRepository;
import com.min.bunjang.security.MemberAccount;
import com.min.bunjang.store.model.Store;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

class ProductInquireServiceTest extends ServiceBaseTest {
    @Autowired
    private ProductInquireService productInquireService;

    @Autowired
    private ProductInquireRepository productInquireRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    private SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    private ThirdProductCategoryRepository thirdProductCategoryRepository;

    @DisplayName("?????? ????????? ????????????, ?????? ????????? ????????? ??????????????? ?????? ????????????. ?????? ????????? ????????? ????????? ??????????????? ????????????.")
    @Test
    void ????????????_??????() {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "writer@naver.com";
        String writerPassword = "password!writer";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

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
        Product savedProduct = productRepository.save(Product.createProduct(productCreateOrUpdateRequest, firstCategory, secondCategory, thirdCategory, owner));

        ProductInquireCreateRequest productInquireCreateRequest = new ProductInquireCreateRequest(writer.getNum(), savedProduct.getNum(), "?????? ??????", null);
        ProductInquireCreateRequest answerProductInquireCreateRequest = new ProductInquireCreateRequest(owner.getNum(), savedProduct.getNum(), "?????? ?????? ???????????????", writer.getNum());
        MemberAccount writerMemberAccount = new MemberAccount(writerMember);
        MemberAccount ownerMemberAccount = new MemberAccount(ownerMember);

        //when
        productInquireService.createProductInquire(writerMemberAccount, productInquireCreateRequest);
        productInquireService.createProductInquire(ownerMemberAccount, answerProductInquireCreateRequest);

        //then
        List<ProductInquire> all = productInquireRepository.findAll();

        Assertions.assertThat(all.get(0).getProductNum()).isEqualTo(productInquireCreateRequest.getProductNum());
        Assertions.assertThat(all.get(0).getWriterNum()).isEqualTo(productInquireCreateRequest.getWriterNum());
        Assertions.assertThat(all.get(0).getInquireContent()).isEqualTo(productInquireCreateRequest.getInquireContent());

        Assertions.assertThat(all.get(1).getProductNum()).isEqualTo(answerProductInquireCreateRequest.getProductNum());
        Assertions.assertThat(all.get(1).getWriterNum()).isEqualTo(answerProductInquireCreateRequest.getWriterNum());
        Assertions.assertThat(all.get(1).getInquireContent()).isEqualTo(answerProductInquireCreateRequest.getInquireContent());
        Assertions.assertThat(all.get(1).getMentionedStoreNumForAnswer()).isEqualTo(answerProductInquireCreateRequest.getMentionedStoreNumForAnswer());


        Product product = productRepository.findById(savedProduct.getNum()).get();
        Assertions.assertThat(product.getProductInquires()).isNotNull();
    }

    @DisplayName("[??????] ?????? ????????? ????????? ???????????? ??????????????? ??????????????? ?????? ????????? ????????????.")
//    @Test
    void ??????_??????????????????_????????????????????????_?????????() {
        //given
        String ownerEmail = "urisegea@naver.com";
        String ownerPassword = "password";
        Member ownerMember = MemberHelper.????????????(ownerEmail, ownerPassword, memberRepository, bCryptPasswordEncoder);

        String writerEmail = "writer@naver.com";
        String writerPassword = "password!writer";
        Member writerMember = MemberHelper.????????????(writerEmail, writerPassword, memberRepository, bCryptPasswordEncoder);

        Store owner = StoreHelper.????????????(ownerMember, storeRepository);
        Store writer = StoreHelper.????????????(writerMember, storeRepository);

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
        Product savedProduct = productRepository.save(Product.createProduct(productCreateOrUpdateRequest, firstCategory, secondCategory, thirdCategory, owner));

        ProductInquireCreateRequest productInquireCreateRequest = new ProductInquireCreateRequest(writer.getNum(), savedProduct.getNum(), "?????? ??????", null);
        MemberAccount writerMemberAccount = new MemberAccount(writerMember);
        MemberAccount ownerMemberAccount = new MemberAccount(ownerMember);

        //when & then
        Assertions.assertThatThrownBy(() -> productInquireService.createProductInquire(null, productInquireCreateRequest)).isInstanceOf(ImpossibleException.class);
    }

    @AfterEach
    void tearDown() {
        databaseFormat.clean();
    }
}