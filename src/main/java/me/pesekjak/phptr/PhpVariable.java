package me.pesekjak.phptr;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record PhpVariable(@Nullable PhpDocBlock docBlock, String name) {

    public PhpVariable {
        Objects.requireNonNull(name, "Name of variable can not be null");
    }

}
