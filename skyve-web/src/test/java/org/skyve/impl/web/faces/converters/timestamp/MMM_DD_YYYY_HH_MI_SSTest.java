package org.skyve.impl.web.faces.converters.timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import javax.faces.convert.ConverterException;

import org.junit.Before;
import org.junit.Test;
import org.skyve.domain.types.Timestamp;
import org.skyve.util.Time;

public class MMM_DD_YYYY_HH_MI_SSTest {

	private MMM_DD_YYYY_HH_MI_SS formatter;

	@Before
	public void before() {
		formatter = new MMM_DD_YYYY_HH_MI_SS();
	}

	@Test(expected = ConverterException.class)
	public void testGetAsObjectInvalidFormat() throws Exception {
		// call the method under test
		formatter.getAsObject(null, null, "01-03-2020 02:30:55");

		fail("Should throw exception before this line.");
	}

	@Test
	public void testGetAsObjectValidFormatAM() throws Exception {
		// setup the test data
		Timestamp testDate = new Timestamp(Time.withDate(01, 03, 2020));
		Time.setTime(testDate, 02, 30, 55);

		// call the method under test
		assertThat(formatter.getAsObject(null, null, "Mar-01-2020 02:30:55 AM"), is(testDate));
	}

	@Test
	public void testGetAsObjectValidFormatPM() throws Exception {
		// setup the test data
		Timestamp testDate = new Timestamp(Time.withDate(01, 03, 2020));
		Time.setTime(testDate, 14, 30, 55);

		// call the method under test
		assertThat(formatter.getAsObject(null, null, "Mar-01-2020 02:30:55 PM"), is(testDate));
	}

	@Test
	public void testGetAsStringAM() throws Exception {
		// setup the test data
		Timestamp testDate = new Timestamp(Time.withDate(01, 03, 2020));
		Time.setTime(testDate, 02, 30, 55);

		// call the method under test
		assertThat(formatter.getAsString(null, null, testDate), is("Mar-01-2020 02:30:55 AM"));
	}

	@Test
	public void testGetAsStringPM() throws Exception {
		// setup the test data
		Timestamp testDate = new Timestamp(Time.withDate(01, 03, 2020));
		Time.setTime(testDate, 14, 30, 55);

		// call the method under test
		assertThat(formatter.getAsString(null, null, testDate), is("Mar-01-2020 02:30:55 PM"));
	}
}
