package me.pesekjak.phptr;


import org.jetbrains.annotations.Contract;

import java.util.*;

public class PhpDocBlock {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, List<DocTag>> tagsMap;

    protected PhpDocBlock(Map<String, List<DocTag>> tagsMap) {
        this.tagsMap = Map.copyOf(tagsMap);
    }

    public List<DocTag> getTagsByName(String tagName) {
        return tagsMap.getOrDefault(tagName, Collections.emptyList());
    }

    public static class Builder {

        private final Map<String, List<DocTag>> tagsMap = new LinkedHashMap<>();

        protected Builder() {}

        @Contract("_, _ -> this")
        public Builder add(String tagName, DocTag... tags) {
            Objects.requireNonNull(tagName, "Tag name can not be null");
            if (Arrays.stream(tags).anyMatch(Objects::isNull))
                throw new NullPointerException("DocTags can not be null");
            tagsMap.computeIfAbsent(tagName, _ -> new ArrayList<>()).addAll(List.of(tags));
            return this;
        }

        public PhpDocBlock build() {
            return new PhpDocBlock(tagsMap);
        }

    }

}
