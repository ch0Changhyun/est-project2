package com.estsoft13.matdori.controller;

import com.estsoft13.matdori.domain.Meeting;
import com.estsoft13.matdori.domain.Restaurant;
import com.estsoft13.matdori.domain.User;
import com.estsoft13.matdori.dto.CommentResponseDto;
import com.estsoft13.matdori.dto.MeetingResponseDto;
import com.estsoft13.matdori.service.CommentService;
import com.estsoft13.matdori.service.MeetingService;
import com.estsoft13.matdori.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequiredArgsConstructor
@Controller
public class MeetingPageController {
    private final MeetingService meetingService;
    private final RestaurantService restaurantService;
    private final CommentService commentService;


    @GetMapping("/add-meeting")
    public String showCreateMeetingForm(@RequestParam(required = false) Long meetingId, Model model) {
        List<Restaurant> restaurants = restaurantService.findAll();
        model.addAttribute("restaurants", restaurants);

        if(meetingId == null) {
            model.addAttribute("meeting", new Meeting());
        } else {
            MeetingResponseDto meeting = meetingService.findById(meetingId);
            model.addAttribute("meeting", meeting);
        }

        return "meetingForm";
    }

    @GetMapping("/meeting/{meetingId}")
    public String showMeetingDetail(@PathVariable Long meetingId, Model model, @AuthenticationPrincipal User user) {
        //Review review = reviewService.findById(reviewId);
        MeetingResponseDto responseDto = meetingService.findById(meetingId);
        model.addAttribute("meeting", responseDto);

        List<CommentResponseDto> comments = commentService.getAllCommentsOfMeeting(meetingId);
        model.addAttribute("comments", comments);

        // 현재 로그인한 유저가 글을 등록한 유저인지 확인 후 글을 수정할 수 있게끔
        Long userId = user.getId();
        boolean isOwner = responseDto.getId().equals(userId);
        model.addAttribute("isOwner", isOwner);

        return "detailedMeetingPage";
    }
}