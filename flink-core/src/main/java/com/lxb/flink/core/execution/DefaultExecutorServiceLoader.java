package com.lxb.flink.core.execution;

import com.lxb.flink.configuration.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultExecutorServiceLoader implements PipelineExecutorServiceLoader {
    @Override
    public PipelineExecutorFactory getExecutorFactory(Configuration configuration) throws Exception {
        final ServiceLoader<PipelineExecutorFactory> loader =
                ServiceLoader.load(PipelineExecutorFactory.class);
        final List<PipelineExecutorFactory>     compatibleFactories = new ArrayList<>();
        final Iterator<PipelineExecutorFactory> factories           = loader.iterator();
        while (factories.hasNext()) {
            try {
                final PipelineExecutorFactory factory = factories.next();
                if (factory != null && factory.isCompatibleWith(configuration)) {
                    compatibleFactories.add(factory);
                }
            } catch (Throwable e) {
                if (e.getCause() instanceof NoClassDefFoundError) {
                    System.out.println("Could not load factory due to missing dependencies.");
                } else {
                    throw e;
                }
            }
        }

        if (compatibleFactories.size() > 1) {
            final String configStr =
                    configuration.toMap().entrySet().stream()
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("\n"));

            throw new IllegalStateException("Multiple compatible client factories found for:\n" + configStr + ".");
        }


        if (compatibleFactories.isEmpty()) {
            throw new IllegalStateException("No ExecutorFactory found to execute the application.");
        }

        return compatibleFactories.get(0);
    }

    @Override
    public Stream<String> getExecutorNames() {
        final ServiceLoader<PipelineExecutorFactory> loader =
                ServiceLoader.load(PipelineExecutorFactory.class);

        return StreamSupport.stream(loader.spliterator(), false)
                .map(PipelineExecutorFactory::getName);
    }
}
