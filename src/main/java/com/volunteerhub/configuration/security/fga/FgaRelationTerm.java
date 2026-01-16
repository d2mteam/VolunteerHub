package com.volunteerhub.configuration.security.fga;

import java.util.Objects;

public final class FgaRelationTerm {

    public enum Kind {
        DIRECT_USER,
        DIRECT_OBJECT,
        SAME_RELATION,
        TUPLE_TO_USERSET
    }

    private final Kind kind;
    private final String value;
    private final String targetRelation;

    private FgaRelationTerm(Kind kind, String value, String targetRelation) {
        this.kind = kind;
        this.value = value;
        this.targetRelation = targetRelation;
    }

    public static FgaRelationTerm directUser() {
        return new FgaRelationTerm(Kind.DIRECT_USER, null, null);
    }

    public static FgaRelationTerm directObject(String type) {
        return new FgaRelationTerm(Kind.DIRECT_OBJECT, type, null);
    }

    public static FgaRelationTerm sameRelation(String relation) {
        return new FgaRelationTerm(Kind.SAME_RELATION, relation, null);
    }

    public static FgaRelationTerm tupleToUserset(String objectRelation, String targetRelation) {
        return new FgaRelationTerm(Kind.TUPLE_TO_USERSET, objectRelation, targetRelation);
    }

    public Kind kind() {
        return kind;
    }

    public String value() {
        return value;
    }

    public String targetRelation() {
        return targetRelation;
    }

    public String objectRelation() {
        if (kind != Kind.TUPLE_TO_USERSET) {
            return null;
        }
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FgaRelationTerm term)) {
            return false;
        }
        return kind == term.kind
                && Objects.equals(value, term.value)
                && Objects.equals(targetRelation, term.targetRelation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, value, targetRelation);
    }

    @Override
    public String toString() {
        return "FgaRelationTerm{"
                + "kind=" + kind
                + ", value='" + value + '\''
                + ", targetRelation='" + targetRelation + '\''
                + '}';
    }
}
