package com.amadich.colly.friend.service;

import com.amadich.colly.friend.dto.FriendRequestResponse;
import com.amadich.colly.friend.dto.FriendResponse;
import com.amadich.colly.friend.entity.FriendRequest;
import com.amadich.colly.friend.enums.FriendRequestStatus;
import com.amadich.colly.friend.repository.FriendRequestRepository;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public void sendFriendRequest(
            Authentication authentication,
            UUID receiverId
    ) {

        String email = authentication.getName();

        User sender = userRepository.findByEmail(email)
                .orElseThrow();

        User receiver = userRepository.findById(receiverId)
                .orElseThrow();

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot add yourself");
        }

        boolean alreadyExists =
                friendRequestRepository
                        .findBySenderAndReceiver(sender, receiver)
                        .isPresent();

        if (alreadyExists) {
            throw new RuntimeException("Request already exists");
        }

        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        friendRequestRepository.save(request);
    }

    public void acceptRequest(UUID requestId) {

        FriendRequest request =
                friendRequestRepository.findById(requestId)
                        .orElseThrow();

        request.setStatus(FriendRequestStatus.ACCEPTED);

        friendRequestRepository.save(request);
    }

    public List<FriendResponse> getFriends(Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow();

        List<FriendRequest> requests =
                friendRequestRepository.findAll();

        List<FriendResponse> friends = new ArrayList<>();

        for (FriendRequest request : requests) {

            if (request.getStatus() != FriendRequestStatus.ACCEPTED) {
                continue;
            }

            User friend = null;

            if (request.getSender().getId().equals(currentUser.getId())) {
                friend = request.getReceiver();
            }

            else if (request.getReceiver().getId().equals(currentUser.getId())) {
                friend = request.getSender();
            }

            if (friend != null) {

                friends.add(
                        FriendResponse.builder()
                                .id(friend.getId())
                                .firstName(friend.getFirstName())
                                .lastName(friend.getLastName())
                                .username(friend.getUsername())
                                .avatar(friend.getAvatar())
                                .build()
                );
            }
        }

        return friends;
    }

    public List<FriendRequestResponse> getPendingRequests(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow();

        List<FriendRequest> requests =
                friendRequestRepository.findAll();

        List<FriendRequestResponse> responses = new ArrayList<>();

        for (FriendRequest request : requests) {

            boolean isReceiver =
                    request.getReceiver()
                            .getId()
                            .equals(currentUser.getId());

            boolean isPending =
                    request.getStatus() == FriendRequestStatus.PENDING;

            if (isReceiver && isPending) {

                User sender = request.getSender();

                responses.add(
                        FriendRequestResponse.builder()
                                .id(request.getId())
                                .firstName(sender.getFirstName())
                                .lastName(sender.getLastName())
                                .username(sender.getUsername())
                                .avatar(sender.getAvatar())
                                .build()
                );
            }
        }

        return responses;
    }


    public void rejectRequest(UUID requestId) {

        FriendRequest request = friendRequestRepository.findById(requestId).orElseThrow();

        request.setStatus(FriendRequestStatus.REJECTED);

        friendRequestRepository.save(request);
    }
}