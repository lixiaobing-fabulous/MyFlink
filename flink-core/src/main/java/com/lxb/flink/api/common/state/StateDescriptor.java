package com.lxb.flink.api.common.state;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.api.java.typeutils.TypeExtractor;
import com.lxb.flink.core.memory.DataInputViewStreamWrapper;
import com.lxb.flink.core.memory.DataOutputViewStreamWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import static com.lxb.flink.utl.Preconditions.checkNotNull;
import static com.lxb.flink.utl.Preconditions.checkState;

public abstract class StateDescriptor<S extends State, T> implements Serializable {

    public enum Type {
        UNKNOWN,
        VALUE,
        LIST,
        REDUCING,
        FOLDING,
        AGGREGATING,
        MAP
    }

    protected final     String                             name;
    private final       AtomicReference<TypeSerializer<T>> serializerAtomicReference = new AtomicReference<>();
    private             TypeInformation<T>                 typeInfo;
    protected transient T                                  defaultValue;

    protected StateDescriptor(String name, TypeSerializer<T> serializer, T defaultValue) {
        this.name = checkNotNull(name, "name must not be null");
        this.serializerAtomicReference.set(checkNotNull(serializer, "serializer must not be null"));
        this.defaultValue = defaultValue;
    }

    protected StateDescriptor(String name, TypeInformation<T> typeInfo, T defaultValue) {
        this.name = checkNotNull(name, "name must not be null");
        this.typeInfo = checkNotNull(typeInfo, "type information must not be null");
        this.defaultValue = defaultValue;
    }

    protected StateDescriptor(String name, Class<T> type, T defaultValue) {
        this.name = checkNotNull(name, "name must not be null");
        checkNotNull(type, "type class must not be null");

        try {
            this.typeInfo = TypeExtractor.createTypeInfo(type);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not create the type information for '" + type.getName() + "'. " +
                            "The most common reason is failure to infer the generic type information, due to Java's type erasure. " +
                            "In that case, please pass a 'TypeHint' instead of a class to describe the type. " +
                            "For example, to describe 'Tuple2<String, String>' as a generic type, use " +
                            "'new PravegaDeserializationSchema<>(new TypeHint<Tuple2<String, String>>(){}, serializer);'", e);
        }

        this.defaultValue = defaultValue;
    }


    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        if (defaultValue != null) {
            TypeSerializer<T> serializer = serializerAtomicReference.get();
            if (serializer != null) {
                return serializer.copy(defaultValue);
            } else {
                throw new IllegalStateException("Serializer not yet initialized.");
            }
        } else {
            return null;
        }
    }

    public TypeSerializer<T> getSerializer() {
        TypeSerializer<T> serializer = serializerAtomicReference.get();
        if (serializer != null) {
            return serializer.duplicate();
        } else {
            throw new IllegalStateException("Serializer not yet initialized.");
        }
    }

    final TypeSerializer<T> getOriginalSerializer() {
        TypeSerializer<T> serializer = serializerAtomicReference.get();
        if (serializer != null) {
            return serializer;
        } else {
            throw new IllegalStateException("Serializer not yet initialized.");
        }
    }

    public boolean isSerializerInitialized() {
        return serializerAtomicReference.get() != null;
    }

    public void initializeSerializerUnlessSet(ExecutionConfig executionConfig) {
        if (serializerAtomicReference.get() == null) {
            checkState(typeInfo != null, "no serializer and no type info");
            // try to instantiate and set the serializer
            TypeSerializer<T> serializer = typeInfo.createSerializer(executionConfig);
            // use cas to assure the singleton
            if (!serializerAtomicReference.compareAndSet(null, serializer)) {
                System.out.println("Someone else beat us at initializing the serializer.");
            }
        }
    }

    public final int hashCode() {
        return name.hashCode() + 31 * getClass().hashCode();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o != null && o.getClass() == this.getClass()) {
            final StateDescriptor<?, ?> that = (StateDescriptor<?, ?>) o;
            return this.name.equals(that.name);
        } else {
            return false;
        }
    }

    public String toString() {
        return getClass().getSimpleName() +
                "{name=" + name +
                ", defaultValue=" + defaultValue +
                ", serializer=" + serializerAtomicReference.get() +
                '}';
    }

    public abstract Type getType();

    private void writeObject(final ObjectOutputStream out) throws IOException {
        // write all the non-transient fields
        out.defaultWriteObject();

        // write the non-serializable default value field
        if (defaultValue == null) {
            // we don't have a default value
            out.writeBoolean(false);
        } else {
            TypeSerializer<T> serializer = serializerAtomicReference.get();
            checkNotNull(serializer, "Serializer not initialized.");

            // we have a default value
            out.writeBoolean(true);

            byte[] serializedDefaultValue;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 DataOutputViewStreamWrapper outView = new DataOutputViewStreamWrapper(baos)) {

                TypeSerializer<T> duplicateSerializer = serializer.duplicate();
                duplicateSerializer.serialize(defaultValue, outView);

                outView.flush();
                serializedDefaultValue = baos.toByteArray();
            } catch (Exception e) {
                throw new IOException("Unable to serialize default value of type " +
                        defaultValue.getClass().getSimpleName() + ".", e);
            }

            out.writeInt(serializedDefaultValue.length);
            out.write(serializedDefaultValue);
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        // read the non-transient fields
        in.defaultReadObject();

        // read the default value field
        boolean hasDefaultValue = in.readBoolean();
        if (hasDefaultValue) {
            TypeSerializer<T> serializer = serializerAtomicReference.get();
            checkNotNull(serializer, "Serializer not initialized.");

            int size = in.readInt();

            byte[] buffer = new byte[size];

            in.readFully(buffer);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                 DataInputViewStreamWrapper inView = new DataInputViewStreamWrapper(bais)) {

                defaultValue = serializer.deserialize(inView);
            } catch (Exception e) {
                throw new IOException("Unable to deserialize default value.", e);
            }
        } else {
            defaultValue = null;
        }
    }

}
