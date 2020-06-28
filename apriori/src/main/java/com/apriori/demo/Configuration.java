package com.apriori.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("apriori")
public class Configuration {

	private int support_val;

	/**
	 * @return the support_val
	 */
	public int getSupport_val() {
		return support_val;
	}

	/**
	 * @param support_val the support_val to set
	 */
	public void setSupport_val(int support_val) {
		this.support_val = support_val;
	}

}
