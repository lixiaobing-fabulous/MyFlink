package com.lxb.flink.api.common.operators;

import com.lxb.flink.api.common.resources.CPUResource;
import com.lxb.flink.api.common.resources.GPUResource;
import com.lxb.flink.api.common.resources.Resource;
import com.lxb.flink.configuration.MemorySize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.lxb.flink.utl.Preconditions.checkArgument;
import static com.lxb.flink.utl.Preconditions.checkNotNull;

public final class ResourceSpec implements Serializable {
    public static final ResourceSpec          UNKNOWN           = new ResourceSpec();
    public static final ResourceSpec          DEFAULT           = UNKNOWN;
    public static final ResourceSpec          ZERO              = ResourceSpec.newBuilder(0.0, 0).build();
    private final       Resource              cpuCores;
    private final       MemorySize            taskHeapMemory;
    private final       MemorySize            taskOffHeapMemory;
    private final       MemorySize            managedMemory;
    private final       Map<String, Resource> extendedResources = new HashMap<>(1);
    private ResourceSpec(
            final Resource cpuCores,
            final MemorySize taskHeapMemory,
            final MemorySize taskOffHeapMemory,
            final MemorySize managedMemory,
            final Resource... extendedResources) {

        checkNotNull(cpuCores);
        checkArgument(cpuCores instanceof CPUResource, "cpuCores must be CPUResource");

        this.cpuCores = cpuCores;
        this.taskHeapMemory = checkNotNull(taskHeapMemory);
        this.taskOffHeapMemory = checkNotNull(taskOffHeapMemory);
        this.managedMemory = checkNotNull(managedMemory);

        for (Resource resource : extendedResources) {
            if (resource != null) {
                this.extendedResources.put(resource.getName(), resource);
            }
        }
    }

    private ResourceSpec() {
        this.cpuCores = null;
        this.taskHeapMemory = null;
        this.taskOffHeapMemory = null;
        this.managedMemory = null;
    }
    public ResourceSpec merge(final ResourceSpec other) {
        checkNotNull(other, "Cannot merge with null resources");

        if (this.equals(UNKNOWN) || other.equals(UNKNOWN)) {
            return UNKNOWN;
        }

        ResourceSpec target = new ResourceSpec(
                this.cpuCores.merge(other.cpuCores),
                this.taskHeapMemory.add(other.taskHeapMemory),
                this.taskOffHeapMemory.add(other.taskOffHeapMemory),
                this.managedMemory.add(other.managedMemory));
        target.extendedResources.putAll(extendedResources);
        for (Resource resource : other.extendedResources.values()) {
            target.extendedResources.merge(resource.getName(), resource, (v1, v2) -> v1.merge(v2));
        }
        return target;
    }

    public ResourceSpec subtract(final ResourceSpec other) {
        checkNotNull(other, "Cannot subtract null resources");

        if (this.equals(UNKNOWN) || other.equals(UNKNOWN)) {
            return UNKNOWN;
        }

        checkArgument(other.lessThanOrEqual(this), "Cannot subtract a larger ResourceSpec from this one.");

        final ResourceSpec target = new ResourceSpec(
                this.cpuCores.subtract(other.cpuCores),
                this.taskHeapMemory.subtract(other.taskHeapMemory),
                this.taskOffHeapMemory.subtract(other.taskOffHeapMemory),
                this.managedMemory.subtract(other.managedMemory));

        target.extendedResources.putAll(extendedResources);

        for (Resource resource : other.extendedResources.values()) {
            target.extendedResources.merge(resource.getName(), resource, (v1, v2) -> {
                final Resource subtracted = v1.subtract(v2);
                return subtracted.getValue().compareTo(BigDecimal.ZERO) == 0 ? null : subtracted;
            });
        }
        return target;
    }

    public Resource getCpuCores() {
        throwUnsupportedOperationExceptionIfUnknown();
        return this.cpuCores;
    }

    public MemorySize getTaskHeapMemory() {
        throwUnsupportedOperationExceptionIfUnknown();
        return this.taskHeapMemory;
    }

    public MemorySize getTaskOffHeapMemory() {
        throwUnsupportedOperationExceptionIfUnknown();
        return taskOffHeapMemory;
    }

    public MemorySize getManagedMemory() {
        throwUnsupportedOperationExceptionIfUnknown();
        return managedMemory;
    }

    public Resource getGPUResource() {
        throwUnsupportedOperationExceptionIfUnknown();
        return extendedResources.get(GPUResource.NAME);
    }

    public Map<String, Resource> getExtendedResources() {
        throwUnsupportedOperationExceptionIfUnknown();
        return extendedResources;
    }

