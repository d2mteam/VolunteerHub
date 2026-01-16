package com.volunteerhub.configuration.security.fga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FgaDslParser {

    public FgaModel parse(String dsl) {
        Set<String> typeNames = collectTypes(dsl);
        Map<String, Map<String, List<FgaRelationTerm>>> definitions = new HashMap<>();
        String currentType = null;

        for (String rawLine : dsl.split("\n")) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("type ")) {
                currentType = line.substring("type ".length()).trim();
                definitions.putIfAbsent(currentType, new HashMap<>());
                continue;
            }
            if (currentType == null || line.equals("relations") || line.equals("relations:")) {
                continue;
            }
            if (!line.contains(":")) {
                continue;
            }
            String[] parts = line.split(":", 2);
            String relationName = parts[0].trim();
            String expression = parts[1].trim();
            List<FgaRelationTerm> terms = parseExpression(expression, typeNames);
            definitions.get(currentType).put(relationName, terms);
        }

        return new FgaModel(definitions);
    }

    private Set<String> collectTypes(String dsl) {
        Set<String> types = new HashSet<>();
        for (String rawLine : dsl.split("\n")) {
            String line = rawLine.trim();
            if (line.startsWith("type ")) {
                types.add(line.substring("type ".length()).trim());
            }
        }
        return types;
    }

    private List<FgaRelationTerm> parseExpression(String expression, Set<String> typeNames) {
        if (expression.isEmpty()) {
            return List.of();
        }
        String normalized = expression.replaceAll("\\s+or\\s+", " | ");
        String[] tokens = normalized.split("\\|");
        List<FgaRelationTerm> terms = new ArrayList<>();
        for (String rawToken : tokens) {
            String token = rawToken.trim();
            if (token.isEmpty()) {
                continue;
            }
            if (token.equals("user")) {
                terms.add(FgaRelationTerm.directUser());
                continue;
            }
            if (token.contains(".")) {
                String[] parts = token.split("\\.", 2);
                String objectRelation = parts[0].trim();
                String targetRelation = parts[1].trim();
                terms.add(FgaRelationTerm.tupleToUserset(objectRelation, targetRelation));
                continue;
            }
            if (typeNames.contains(token)) {
                terms.add(FgaRelationTerm.directObject(token));
                continue;
            }
            terms.add(FgaRelationTerm.sameRelation(token));
        }
        return terms;
    }
}
