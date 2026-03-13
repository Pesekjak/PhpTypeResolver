package me.pesekjak.phptr;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TypeResolver {

    private static final String TAGS_NAME = "var";

    public PhpType inferTypeFromDoc(PhpVariable variable) {
        Objects.requireNonNull(variable, "Variable can not be null");

        if (variable.docBlock() == null)
            return PhpType.MIXED;

        List<DocTag> tags = variable.docBlock().getTagsByName(TAGS_NAME);

        if (tags == null || tags.isEmpty())
            return PhpType.MIXED;

        String targetName = variable.name();
        String namelessType = null;

        for (DocTag tag : tags) {
            String value = tag.value().trim();

            if (value.isBlank())
                continue;

            String[] parts = value.split("\\s+");
            String type = parts[0];

            String tagVarName = parts.length > 1 && parts[1].startsWith("$")
                    ? parts[1]
                    : null;

            if (targetName.equals(tagVarName)) {
                return parseType(type);
            } else if (tagVarName == null && namelessType == null) {
                namelessType = type;
            }
        }

        if (namelessType != null) {
            return parseType(namelessType);
        }

        return PhpType.MIXED;
    }

    private static PhpType parseType(String type) {
        if (!type.contains("|"))
            return TypeFactory.createType(type);

        List<PhpType> unionTypes = Arrays.stream(type.split("\\|"))
                .map(TypeFactory::createType)
                .toList();

        return TypeFactory.createUnionType(unionTypes);
    }

}
