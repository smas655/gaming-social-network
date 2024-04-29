package com.orazov.game.game;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedGameResponse {

    private Integer id;
    private String title;
    private String publisherName;
    private double rate;
    private boolean returned;
    private boolean returnApproved;
}
