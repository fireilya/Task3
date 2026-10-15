package ru.Perminov_Ilya.NauJava.entities;

import lombok.Getter;
import lombok.Setter;
import ru.Perminov_Ilya.NauJava.entities.enums.ChatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;

@Getter
@Setter
@Entity
@Table(
        name = "direct_chats",
        check = {@CheckConstraint(constraint = "user_a_id <> user_b_id")})
@DiscriminatorValue(ChatType.StringValues.DIRECT)
public class DirectChat extends ChatBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;

    @AssertTrue(message = "Direct chat users must be different")
    public boolean isUsersDistinct() {
        return userA != null
                && userB != null
                && userA.getId() != null
                && userB.getId() != null
                && !userA.getId().equals(userB.getId());
    }
}
