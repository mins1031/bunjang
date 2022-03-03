package com.min.bunjang.wishproduct.service;

import com.min.bunjang.common.database.DatabaseCleanup;
import com.min.bunjang.member.dto.MemberDirectCreateDto;
import com.min.bunjang.member.model.Member;
import com.min.bunjang.member.model.MemberRole;
import com.min.bunjang.member.repository.MemberRepository;
import com.min.bunjang.product.model.Product;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.store.repository.StoreRepository;
import com.min.bunjang.wishproduct.dto.WishProductCreateRequest;
import com.min.bunjang.wishproduct.dto.WishProductsDeleteRequest;
import com.min.bunjang.wishproduct.model.WishProduct;
import com.min.bunjang.wishproduct.repository.WishProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("h2")
class WishProductServiceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private WishProductService wishProductService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishProductRepository wishProductRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("찜상품이 추가되면 관련 제품과 상점에 찜목록이 등록된다")
    @Test
    public void wishProduct_create() {
        //given
        Member member = Member.createMember(MemberDirectCreateDto.of("email", "pwd", "name", null, null, MemberRole.ROLE_MEMBER));
        Member savedMember = memberRepository.save(member);
        Store savedStore = storeRepository.save(Store.createStore("storeName", "introduce", null, member));
        Product savedProduct = productRepository.save(new Product("productName"));

        WishProductCreateRequest wishProductCreateRequest = new WishProductCreateRequest(savedStore.getNum(), savedProduct.getNum());

        //when
        wishProductService.createWishProduct(wishProductCreateRequest);

        //then
        WishProduct wishProduct = wishProductRepository.findAll().get(0);
        Assertions.assertThat(wishProduct).isNotNull();
        Assertions.assertThat(wishProduct.getProduct()).isNotNull();
        Assertions.assertThat(wishProduct.getStore()).isNotNull();
    }

    @DisplayName("찜목록들이 삭제된다.")
    @Test
    public void wishProduct_delete() {
        //given
        Member member = Member.createMember(MemberDirectCreateDto.of("email", "pwd", "name", null, null, MemberRole.ROLE_MEMBER));
        memberRepository.save(member);
        Store savedStore = storeRepository.save(Store.createStore("storeName", "introduce", null, member));
        Product savedProduct = productRepository.save(new Product("productName"));
        Product savedProduct2 = productRepository.save(new Product("productName2"));
        Product savedProduct3 = productRepository.save(new Product("productName3"));
        Product savedProduct4 = productRepository.save(new Product("productName4"));

        List<WishProduct> wishProducts = Arrays.asList(
                new WishProduct(savedStore, savedProduct),
                new WishProduct(savedStore, savedProduct2),
                new WishProduct(savedStore, savedProduct3)
        );
        List<WishProduct> savedWishProducts = wishProductRepository.saveAll(wishProducts);
        WishProduct savedWishProduct = wishProductRepository.save(new WishProduct(savedStore, savedProduct4));

        WishProductsDeleteRequest wishProductsDeleteRequest = 
                new WishProductsDeleteRequest(savedWishProducts.stream().map(wishProduct -> wishProduct.getNum()).collect(Collectors.toList()));
        
        //when
        wishProductService.deleteWishProducts(wishProductsDeleteRequest);
        
        //then
        List<WishProduct> allWishProduct = wishProductRepository.findAll();
        Assertions.assertThat(allWishProduct).hasSize(1);
        Assertions.assertThat(allWishProduct.get(0).getNum()).isEqualTo(savedWishProduct.getNum());
    }

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
    }
}