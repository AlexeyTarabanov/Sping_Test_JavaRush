package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 Controller
 - обрабатывает запросы от пользователя
 - обменивается данными с моделью (сущностью)
 - показывает пользователю правильное представление
 - переадресовывает пользователя на другие стараницы

 Маппинги связывают метод контроллера с тем адресом, по которому можно к этому методу обратиться.
 */

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "race", required = false) Race race,
                                       @RequestParam(value = "profession", required = false) Profession profession,
                                       @RequestParam(value = "after", required = false) Long after,
                                       @RequestParam(value = "before", required = false) Long before,
                                       @RequestParam(value = "banned", required = false) Boolean banned,
                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                       @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                       @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {

        List<Player> playerList = playerService.getPlayerList(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);
        List<Player> sortedPlayers = playerService.sortPlayers(playerList, order);

        return playerService.sortPage(sortedPlayers, pageNumber, pageSize);
    }

    // 7. получать количество игроков, которые соответствуют фильтрам
    @GetMapping("players/count")
    public Integer getPlayersCount(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel
    ) {

        return playerService.getPlayerList(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel).size();
    }

    // 2. создавать нового игрока
    @PostMapping ("/players")
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    // 5. получать игрока по id;
    @GetMapping ("players/{id}")
    public Player getPlayer(@PathVariable(value = "id") Long id) {
        return playerService.findById(id);
    }

    // 3. редактировать характеристики существующего игрока
    @PostMapping("players/{id}")
    public Player updatePlayer(@PathVariable(value = "id") Long id,
                               // Значения параметров преобразуются в объявленный тип аргумента метода
                               @RequestBody Player player) {
        return playerService.updatePlayer(id, player);
    }

    // 4. удалять игрока
    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable(value = "id") Long id) {
        playerService.deleteById(id);
    }
}