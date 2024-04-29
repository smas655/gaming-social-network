package com.orazov.game.history;

import com.orazov.game.common.BaseEntity;
import com.orazov.game.game.Game;
import com.orazov.game.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GameTransactionHistory extends BaseEntity {

    // user relationship
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    // game relationship
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    private boolean returned;
    private boolean returnApproved;

}
