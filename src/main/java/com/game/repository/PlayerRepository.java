package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 Repository -
 интерфейс обеспечивает доступ к данным (БД),
 позволяет оперировать объектом в БД

 JpaRepository –
 интерфейс фреймворка Spring Data, предоставляющий набор стандартных методов JPA для работы с БД.
 Он параметризованный:
 параметр 1 - сущность
 параметр 2 - id

 */

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
