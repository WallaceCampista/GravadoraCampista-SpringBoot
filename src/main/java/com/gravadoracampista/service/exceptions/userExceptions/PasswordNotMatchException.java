package com.gravadoracampista.service.exceptions.userExceptions;

public class PasswordNotMatchException extends RuntimeException{
    public PasswordNotMatchException() {
        super("'Senha' e 'confirma Senha' não são iguais.");
    }
}