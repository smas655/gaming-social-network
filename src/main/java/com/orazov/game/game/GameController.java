package com.orazov.game.game;

import com.orazov.game.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("games")
@RequiredArgsConstructor
@Tag(name = "Game")
public class GameController {

    private final GameService service;

    @PostMapping
    public ResponseEntity<Integer> saveGame(
        @Valid @RequestBody GameRequest request,
        Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("{game-id}")
    public ResponseEntity<GameResponse> findGameById(
            @PathVariable("game-id") Integer gameId
    ) {
        return ResponseEntity.ok(service.findById(gameId));
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<GameResponse>> findAllGames(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllGames(page, size ,connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<GameResponse>> findAllGamesByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllGamesByOwner(page, size ,connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedGameResponse>> findAllBorrowedGames(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBorrowedGames(page, size ,connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedGameResponse>> findAllReturnedGames(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllReturnedGames(page, size ,connectedUser));
    }

    @PatchMapping("/shareable/{game-id}")
    public ResponseEntity<Integer> updateShareableStatus(
        @PathVariable("game-id") Integer gameId,
        Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateShareableStatus(gameId, connectedUser));
    }

    @PatchMapping("/archived/{game-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("game-id") Integer gameId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateArchivedStatus(gameId, connectedUser));
    }

    @PostMapping("/borrow/{game-id}")
    public ResponseEntity<Integer> borrowGame(
            @PathVariable("game-id") Integer gameId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.borrowGame(gameId, connectedUser));
    }

    @PatchMapping("/borrow/return/{game-id}")
    public ResponseEntity<Integer> returnBorrowGame(
            @PathVariable("game-id") Integer gameId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.returnBorrowedGame(gameId, connectedUser));
    }

    @PatchMapping("/borrow/return/approve/{game-id}")
    public ResponseEntity<Integer> approveReturnBorrowGame(
            @PathVariable("game-id") Integer gameId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.approveReturnBorrowedGame(gameId, connectedUser));
    }

    @PostMapping(value = "/cover/{game-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadGameCoverPicture(
            @PathVariable("game-id") Integer gameId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        service.uploadGameCoverPicture(file, connectedUser, gameId);
        return ResponseEntity.accepted().build();
    }

}
