package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Напрямую использовать Repositories для получение данных на Пользовательский Интерфейс не принято и считается плохим тоном,
 * для этого были придуманы Services
 *
 * Service
 * – это Java класс, который предоставляет с себя основную (Бизнес-Логику).
 * В основном сервис использует готовые DAO/Repositories или же другие сервисы,
 * для того чтобы предоставить конечные данные для пользовательского интерфейса.
 *
 * здесь, мы указываем, какие методы нам будут нужны для написания бизнес-логики проекта.
 */

@Service
public class PlayerService {

    public final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * получать список всех зарегистрированных игроков
     *
     * Поиск по полям name и title происходить по частичному соответствию.
     * Например, если в БД есть игрок с именем «Камираж», а параметр name задан как «ир» -
     * такой игрок должен отображаться в результатах (Камираж).
     * pageNumber – параметр, который отвечает за номер отображаемой страницы при использовании пейджинга.
     * Нумерация начинается с нуля
     * pageSize – параметр, который отвечает за количество результатов на одной странице при пейджинге
     */
    public List<Player> getPlayerList(String name, String title, Race race, Profession profession,
                                      Long after, Long before, Boolean banned, Integer minExperience,
                                      Integer maxExperience, Integer minLevel, Integer maxLevel) {

        List<Player> playerList = new ArrayList<>();

        playerRepository.findAll().forEach(player -> {

            if (name != null && !player.getName().contains(name))
                return;
            if (title != null && !player.getTitle().contains(title))
                return;
            if (race != null && player.getRace() != race) {
                return;
            }
            if (profession != null && player.getProfession() != profession) {
                return;
            }

            if (after != null) {
                Date afterDate = new Date(after);
                if (player.getBirthday().before(afterDate)) {
                    return;
                }
            }
            if (before != null) {
                Date beforeDate = new Date(before);
                if (player.getBirthday().after(beforeDate)) {
                    return;
                }
            }

            if (banned != null && player.getBanned().booleanValue() != banned.booleanValue()) {
                return;
            }
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) {
                return;
            }
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) {
                return;
            }
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) {
                return;
            }
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) {
                return;
            }
            playerList.add(player);
        });

        return playerList;
    }

    // проверяем, что длина значения параметра “name” и "title" не превышает размер 12 и 30 соответсвенно
    // значения параметров “name” и "title" не пустая строка
    private boolean isValidName(String name) {
        return name.length() <= 12 && !name.isEmpty();
    }

    private boolean isValidTitle(String title) {
        return title.length() <= 30 && !title.isEmpty();
    }

    // проверяем все ли параметры указаны
    private boolean isValidParams(Player player) {
        return player.getName() != null ||
                player.getTitle() != null ||
                player.getRace() != null ||
                player.getProfession() != null ||
                player.getBirthday() != null ||
                player.getExperience() != null;
    }

    // проверяем, что опыт не находится вне заданных пределов
    private boolean isValidExperience(Integer experience) {
        // Опыт персонажа. Диапазон значений 0..10,000,000
        return experience >= 0 && experience <= 10000000;
    }

    // проверяем, что дата регистрации не находятся вне заданных пределов
    private boolean isValidDate(Date date) {
        if (date == null) {
            return false;
        }

        // Дата регистрации
        // Диапазон значений года 2000..3000 включительно
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) >= 2000 && calendar.get(Calendar.YEAR) <= 3000;
    }

    // текущий уровень персонажа
    private Integer calculateLevel(Player player) {
        return (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
    }

    // опыт необходимый для достижения следующего уровня
    private Integer calculateUntilNextLevel(Player player) {
        return 50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience();
    }

    /**
     * создать пользователя
     *
     * Мы не можем создать игрока, если:
     * - указаны не все параметры из Data Params (кроме banned);
     * - длина значения параметра “name” или “title” превышает размер соответствующего поля в БД (12 и 30 символов);
     * - значение параметра “name” пустая строка;
     * - опыт находится вне заданных пределов;
     * - “birthday”:[Long] < 0;
     * - дата регистрации находятся вне заданных пределов.
     * В случае всего вышеперечисленного необходимо ответить ошибкой с кодом 400.
     */

    public Player createPlayer(Player player) {

        // проверяем все ли параметры указаны
        if (isValidParams(player)
                // проверяем длину значений имени,
                && isValidName(player.getName())
                // титула
                && isValidTitle(player.getTitle())
                // проверяем, что опыт не находится вне заданных пределов
                && isValidExperience(player.getExperience())
                // проверяем, что дата регистрации не находятся вне заданных пределов
                && isValidDate(player.getBirthday())) {

            // высчитываем текущий уровень персонажа
            // и опыт необходимый для достижения следующего уровня
            player.setLevel(calculateLevel(player));
            player.setUntilNextLevel(calculateUntilNextLevel(player));

            return playerRepository.save(player);

        } else {
            throw new BadRequestException();
        }
    }

    /**
     * редактировать характеристики существующего игрока
     *
     * Обновлять нужно только те поля, которые не null.
     * Если игрок не найден в БД, необходимо ответить ошибкой с кодом 404.
     * Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
     */
    public Player updatePlayer(Long id, Player player) {

        // Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        if (id <= 0) {
            throw new BadRequestException();
        }

        // Если игрок не найден в БД, необходимо ответить ошибкой с кодом 404.
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException();
        }

        Player changedPlayer = playerRepository.findById(id).get();

        // Обновлять нужно только те поля, которые не null
        if (player.getName() != null) {
            changedPlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            changedPlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            changedPlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            changedPlayer.setProfession(player.getProfession());
        }
        if (player.getBirthday() != null) {
            if (isValidDate(player.getBirthday())) {
                changedPlayer.setBirthday(player.getBirthday());
            } else {
                throw new BadRequestException();
            }
        }

        if (player.getBanned() != null) {
            changedPlayer.setBanned(player.getBanned());
        }

        if (player.getExperience() != null) {
            if (isValidExperience(player.getExperience())) {
                changedPlayer.setExperience(player.getExperience());
            } else {
                throw new BadRequestException();
            }
        }

        changedPlayer.setLevel(calculateLevel(changedPlayer));
        changedPlayer.setUntilNextLevel(calculateUntilNextLevel(changedPlayer));

        return playerRepository.save(changedPlayer);
    }

    /**
     * удалять игрока
     *
     * Если игрок не найден в БД, необходимо ответить ошибкой с кодом 404.
     * Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
     */
    public void deleteById(Long id) {

        if (id <= 0) {
            throw new BadRequestException();
        }

        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException();
        }

        playerRepository.deleteById(id);
    }

    /**
     * получать игрока по id
     *
     * Если игрок не найден в БД, необходимо ответить ошибкой с кодом 404.
     * Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
     */
    public Player findById(Long id) {

        // Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        if (id <= 0) {
            throw new BadRequestException();
        }

        // Если игрок не найден в БД, необходимо ответить ошибкой с кодом 404.
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException();
        }

        return playerRepository.findById(id).get();
    }

    /**
     * сортировка списка в соответсвии с переданным параметром
     *
     * получать отфильтрованный список игроков в соответствии с переданными фильтрами
     */

    public List<Player> sortPlayers(List<Player> list, PlayerOrder order) {
        if (order != null) {
            switch (order) {
                case ID:
                    //list.sort(Comparator.comparing(Player -> Player.getId()));
                    list.sort(Comparator.comparing(Player::getId));
                    break;
                case NAME:
                    list.sort(Comparator.comparing(Player::getName));
                    break;
                case EXPERIENCE:
                    list.sort(Comparator.comparing(Player::getExperience));
                    break;
                case BIRTHDAY:
                    list.sort(Comparator.comparing(Player::getBirthday));
                    break;
            }
        }
        return list;
    }

    /**
     * сортировка страницы в соответсвии с переданными параметрами
     *
     * pageNumber – параметр, который отвечает за номер отображаемой страницы при использовании пейджингаъ
     * pageSize – параметр, который отвечает за количество результатов на одной странице при пейджинге
     */

    public List<Player> sortPage(List<Player> list, Integer pageNumber, Integer pageSize) {

        // start = номер отображаемой страницы (0)
        // * количество результатов на одной странице (3)
        int start = pageNumber * pageSize; // 0 * 3 = 0

        // end = start + количество результатов на одной странице
        int end = start + pageSize; // 0 + 3 = 3

        //
        if (end > list.size())
            end = list.size();

        return list.subList(start, end);
    }
}