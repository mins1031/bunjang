package com.min.bunjang.storereview.dto.response;

import com.min.bunjang.aws.s3.dto.S3FileDto;
import com.min.bunjang.store.dto.response.StoreThumbnailResponse;
import com.min.bunjang.store.model.StoreThumbnail;
import com.min.bunjang.storereview.model.StoreReview;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class StoreReviewResponse {
    private Long reviewNum;
    private Long writerNum;
    private StoreThumbnailResponse writerThumbnail;
    private String writerName;
    private double dealScore;
    private Long productNum;
    private String productName;
    private String reviewContent;
    private LocalDate postingDate;


    public StoreReviewResponse(Long reviewNum, Long writerNum, StoreThumbnailResponse writerThumbnail, String writerName, double dealScore, Long productNum, String productName, String reviewContent, LocalDate postingDate) {
        this.reviewNum = reviewNum;
        this.writerNum = writerNum;
        this.writerThumbnail = writerThumbnail;
        this.writerName = writerName;
        this.dealScore = dealScore;
        this.productNum = productNum;
        this.productName = productName;
        this.reviewContent = reviewContent;
        this.postingDate = postingDate;
    }

    public static StoreReviewResponse of(StoreReview storeReview) {
        return new StoreReviewResponse(
                storeReview.getNum(),
                storeReview.getWriter().getNum(),
                StoreThumbnailResponse.of(storeReview.getWriter()),
                storeReview.getWriterName(),
                storeReview.getDealScore(),
                storeReview.getProductNum(),
                storeReview.getProductName(),
                storeReview.getReviewContent(),
                storeReview.getUpdatedDate().toLocalDate()
        );
    }

    public static List<StoreReviewResponse> listOf(List<StoreReview> storeReviews) {
        return storeReviews.stream()
                .map(StoreReviewResponse::of)
                .collect(Collectors.toList());
    }
}
