package com.orazov.game.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<Game, Integer>, JpaSpecificationExecutor<Game> {

    @Query("""
    SELECT game
    from Game game
    where game.archived = false
    and game.shareable = true 
    and game.owner.id != :userId
"""
    )
    Page<Game> findAllDisplayableGames(Pageable pageable, Integer userId);
}
