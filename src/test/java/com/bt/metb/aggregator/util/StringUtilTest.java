package com.bt.metb.aggregator.util;

import com.bt.metb.MetbAbstractBase;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author 612031539 - Sid
 *
 */
@RunWith(JMockit.class)
public class StringUtilTest extends MetbAbstractBase {

	@Test
	public void testSuccessStringArray() {

		String[] result = StringUtil.getStringArrayFromCsv("abc,xyz");
		assertEquals("abc", result[0]);
	}
	
	@Test
	public void testSuccessStringArrayList() {

		List<String> result = StringUtil.getStringArrayListFromCsv("abc,xyz");
		assertEquals("abc", result.get(0));
	}
	
	@Test
	public void testSuccessIsNullOrEmptyArrayElementsWithValue() {

		boolean result = StringUtil.isNullOrEmptyArrayElements("abc");
		assertEquals(false, result);
	}
	
	@Test
	public void testSuccessIsNullOrEmptyArrayElementsWithEmpty() {

		boolean result = StringUtil.isNullOrEmptyArrayElements("");
		assertEquals(true, result);
	}
	
	@Test
	public void testSuccessIsNullOrEmptyArrayElementsWithNull() {

		boolean result = StringUtil.isNullOrEmptyArrayElements(null);
		assertEquals(true, result);
	}
	
	@Test
	public void testSuccessReturnEmptyIfNull() {

		String result = StringUtil.returnEmptyIfNull(null);
		assertEquals("", result);
	}
	
	@Test
	public void testSuccessReturnEmptyIfValue() {

		String result = StringUtil.returnEmptyIfNull("BT");
		assertEquals("BT", result);
	}
	
	@Test
	public void testSuccessIsValuePresentInCSVStringFound() {

		boolean result = StringUtil.isValuePresentInCSVString("BT", "BT");
		assertEquals(true, result);
	}
	
	@Test
	public void testSuccessIsValuePresentInCSVStringNotFound() {

		boolean result = StringUtil.isValuePresentInCSVString("BT", "AB");
		assertEquals(false, result);
	}
	
	@Test
	public void testSuccessGetPaddedString() {

		String result = StringUtil.getPaddedString(2);
		assertEquals("  ", result);
	} 
	
	@Test
	public void testSuccessGetCurrentDateTime() {

		String result = StringUtil.getCurrentDateTime("ddMMyyyy");
		assertNotNull(result);
	} 
	
	@Test
	public void testSuccessRemoveSingleQuotes() {

		String result = StringUtil.removeSingleQuotes("text");
		assertEquals("ex", result);
	} 
	
	@Test
	public void testSuccessGetCommomIndex() {

		int result = StringUtil.getCommomIndex("text-text+.Z");
		assertEquals(11, result);
	} 
	
	@Test
	public void testSuccessDateFormation() {
		String result = StringUtil.dateFormation("10:28:2018-00.");
		assertEquals("8201800", result);
	} 
	
	@Test
	public void testSuccessIsNotNull() {
		boolean result = StringUtil.isNotNull("test");
		assertEquals(true, result);
	} 
	
	@Test
	public void testSuccessIsEmpty() {
		boolean result = StringUtil.isNotNull(null);
		
		result = StringUtil.isNotNull("");
		assertEquals(false, result);
	} 
	
}
