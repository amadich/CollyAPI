package com.amadich.colly.friend.repository;

import com.amadich.colly.friend.entity.FriendRequest;
import com.amadich.colly.friend.enums.FriendRequestStatus;
import com.amadich.colly.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

    List<FriendRequest> findByReceiverAndStatus(
            User receiver,
            FriendRequestStatus status
    );

    List<FriendRequest> findBySenderAndStatus(
            User sender,
            FriendRequestStatus status
    );

    Optional<FriendRequest> findBySenderAndReceiver(
            User sender,
            User receiver
    );
}