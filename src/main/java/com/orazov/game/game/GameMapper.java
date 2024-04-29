package com.orazov.game.game;

import com.orazov.game.file.FileUtils;
import com.orazov.game.history.GameTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class GameMapper {
    public Game toGame(GameRequest request) {

        return Game.builder()
                .id(request.id())
                .title(request.title())
                .publisherName(request.publisherName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public GameResponse toGameResponse(Game game) {

        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .publisherName(game.getPublisherName())
                .synopsis(game.getSynopsis())
                .rate(game.getRate())
                .archived(game.isArchived())
                .shareable(game.isShareable())
                .owner(game.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(game.getGameCover()))
                .build();
    }

    public BorrowedGameResponse toBorrowedGameResponse(GameTransactionHistory history) {
        return BorrowedGameResponse.builder()
                .id(history.getGame().getId())
                .title(history.getGame().getTitle())
                .publisherName(history.getGame().getPublisherName())
                .rate(history.getGame().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
