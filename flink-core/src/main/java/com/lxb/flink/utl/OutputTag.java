package com.lxb.flink.utl;

import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.java.typeutils.TypeExtractor;

public class OutputTag<T> {
    private static final long serialVersionUID = 2L;

    private final String id;

    private final TypeInformation<T> typeInfo;

    /**
     * Creates a new named {@code OutputTag} with the given id.
     *
     * @param id The id of the created {@code OutputTag}.
     */
    public OutputTag(String id) {
        Preconditions.checkNotNull(id, "OutputTag id cannot be null.");
        Preconditions.checkArgument(!id.isEmpty(), "OutputTag id must not be empty.");
        this.id = id;

        try {
            this.typeInfo = TypeExtractor.createTypeInfo(this, OutputTag.class, getClass(), 0);
        }
        catch (InvalidTypesException e) {
            throw new InvalidTypesException("Could not determine TypeInformation for the OutputTag type. " +
                    "The most common reason is forgetting to make the OutputTag an anonymous inner class. " +
                    "It is also not possible to use generic type variables with OutputTags, such as 'Tuple2<A, B>'.", e);
        }
    }

    /**
     * Creates a new named {@code OutputTag} with the given id and output {@link TypeInformation}.
     *
     * @param id The id of the created {@code OutputTag}.
     * @param typeInfo The {@code TypeInformation} for the side output.
     */
    public OutputTag(String id, TypeInformation<T> typeInfo) {
        Preconditions.checkNotNull(id, "OutputTag id cannot be null.");
        Preconditions.checkArgument(!id.isEmpty(), "OutputTag id must not be empty.");
        this.id = id;
        this.typeInfo = Preconditions.checkNotNull(typeInfo, "TypeInformation cannot be null.");
    }

    // ------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public TypeInformation<T> getTypeInfo() {
        return typeInfo;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OutputTag
                && ((OutputTag) obj).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "OutputTag(" + getTypeInfo() + ", " + id + ")";
    }
}
