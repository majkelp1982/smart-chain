package pl.smarthouse.smartchain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import pl.smarthouse.smartchain.model.Chain;
import pl.smarthouse.smartchain.processor.ChainProcessor;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChainService implements ApplicationListener<ApplicationReadyEvent> {
	private static final String PROCESSING_CHAIN = "Processing chain number: {}, description: {}";
	List<ChainProcessor> chainProcessorList = new ArrayList<>();

	public void addChain(final Chain chain) {
		chainProcessorList.add(new ChainProcessor(chain));
	}

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		log.info("Starting chain processor.");
		while (true) {
			for (final ChainProcessor chainProcessor : chainProcessorList) {
				chainProcessor.process();
			}
		}
	}

}
