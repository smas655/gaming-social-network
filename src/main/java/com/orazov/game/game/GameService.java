package com.orazov.game.game;

import com.orazov.game.common.PageResponse;
import com.orazov.game.exception.OperationNotPermittedException;
import com.orazov.game.file.FileStorageService;
import com.orazov.game.history.GameTransactionHistory;
import com.orazov.game.history.GameTransactionHistoryRepository;
import com.orazov.game.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.orazov.game.game.GameSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final GameTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;
    public Integer save(GameRequest request, Authentication connectedUser) {

        User user = ((User) connectedUser.getPrincipal());
        Game game = gameMapper.toGame(request);
        game.setOwner(user);
        return gameRepository.save(game).getId();
    }

    public GameResponse findById(Integer bookId) {
        return gameRepository.findById(bookId)
                .map(gameMapper::toGameResponse)
                .orElseThrow(() -> new EntityNotFoundException("No Game found with the id: " + bookId));
    }

    public PageResponse<GameResponse> findAllGames(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Game> games = gameRepository.findAllDisplayableGames(pageable, user.getId());
        List<GameResponse> gameResponse = games.stream()
                .map(gameMapper::toGameResponse)
                .toList();
        return new PageResponse<>(
                gameResponse,
                games.getNumber(),
                games.getSize(),
                games.getTotalElements(),
                games.getTotalPages(),
                games.isFirst(),
                games.isLast()
        );
    }

    public PageResponse<GameResponse> findAllGamesByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Game> games = gameRepository.findAll(withOwnerId(user.getId()), pageable);

        List<GameResponse> gameResponse = games.stream()
                .map(gameMapper::toGameResponse)
                .toList();
        return new PageResponse<>(
                gameResponse,
                games.getNumber(),
                games.getSize(),
                games.getTotalElements(),
                games.getTotalPages(),
                games.isFirst(),
                games.isLast()
        );
    }

    public PageResponse<BorrowedGameResponse> findAllBorrowedGames(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<GameTransactionHistory> allBorrowedGames = transactionHistoryRepository.findAllBorrowedGames(pageable, user.getId());
        List<BorrowedGameResponse> gameResponse = allBorrowedGames.stream()
                .map(gameMapper::toBorrowedGameResponse)
                .toList();
        return new PageResponse<>(
                gameResponse,
                allBorrowedGames.getNumber(),
                allBorrowedGames.getSize(),
                allBorrowedGames.getTotalElements(),
                allBorrowedGames.getTotalPages(),
                allBorrowedGames.isFirst(),
                allBorrowedGames.isLast()
        );
    }

    public PageResponse<BorrowedGameResponse> findAllReturnedGames(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<GameTransactionHistory> allBorrowedGames = transactionHistoryRepository.findAllReturnedGames(pageable, user.getId());
        List<BorrowedGameResponse> gameResponse = allBorrowedGames.stream()
                .map(gameMapper::toBorrowedGameResponse)
                .toList();
        return new PageResponse<>(
                gameResponse,
                allBorrowedGames.getNumber(),
                allBorrowedGames.getSize(),
                allBorrowedGames.getTotalElements(),
                allBorrowedGames.getTotalPages(),
                allBorrowedGames.isFirst(),
                allBorrowedGames.isLast()
        );
    }

    public Integer updateShareableStatus(Integer gameId, Authentication connectedUser) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(()-> new EntityNotFoundException("No game found with the id: " + gameId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(game.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update games shareable status");
        }
        game.setShareable(!game.isShareable());
        gameRepository.save(game);
        return gameId;
    }

    public Integer updateArchivedStatus(Integer gameId, Authentication connectedUser) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(()-> new EntityNotFoundException("No game found with the id: " + gameId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(game.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update games archived status");
        }
        game.setArchived(!game.isArchived());
        gameRepository.save(game);
        return gameId;
    }

    public Integer borrowGame(Integer gameId, Authentication connectedUser) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("No game found with id: " + gameId));

        if (game.isArchived() || !game.isShareable()) {
            throw new OperationNotPermittedException("The requested game cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        // checking is the user is not the same as the owner
        if (Objects.equals(game.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own game");
        }

        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(gameId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested game is already borrowed");
        }
        GameTransactionHistory gameTransactionHistory = GameTransactionHistory.builder()
                .user(user)
                .game(game)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(gameTransactionHistory).getId();
    }

    public Integer returnBorrowedGame(Integer gameId, Authentication connectedUser) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("No game found with id: " + gameId));
        if (game.isArchived() || !game.isShareable()) {
            throw new OperationNotPermittedException("The requested game cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(game.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own game");
        }
       GameTransactionHistory gameTransactionHistory = transactionHistoryRepository.findByGameIdAndUserId(gameId, user.getId())
               .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this game"));
        gameTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(gameTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedGame(Integer gameId, Authentication connectedUser) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("No game found with id: " + gameId));
        if (game.isArchived() || !game.isShareable()) {
            throw new OperationNotPermittedException("The requested game cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(game.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own game");
        }
        GameTransactionHistory gameTransactionHistory = transactionHistoryRepository.findByGameIdAndOwnerId(gameId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The game is not returned yet. You cannot approve its return"));
        gameTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(gameTransactionHistory).getId();
    }

    public void uploadGameCoverPicture(MultipartFile file, Authentication connectedUser, Integer gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("No game found with id: " + gameId));
        User user = ((User) connectedUser.getPrincipal());
        var gameCover = fileStorageService.saveFile(file, user.getId());
        game.setGameCover(gameCover);
        gameRepository.save(game);
    }
}
