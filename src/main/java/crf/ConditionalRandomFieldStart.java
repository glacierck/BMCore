package crf;

import cn.edu.fudan.crf.ConditionalRandomField;

public class ConditionalRandomFieldStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String confFile = "crf/crf.properties";

		ConditionalRandomField crf = new ConditionalRandomField(confFile);
		crf.setDebug(false);
		crf.init();
		crf.run();

	}

}
