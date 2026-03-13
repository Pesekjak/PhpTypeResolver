package me.pesekjak.phptr;

import java.util.List;

public final class TypeFactory {

    public static PhpType createType(String typeName) {
        return new PhpType.SingleType(typeName);
    }

    public static PhpType createUnionType(List<PhpType> types) {
        return new PhpType.UnionType(types);
    }

    private TypeFactory() {
        throw new UnsupportedOperationException();
    }

}
