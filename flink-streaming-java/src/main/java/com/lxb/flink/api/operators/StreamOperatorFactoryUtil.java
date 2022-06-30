package com.lxb.flink.api.operators;

import com.lxb.flink.api.graph.StreamConfig;
import com.lxb.flink.api.java.tupple.Tuple2;
import com.lxb.flink.runtime.operators.coordination.OperatorEventDispatcher;
import com.lxb.flink.runtime.streamrecord.StreamRecord;
import com.lxb.flink.runtime.tasks.ProcessingTimeService;
import com.lxb.flink.runtime.tasks.ProcessingTimeServiceAware;
import com.lxb.flink.runtime.tasks.StreamTask;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A utility to instantiate new operators with a given factory.
 */
public class StreamOperatorFactoryUtil {
	/**
	 * Creates a new operator using a factory and makes sure that all special factory traits are properly handled.
	 *
	 * @param operatorFactory the operator factory.
	 * @param containingTask the containing task.
	 * @param configuration the configuration of the operator.
	 * @param output the output of the operator.
	 * @param operatorEventDispatcher the operator event dispatcher for communication between operator and coordinators.
	 * @return a newly created and configured operator, and the {@link ProcessingTimeService} instance it can access.
	 */
	public static <OUT, OP extends StreamOperator<OUT>> Tuple2<OP, Optional<ProcessingTimeService>> createOperator(
			StreamOperatorFactory<OUT> operatorFactory,
			StreamTask<OUT, ?> containingTask,
			StreamConfig configuration,
			Output<StreamRecord<OUT>> output,
			OperatorEventDispatcher operatorEventDispatcher) {

		MailboxExecutor mailboxExecutor = containingTask.getMailboxExecutorFactory().createExecutor(configuration.getChainIndex());

		final Supplier<ProcessingTimeService> processingTimeServiceFactory =
				() -> containingTask.getProcessingTimeServiceFactory().createProcessingTimeService(mailboxExecutor);

		final ProcessingTimeService processingTimeService;
		if (operatorFactory instanceof ProcessingTimeServiceAware) {
			processingTimeService = processingTimeServiceFactory.get();
			((ProcessingTimeServiceAware) operatorFactory).setProcessingTimeService(processingTimeService);
		} else {
			processingTimeService = null;
		}

		// TODO: what to do with ProcessingTimeServiceAware?
		OP op = operatorFactory.createStreamOperator(
			new StreamOperatorParameters<>(
				containingTask,
				configuration,
				output,
				processingTimeService != null ? () -> processingTimeService : processingTimeServiceFactory,
				operatorEventDispatcher));
		return new Tuple2<>(op, Optional.ofNullable(processingTimeService));
	}
}
