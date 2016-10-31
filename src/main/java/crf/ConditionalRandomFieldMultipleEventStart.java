package crf;

import cn.edu.fudan.crf.ConditionalRandomFieldMultipleEvent;

public class ConditionalRandomFieldMultipleEventStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String confFile = "crf/crf_multiple.properties";

		ConditionalRandomFieldMultipleEvent crf = new ConditionalRandomFieldMultipleEvent(confFile);
		crf.setDebug(false);
		crf.init();
		crf.run();
	}
}
