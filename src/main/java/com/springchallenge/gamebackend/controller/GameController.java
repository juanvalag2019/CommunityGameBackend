package com.springchallenge.gamebackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springchallenge.gamebackend.dto.input.game.GameFilterCriteria;
import com.springchallenge.gamebackend.dto.output.game.GameDto;
import com.springchallenge.gamebackend.service.game.GameService;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGame(@PathVariable String id) {
        GameDto game = gameService.getGameById(id);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping("/load")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void loadGamesFromCSV() {
        gameService.loadGamesFromCSV();
    }

    @GetMapping("/")
    public ResponseEntity<List<GameDto>> getGames(GameFilterCriteria filter) {
        System.out.println(filter);
        return new ResponseEntity<>(
                gameService.getFilteredGames(filter.getPage(), filter.getLimit(), filter.getSort(), filter.getTitle(),
                        filter.getPlatform(), filter.getGenre()),
                HttpStatus.OK);
    }
}
