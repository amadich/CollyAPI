package com.amadich.colly.friend.controller;

import com.amadich.colly.friend.dto.FriendRequestResponse;
import com.amadich.colly.friend.dto.FriendResponse;
import com.amadich.colly.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request/{receiverId}")
    public void sendRequest(Authentication authentication, @PathVariable UUID receiverId) {

        friendService.sendFriendRequest(
                authentication,
                receiverId
        );
    }

    @PutMapping("/accept/{requestId}")
    public void acceptRequest(@PathVariable UUID requestId) {

        friendService.acceptRequest(requestId);
    }

    @GetMapping
    public List<FriendResponse> getFriends(Authentication authentication) {

        return friendService.getFriends(authentication);
    }

    @GetMapping("/requests")
    public List<FriendRequestResponse> getPendingRequests(
            Authentication authentication
    ) {

        return friendService.getPendingRequests(authentication);
    }

    @PutMapping("/reject/{requestId}")
    public void rejectRequest(@PathVariable UUID requestId) {

        friendService.rejectRequest(requestId);
    }
}