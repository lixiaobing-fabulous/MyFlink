package com.lxb.flink.api.dag;

import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.common.operators.ResourceSpec;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.java.typeutils.MissingTypeInfo;
import com.lxb.flink.utl.Preconditions;

import java.util.Collection;

import static com.lxb.flink.utl.Preconditions.checkArgument;
import static com.lxb.flink.utl.Preconditions.checkNotNull;

public abstract class Transformation<T> {
    public static final int UPPER_BOUND_MAX_PARALLELISM = 1 << 15;

    public static final int DEFAULT_MANAGED_MEMORY_WEIGHT = 1;

    protected static Integer idCounter = 0;

    public static int getNewNodeId() {
        idCounter++;
        return idCounter;
    }

    protected final int id;

    protected String name;

    protected TypeInformation<T> outputType;
    protected boolean            typeUsed;

    private int          parallelism;
    private int          maxParallelism      = -1;
    private ResourceSpec minResources        = ResourceSpec.DEFAULT;
    private ResourceSpec preferredResources  = ResourceSpec.DEFAULT;
    private int          managedMemoryWeight = DEFAULT_MANAGED_MEMORY_WEIGHT;
    private String       uid;

    private String userProvidedNodeHash;

    protected long bufferTimeout = -1;

    private String slotSharingGroup;

    private String coLocationGroupKey;

    public Transformation(String name, TypeInformation<T> outputType, int parallelism) {
        this.id = getNewNodeId();
        this.name = checkNotNull(name);
        this.outputType = outputType;
        this.parallelism = parallelism;
        this.slotSharingGroup = null;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public void setResources(ResourceSpec minResources, ResourceSpec preferredResources) {
        this.minResources = checkNotNull(minResources);
        this.preferredResources = checkNotNull(preferredResources);
    }

    public ResourceSpec getMinResources() {
        return minResources;
    }

    public ResourceSpec getPreferredResources() {
        return preferredResources;
    }

    public void setManagedMemoryWeight(int managedMemoryWeight) {
        this.managedMemoryWeight = managedMemoryWeight;
    }

    public int getManagedMemoryWeight() {
        return managedMemoryWeight;
    }

    public void setUidHash(String uidHash) {

        Preconditions.checkNotNull(uidHash);
        checkArgument(uidHash.matches("^[0-9A-Fa-f]{32}$"),
                "Node hash must be a 32 character String that describes a hex code. Found: " + uidHash);

        this.userProvidedNodeHash = uidHash;
    }

    public String getUserProvidedNodeHash() {
        return userProvidedNodeHash;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getSlotSharingGroup() {
        return slotSharingGroup;
    }

    public void setSlotSharingGroup(String slotSharingGroup) {
        this.slotSharingGroup = slotSharingGroup;
    }

    public void setCoLocationGroupKey(String coLocationGroupKey) {
        this.coLocationGroupKey = coLocationGroupKey;
    }

    public String getCoLocationGroupKey() {
        return coLocationGroupKey;
    }

    public void setOutputType(TypeInformation<T> outputType) {
        if (typeUsed) {
            throw new IllegalStateException(
                    "TypeInformation cannot be filled in for the type after it has been used. "
                            + "Please make sure that the type info hints are the first call after"
                            + " the transformation function, "
                            + "before any access to types or semantic properties, etc.");
        }
        this.outputType = outputType;
    }

    public TypeInformation<T> getOutputType() {
        if (outputType instanceof MissingTypeInfo) {
            MissingTypeInfo typeInfo = (MissingTypeInfo) this.outputType;
            throw new InvalidTypesException(
                    "The return type of function '"
                            + typeInfo.getFunctionName()
                            + "' could not be determined automatically, due to type erasure. "
                            + "You can give type information hints by using the returns(...) "
                            + "method on the result of the transformation call, or by letting "
                            + "your function implement the 'ResultTypeQueryable' "
                            + "interface.", typeInfo.getTypeException());
        }
        typeUsed = true;
        return this.outputType;
    }

    public void setBufferTimeout(long bufferTimeout) {
        checkArgument(bufferTimeout >= -1);
        this.bufferTimeout = bufferTimeout;
    }

    public long getBufferTimeout() {
        return bufferTimeout;
    }

    public abstract Collection<Transformation<?>> getTransitivePredecessors();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", outputType=" + outputType +
                ", parallelism=" + parallelism +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transformation)) {
            return false;
        }

        Transformation<?> that = (Transformation<?>) o;

        if (bufferTimeout != that.bufferTimeout) {
            return false;
        }
        if (id != that.id) {
            return false;
        }
        if (parallelism != that.parallelism) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        return outputType != null ? outputType.equals(that.outputType) : that.outputType == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + (outputType != null ? outputType.hashCode() : 0);
        result = 31 * result + parallelism;
        result = 31 * result + (int) (bufferTimeout ^ (bufferTimeout >>> 32));
        return result;
    }

}
