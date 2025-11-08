package com.prm392.be.labverse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamErrorCode {

    // from 7001
    MEMBER_NOT_FOUND(7001, "Member not found in this team", HttpStatus.BAD_REQUEST),
    TEAM_NOT_FOUND(7002, "Team not found", HttpStatus.NOT_FOUND),
    TEAM_NO_PERMISSION(7003, "You don't have permission to manage this team", HttpStatus.FORBIDDEN),
    TEAM_CANNOT_REMOVE_SELF(7004, "You cannot remove yourself from the team", HttpStatus.BAD_REQUEST),

    TEAM_READING_LIST_NOT_FOUND(7005, "Team reading list not found", HttpStatus.NOT_FOUND),
    TEAM_READING_LIST_NOT_IN_TEAM(7006, "Team reading list does not belong to this team", HttpStatus.BAD_REQUEST),
    TEAM_READING_LIST_DELETED(7007, "Team reading list has been deleted", HttpStatus.BAD_REQUEST),

    MEMBER_ALREADY_IN_TEAM(7008, "This user is already a member of this team", HttpStatus.BAD_REQUEST),
    INVITATION_EXPIRED(7009, "Invitation has expired", HttpStatus.BAD_REQUEST),
    INVITATION_INVALID(7010, "Invitation is no longer valid", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
