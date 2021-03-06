package com.min.bunjang.store.repository;

import com.min.bunjang.following.model.QFollowing;
import com.min.bunjang.product.model.QProduct;
import com.min.bunjang.store.model.QStoreThumbnail;
import com.min.bunjang.store.model.Store;
import com.min.bunjang.storereview.model.QStoreReview;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.min.bunjang.following.model.QFollowing.following;
import static com.min.bunjang.product.model.QProduct.product;
import static com.min.bunjang.store.model.QStore.store;
import static com.min.bunjang.store.model.QStoreThumbnail.storeThumbnail;
import static com.min.bunjang.storereview.model.QStoreReview.storeReview;

@Repository
@RequiredArgsConstructor
public class StoreSearchRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Store> searchByKeyword(String keyword, Pageable pageable) {
        QueryResults<Store> storeQueryResults = jpaQueryFactory.selectFrom(store)
                .distinct()
                .leftJoin(store.storeThumbnail, storeThumbnail).fetchJoin()
                .leftJoin(store.Products, product).fetchJoin()
                .leftJoin(store.followers, following).fetchJoin()
                .leftJoin(store.storeReviews, storeReview).fetchJoin()
                .where(
                        store.storeName.containsIgnoreCase(keyword)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(store.updatedDate.desc())
                .fetchResults();

        return new PageImpl<>(storeQueryResults.getResults(), pageable, storeQueryResults.getTotal());
    }

}
