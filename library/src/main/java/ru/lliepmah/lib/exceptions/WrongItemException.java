package ru.lliepmah.lib.exceptions;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class WrongItemException extends RuntimeException {
    public WrongItemException(String message) {
        super(message);
    }
}
