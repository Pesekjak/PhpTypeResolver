package me.pesekjak.phptr;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TypeResolverTest {

    private final TypeResolver resolver = new TypeResolver();

    @Test
    void namelessTypeReturnsCorrectly() {
        // /** @var User */ for $user -> User
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("User"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$user");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.single("User"), result);
    }

    @Test
    void unionTypeReturnsUnionCorrectly() {
        // /** @var string|int */ for $id -> UnionType(string, int)
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("string|int"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$id");

        PhpType result = resolver.inferTypeFromDoc(variable);
        PhpType expected = PhpType.union(
                PhpType.single("string"),
                PhpType.single("int")
        );
        assertEquals(expected, result);
    }

    @Test
    void namedTagReturnsCorrectly() {
        // /** @var Logger $log */ for $log -> Logger
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("Logger $log"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$log");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.single("Logger"), result);
    }

    @Test
    void nameMismatchReturnsMixed() {
        // /** @var Admin $adm */ for $guest -> mixed
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("Admin $adm"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$guest");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.MIXED, result);
    }

    @Test
    void multipleTagsMatchesCorrectName() {
        // /** @var int $id */ and /** @var string $name */ for $name -> string
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("int $id"), new DocTag("string $name"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$name");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.single("string"), result);
    }

    @Test
    void nullDocBlockReturnsMixed() {
        PhpVariable variable = new PhpVariable(null, "$user");
        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.MIXED, result);
    }

    @Test
    void noVarTagsReturnsMixed() {
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("not var", new DocTag("int $id"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$user");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.MIXED, result);
    }

    @Test
    void exactMatchIsPreferred() {
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("string"), new DocTag("int $id"))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$id");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.single("int"), result);
    }

    @Test
    void blankTagIsIgnored() {
        PhpDocBlock docBlock = PhpDocBlock.builder()
                .add("var", new DocTag("   "))
                .build();
        PhpVariable variable = new PhpVariable(docBlock, "$user");

        PhpType result = resolver.inferTypeFromDoc(variable);
        assertEquals(PhpType.MIXED, result);
    }

    @Test
    void unionFlattensNestedUnions() {
        PhpType type1 = PhpType.single("string");
        PhpType type2 = PhpType.single("int");
        PhpType type3 = PhpType.single("double");

        PhpType.UnionType nestedUnion = PhpType.union(type1, new PhpType.UnionType(type2, type3));
        List<PhpType> innerTypes = nestedUnion.types();

        assertEquals(3, innerTypes.size());
        assertTrue(innerTypes.containsAll(List.of(type1, type2, type3)));
    }

}
