package com.bt.metb.aggregator.util;


import com.bt.metb.MetbAbstractBase;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(JMockit.class)
public class XmlUtilTest extends MetbAbstractBase {


	@Test
	public void testFailuregetStartTagNull() {
		try {
			XmlUtil.startTag(null);
		} catch (Exception e) {
			assertEquals("Expecting a start tag for empty or null object", e.getMessage());
		}

	}

	@Test
	public void testSuccessgetStartTagValue() {

		String result = XmlUtil.startTag("name");
		assertEquals("<name>", result);

	}

	@Test
	public void testSuccessgetEndTagValue() {

		String result = XmlUtil.endTag("name");
		assertEquals("</name>", result);

	}

	@Test
	public void testFailuregetEndTagNull() {
		try {
			XmlUtil.endTag(null);
		} catch (Exception e) {
			assertEquals("Expecting a end tag for empty or null object", e.getMessage());
		}

	}

	@Test
	public void testSuccessgetEmptyTagValue() {

		String result = XmlUtil.emptyTag("name");
		assertEquals("<name/>", result);

	}

	@Test
	public void testFailuregetEmptyTagNull() {
		try {
			XmlUtil.emptyTag(null);
		} catch (Exception e) {
			assertEquals("Expecting a end tag for empty or null object", e.getMessage());
		}

	}




}
