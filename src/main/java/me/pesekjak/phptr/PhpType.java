package me.pesekjak.phptr;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public sealed interface PhpType {

    static SingleType single(String name) {
        return new SingleType(name);
    }

    static UnionType union(PhpType... types) {
        return new UnionType(types);
    }

    PhpType MIXED = new SingleType("mixed");

    record SingleType(String name) implements PhpType {
        public SingleType {
            Objects.requireNonNull(name, "Name of a single type can not be null");
            name = name.trim();
        }
    }

    record UnionType(@Unmodifiable List<PhpType> types) implements PhpType {

        public UnionType(PhpType... types) {
            this(List.of(types));
        }

        public UnionType {
            List<PhpType> union = new ArrayList<>();
            if (types == null) types = Collections.emptyList();
            for (PhpType type : types) {
                switch (type) {
                    case SingleType _ -> union.add(type);
                    case UnionType(List<PhpType> lst) -> union.addAll(lst);
                }
            }
            types = union;
        }

    }

}
