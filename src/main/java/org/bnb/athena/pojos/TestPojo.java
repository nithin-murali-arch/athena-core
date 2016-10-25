package org.bnb.athena.pojos;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestPojo {
	private String test;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
	
}
