package com.orazov.game.history;

import com.orazov.game.game.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameTransactionHistoryRepository extends JpaRepository<GameTransactionHistory, Integer> {
    @Query("""
    select history
    from GameTransactionHistory history
    where history.user.id = :userId
            """)
    Page<GameTransactionHistory> findAllBorrowedGames(Pageable pageable, Integer userId);

    @Query("""
    select history
    from GameTransactionHistory history
    where history.game.owner.id = :userId
            """)
    Page<GameTransactionHistory> findAllReturnedGames(Pageable pageable, Integer userId);

    @Query("""
    select 
    (count(*) > 0) as isBorrowed
    from GameTransactionHistory gameTransactionHistory
    where gameTransactionHistory.user.id = :userId
    and gameTransactionHistory.game.id = :gameId
    and gameTransactionHistory.returnApproved = false
""")
    boolean isAlreadyBorrowedByUser(Integer gameId, Integer userId);

    @Query("""
    select transaction
    from GameTransactionHistory transaction
    where transaction.user.id = :userId
    and transaction.game.id = :gameId
    and transaction.returned = false
    and transaction.returnApproved = false
""")
    Optional<GameTransactionHistory> findByGameIdAndUserId(Integer gameId, Integer userId);

    @Query("""
    select transaction
    from GameTransactionHistory transaction
    where transaction.game.owner.id = :userId
    and transaction.game.id = :gameId
    and transaction.returned = true
    and transaction.returnApproved = false
""")
    Optional<GameTransactionHistory> findByGameIdAndOwnerId(Integer gameId, Integer userId);
}
