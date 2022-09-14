package com.springchallenge.gamebackend.service.game;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springchallenge.gamebackend.dto.output.game.GameDto;
import com.springchallenge.gamebackend.exception.ExceptionType;
import com.springchallenge.gamebackend.exception.ExceptionsGenerator;
import com.springchallenge.gamebackend.model.Game;
import com.springchallenge.gamebackend.repository.GameRepository;
import com.springchallenge.gamebackend.util.CSVReader;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private CSVReader csvReader;
    @Autowired
    private GameRepository gameRepo;

    @Override
    @Transactional
    public void loadGamesFromCSV() {
        List<Game> newGames = csvReader.loadGamesFromCsv();
        saveGames(newGames);
    }

    @Override
    public void saveGames(List<Game> games) {
        try {
            gameRepo.saveAll(games);
        } catch (DataAccessException e) {
            throw ExceptionsGenerator.getException(ExceptionType.INVALID_OBJECT,
                    "The games could not be stored in the database.");
        }
    }

    @Override
    public GameDto getGameById(String id) {
        Optional<Game> game = gameRepo.findById(id);
        if (game.isPresent()) {
            ModelMapper mapper = new ModelMapper();
            GameDto foundGame = mapper.map(game.get(), GameDto.class);
            assignGameStatistics(foundGame);
            return foundGame;
        }
        throw ExceptionsGenerator.getException(ExceptionType.NOT_FOUND, "There is no game with the supplied id.");
    }

    private void assignGameStatistics(GameDto game) {
        game.setBacklogCount(gameRepo.countByState("BACKLOG"));
        game.setBeatCount(gameRepo.countByState("BEAT"));
        game.setRetiredCount(gameRepo.countByState("RETIRED"));
        game.setPlayingCount(gameRepo.countByState("PLAYING"));
    }

    public List<GameDto> getFilteredGames(int page, int limit, String sort, String title, String platform,
            String genre) {
        page = page - 1;
        Pageable newestGamesPagination = PageRequest.of(page, limit, Sort.by("updateAt").descending());
        System.out.printf("getFilteredGames page:%d, limit:%d, sort:%s, title:%s, platform:%s, genre:%s", page, limit,
                sort, title, platform, genre);
        ModelMapper mapper = new ModelMapper();
        List<GameDto> newestGamesDtos = gameRepo
                .findByPlatformOrGenreOrTitleLikeIgnoreCase(platform, genre, title, newestGamesPagination)
                .stream()
                .map((Game game) -> {
                    return mapper.map(game, GameDto.class);
                })
                .toList();
        return newestGamesDtos;
    }

}