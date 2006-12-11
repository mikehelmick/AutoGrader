package edu.muohio.csa.autograder.style;

import edu.muohio.csa.autograder.framework.GradingException;

public class StyleException extends GradingException {

	private String styleReport = "";

	public String getStyleReport() {
		return styleReport;
	}

	public void setStyleReport(String styleReport) {
		this.styleReport = styleReport;
	}
	
	
	
}
