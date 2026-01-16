package com.volunteerhub.configuration.security.fga;

import java.util.Optional;
import java.util.UUID;

public interface FgaTupleResolver {

    boolean hasUserRelation(String objectType, String objectId, String relation, UUID userId);

    Optional<FgaObjectRef> resolveObjectRelation(String objectType, String objectId, String relation);
}
