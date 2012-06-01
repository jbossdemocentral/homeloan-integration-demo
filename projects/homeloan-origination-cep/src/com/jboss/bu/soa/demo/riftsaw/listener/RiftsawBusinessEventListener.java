package com.jboss.bu.soa.demo.riftsaw.listener;

import java.util.Properties;

import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.jboss.soa.esb.ConfigurationException;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.listeners.message.ActionProcessingPipeline;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.format.MessageFactory;

public class RiftsawBusinessEventListener implements BpelEventListener {

	private static ActionProcessingPipeline pipeline;

	@Override
	public void onEvent(BpelEvent bpelEvent) {
		// ProcessInstanceStartedEvent
		// ProcessCompletionEvent

		if (pipeline != null) {
			if (bpelEvent.getType().equals(BpelEvent.TYPE.activityLifecycle) || bpelEvent.getType().equals(BpelEvent.TYPE.instanceLifecycle)) {
				Message esbMessage = MessageFactory.getInstance().getMessage();
				esbMessage.getBody().add("event", bpelEvent);
				
				// Forward the ESB message to the Action Pipeline...
				boolean success = pipeline.process(esbMessage);

				if (!success) {
					System.out.println("Error in BpelEventListener " + this.getClass().getSimpleName() + ". Not sucessfuly processing message on pipeline");
				}
			}
		}
	}

	@Override
	public void startup(Properties properties) {

		if (pipeline == null) {
			System.out.println("Riftsaw not routing messages to JBoss ESB");
		}
	}

	@Override
	public void shutdown() {
	}

	public void startEsbListener(ConfigTree config)
			throws ConfigurationException {
		pipeline = new ActionProcessingPipeline(config);
		pipeline.initialise();
	}

	public void stopEsbListener() {
		if (pipeline != null) {
			pipeline.destroy();
			pipeline = null;
		}
	}
}
