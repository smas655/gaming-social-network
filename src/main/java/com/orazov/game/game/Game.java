package com.orazov.game.game;

import com.orazov.game.common.BaseEntity;
import com.orazov.game.feedback.Feedback;
import com.orazov.game.history.GameTransactionHistory;
import com.orazov.game.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Game extends BaseEntity {

    private String title;
    private String publisherName;
    private String synopsis;
    private String gameCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne // many books can be assigned to one user
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "game")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "game")
    private List<GameTransactionHistory> histories;

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        // if (f.e) 3.23 --> 3.0 || 3.65 --> 4.00
        double roundedRate = Math.round(rate * 10.0) / 10.0;
        return roundedRate;
    }


}