    private void throwUnsupportedOperationExceptionIfUnknown() {
        if (this.equals(UNKNOWN)) {
            throw new UnsupportedOperationException();
        }
    }
    public boolean lessThanOrEqual(final ResourceSpec other) {
        checkNotNull(other, "Cannot compare with null resources");

        if (this.equals(UNKNOWN) && other.equals(UNKNOWN)) {
            return true;
        } else if (this.equals(UNKNOWN) || other.equals(UNKNOWN)) {
            throw new IllegalArgumentException("Cannot compare specified resources with UNKNOWN resources.");
        }

        int cmp1 = this.cpuCores.getValue().compareTo(other.getCpuCores().getValue());
        int cmp2 = this.taskHeapMemory.compareTo(other.taskHeapMemory);
        int cmp3 = this.taskOffHeapMemory.compareTo(other.taskOffHeapMemory);
        int cmp4 = this.managedMemory.compareTo(other.managedMemory);
        if (cmp1 <= 0 && cmp2 <= 0 && cmp3 <= 0 && cmp4 <= 0) {
            for (Resource resource : extendedResources.values()) {
                if (!other.extendedResources.containsKey(resource.getName()) ||
                        other.extendedResources.get(resource.getName()).getValue().compareTo(resource.getValue()) < 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == ResourceSpec.class) {
            ResourceSpec that = (ResourceSpec) obj;
            return Objects.equals(this.cpuCores, that.cpuCores) &&
                    Objects.equals(this.taskHeapMemory, that.taskHeapMemory) &&
                    Objects.equals(this.taskOffHeapMemory, that.taskOffHeapMemory) &&
                    Objects.equals(this.managedMemory, that.managedMemory) &&
                    Objects.equals(extendedResources, that.extendedResources);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(cpuCores);
        result = 31 * result + Objects.hashCode(taskHeapMemory);
        result = 31 * result + Objects.hashCode(taskOffHeapMemory);
        result = 31 * result + Objects.hashCode(managedMemory);
        result = 31 * result + extendedResources.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (this.equals(UNKNOWN)) {
            return "ResourceSpec{UNKNOWN}";
        }

        final StringBuilder extResources = new StringBuilder(extendedResources.size() * 10);
        for (Map.Entry<String, Resource> resource : extendedResources.entrySet()) {
            extResources.append(", ").append(resource.getKey()).append('=').append(resource.getValue().getValue());
        }
        return "ResourceSpec{" +
                "cpuCores=" + cpuCores.getValue() +
                ", taskHeapMemory=" + taskHeapMemory.toHumanReadableString() +
                ", taskOffHeapMemory=" + taskOffHeapMemory.toHumanReadableString() +
                ", managedMemory=" + managedMemory.toHumanReadableString() + extResources +
                '}';
    }
    private Object readResolve() {
        // try to preserve the singleton property for UNKNOWN
        return this.equals(UNKNOWN) ? UNKNOWN : this;
    }
    public static Builder newBuilder(double cpuCores, int taskHeapMemoryMB) {
        return new Builder(new CPUResource(cpuCores), MemorySize.ofMebiBytes(taskHeapMemoryMB));
    }
    public static class Builder {

        private Resource cpuCores;
        private MemorySize taskHeapMemory;
        private MemorySize taskOffHeapMemory = MemorySize.ZERO;
        private MemorySize managedMemory = MemorySize.ZERO;
        private GPUResource gpuResource;

        private Builder(CPUResource cpuCores, MemorySize taskHeapMemory) {
            this.cpuCores = cpuCores;
            this.taskHeapMemory = taskHeapMemory;
        }

        public Builder setCpuCores(double cpuCores) {
            this.cpuCores = new CPUResource(cpuCores);
            return this;
        }

        public Builder setTaskHeapMemory(MemorySize taskHeapMemory) {
            this.taskHeapMemory = taskHeapMemory;
            return this;
        }

        public Builder setTaskHeapMemoryMB(int taskHeapMemoryMB) {
            this.taskHeapMemory = MemorySize.ofMebiBytes(taskHeapMemoryMB);
            return this;
        }

        public Builder setTaskOffHeapMemory(MemorySize taskOffHeapMemory) {
            this.taskOffHeapMemory = taskOffHeapMemory;
            return this;
        }

        public Builder setOffTaskHeapMemoryMB(int taskOffHeapMemoryMB) {
            this.taskOffHeapMemory = MemorySize.ofMebiBytes(taskOffHeapMemoryMB);
            return this;
        }

        public Builder setManagedMemory(MemorySize managedMemory) {
            this.managedMemory = managedMemory;
            return this;
        }

        public Builder setManagedMemoryMB(int managedMemoryMB) {
            this.managedMemory = MemorySize.ofMebiBytes(managedMemoryMB);
            return this;
        }

        public Builder setGPUResource(double gpus) {
            this.gpuResource = new GPUResource(gpus);
            return this;
        }

        public ResourceSpec build() {
            return new ResourceSpec(
                    cpuCores,
                    taskHeapMemory,
                    taskOffHeapMemory,
                    managedMemory,
                    gpuResource);
        }
    }
}
