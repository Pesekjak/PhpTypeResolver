package me.pesekjak.phptr;

import java.util.Objects;

public record DocTag(String value) {

    public DocTag {
        Objects.requireNonNull(value, "Value of doc tag can not be null");
    }

}
