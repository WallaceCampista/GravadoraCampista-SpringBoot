package com.gravadoracampista.service.exceptions.allExceptions;

import java.util.Arrays;
import java.util.List;

public class CreateEntitiesException extends RuntimeException{
    public CreateEntitiesException(List<String> message) {
        super(Arrays.toString(message.toArray()));
    }
}