package com.volunteerhub.configuration.security.fga;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FgaAuthorizationEngine {

    private final FgaModel model;
    private final FgaTupleResolver tupleResolver;

    public FgaAuthorizationEngine(FgaModel model, FgaTupleResolver tupleResolver) {
        this.model = model;
        this.tupleResolver = tupleResolver;
    }

    public boolean check(UUID userId, String objectType, String objectId, String relation) {
        if (!model.hasType(objectType)) {
            return false;
        }
        return checkInternal(userId, objectType, objectId, relation, new HashSet<>());
    }

    private boolean checkInternal(
            UUID userId,
            String objectType,
            String objectId,
            String relation,
            Set<String> visited
    ) {
        String key = objectType + ":" + objectId + "#" + relation + ":" + userId;
        if (!visited.add(key)) {
            return false;
        }

        List<FgaRelationTerm> terms = model.relationTerms(objectType, relation);
        for (FgaRelationTerm term : terms) {
            if (matchesTerm(userId, objectType, objectId, relation, term, visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesTerm(
            UUID userId,
            String objectType,
            String objectId,
            String relation,
            FgaRelationTerm term,
            Set<String> visited
    ) {
        return switch (term.kind()) {
            case DIRECT_USER -> tupleResolver.hasUserRelation(objectType, objectId, relation, userId);
            case SAME_RELATION -> checkInternal(userId, objectType, objectId, term.value(), visited);
            case TUPLE_TO_USERSET -> resolveTupleToUserset(userId, objectType, objectId, term, visited);
            case DIRECT_OBJECT -> false;
        };
    }

    private boolean resolveTupleToUserset(
            UUID userId,
            String objectType,
            String objectId,
            FgaRelationTerm term,
            Set<String> visited
    ) {
        Optional<FgaObjectRef> target = tupleResolver.resolveObjectRelation(
                objectType,
                objectId,
                term.objectRelation()
        );
        return target
                .map(ref -> checkInternal(userId, ref.type(), ref.id(), term.targetRelation(), visited))
                .orElse(false);
    }
}
