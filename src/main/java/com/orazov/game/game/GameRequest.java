package com.orazov.game.game;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GameRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String title,
        @NotNull(message = "101")
        @NotEmpty(message = "101")
        String publisherName,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String synopsis,
        boolean shareable

) {


}
