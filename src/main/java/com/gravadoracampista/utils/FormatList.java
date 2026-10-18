package com.gravadoracampista.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormatList {

    public static List<String> deleteBrackets(String string) {

        String cleanedMessage = string.replace("[", "").replace("]", "");

        String[] errorsArray = cleanedMessage.split(", ");
        List<String> errorsList = new ArrayList<>(Arrays.asList(errorsArray));

        return errorsList;
    }
}