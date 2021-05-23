package com.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// ошибка 404
// код ответа сервера, который сообщает пользователю, что сервер не может найти запрашиваемые данные
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException() {}

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
