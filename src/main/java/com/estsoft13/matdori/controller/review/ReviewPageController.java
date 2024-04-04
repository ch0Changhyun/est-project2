package com.estsoft13.matdori.controller.review;

import com.estsoft13.matdori.domain.Restaurant;
import com.estsoft13.matdori.domain.Review;
import com.estsoft13.matdori.domain.ReviewImage;
import com.estsoft13.matdori.domain.User;
import com.estsoft13.matdori.dto.comment.CommentResponseDto;
import com.estsoft13.matdori.dto.review.ReviewResponseDto;
import com.estsoft13.matdori.repository.CommentRepository;
import com.estsoft13.matdori.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ReviewPageController {
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    // review post - test용 컨트롤러입니다.
    @GetMapping("/add-review")
    public String showCreateReviewForm(@RequestParam(required = false) Long reviewId, Model model) {
        List<Restaurant> restaurants = restaurantService.findAll();
        model.addAttribute("restaurants", restaurants);

        if (reviewId == null) {
            model.addAttribute("review", new Review());
        } else {
            Optional<Review> reviewOptional = reviewService.findById(reviewId);
            Review review = reviewOptional.get();
            model.addAttribute("review", review);
        }

        return "review-form";
    }

    @GetMapping("/review/{reviewId}")
    public String showReviewDetail(@PathVariable Long reviewId, Model model, @AuthenticationPrincipal User user) {

        Optional<Review> reviewOptional = reviewService.findById(reviewId);

        if (!reviewOptional.isPresent()) {
            return "redirect:/reviews"; // 리뷰가 없으면 /reviews로 리다이렉션
        }
        Review review = reviewOptional.get();
        //조회수 up
        reviewService.countUpViewCount(reviewId);
        review.setViewCount(review.getViewCount()+1);

        model.addAttribute("review", review);

        List<ReviewImage> images = reviewImageService.findAllByReviewId(reviewId);
        model.addAttribute("images", images);

        List<CommentResponseDto> comments = commentService.getAllCommentsOfReview(reviewId);
        model.addAttribute("comments", comments);

        // 현재 로그인한 유저가 글을 등록한 유저인지 확인 후 글을 수정할 수 있게끔
        Long userId = user.getId();
        boolean isOwner = review.getUser().getId().equals(userId);
        model.addAttribute("isOwner", isOwner);

        if (!comments.isEmpty()) {
            comments.forEach(comment -> {
                boolean isCommentOwner = comment.getUserId().equals(userId);
                model.addAttribute("isCommentOwner", isCommentOwner); // CommentResponseDto에 isOwner 필드를 추가해야 합니다.
            });
        }

        return "detailedReviewPage";
    }

    @GetMapping("/reviews")
    public String showReviews(@RequestParam(required = false) String sort, Model model) {
   //List<Review> reviews = reviewService.findAll();
        List<Review> reviews;
        if("rating".equals(sort)) {
            reviews = reviewService.findAllByOrderByRatingDesc();
        } else if ("viewCount".equals(sort)) {
            reviews = reviewService.findAllByOrderByViewCountDesc();
        } else if ("latest".equals(sort)){
            reviews = reviewService.findAllByOrderByCreatedAtDesc();
        } else {
            reviews = reviewService.findAll();
        }

        List<ReviewResponseDto> responseDtoes = new ArrayList<>();
        for (Review review : reviews) {
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review);
            Long reviewId = review.getId();
            List<ReviewImage> reviewImages = reviewImageService.findAllByReviewId(reviewId);
            List<String> imgPaths = new ArrayList<>();
            for (ReviewImage reviewImage : reviewImages) {
                imgPaths.add(reviewImage.getImgPath());
            }
            reviewResponseDto.setImgPaths(imgPaths);
            responseDtoes.add(reviewResponseDto);
        }

        model.addAttribute("reviews", responseDtoes);

        return "review-main";
    }

    // 검색기능 ->  검색시 리뷰의 타이틀, 내용, 관련 식당에 키워드가 있으면 표시
    // /reviews/에서 검색 시 /search?keyword={keyword}로 이동
    @GetMapping("/searchReviews")
    public String searchByKeyword(@RequestParam("keyword") String keyword, Model model) {
        List<Review> searchResults = reviewService.findByTitleContainingOrContentContainingOrRestaurantNameContainingOrRestaurantCategoryContaining(keyword, keyword, keyword, keyword);
        List<ReviewResponseDto> responseDtoes = new ArrayList<>();
        for (Review review : searchResults) {
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review);
            Long reviewId = review.getId();
            List<ReviewImage> reviewImages = reviewImageService.findAllByReviewId(reviewId);
            List<String> imgPaths = new ArrayList<>();
            for (ReviewImage reviewImage : reviewImages) {
                imgPaths.add(reviewImage.getImgPath());
            }
            reviewResponseDto.setImgPaths(imgPaths);
            responseDtoes.add(reviewResponseDto);
        }
        model.addAttribute("reviews", responseDtoes);
        model.addAttribute("keyword", keyword);

        return "review-main";
    }

}