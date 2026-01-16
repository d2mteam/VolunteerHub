package com.volunteerhub.configuration.security.fga;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FgaModel {

    private final Map<String, Map<String, List<FgaRelationTerm>>> definitions;

    public FgaModel(Map<String, Map<String, List<FgaRelationTerm>>> definitions) {
        this.definitions = Collections.unmodifiableMap(definitions);
    }

    public List<FgaRelationTerm> relationTerms(String type, String relation) {
        Map<String, List<FgaRelationTerm>> relations = definitions.get(type);
        if (relations == null) {
            return List.of();
        }
        return relations.getOrDefault(relation, List.of());
    }

    public boolean hasType(String type) {
        return definitions.containsKey(type);
    }

    public Map<String, Map<String, List<FgaRelationTerm>>> definitions() {
        return definitions;
    }
}
