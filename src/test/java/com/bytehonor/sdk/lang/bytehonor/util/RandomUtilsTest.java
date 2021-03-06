package com.bytehonor.sdk.lang.bytehonor.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RandomUtilsTest {

	@Test
	public void testGetInt() {
		int min = 100000;
		int max = 999999;
		boolean isOk = true;
		for (int i=0;i<10000;i++) {
			int src = RandomUtils.getInt(min, max);
			boolean isFit = src > min && src < max;
			isOk = (isFit && isOk);
		}
		assertTrue("testGetInt", isOk );
	}

	@Test
	public void testGetString() {
		int len = 8;
		boolean isOk = true;
		for (int i=0;i<10000;i++) {
			String src = RandomUtils.getString(len);
			boolean isFit = src != null && src.length() == len;
			isOk = (isFit && isOk);
		}
		assertTrue("testGetString", isOk );
	}

}
