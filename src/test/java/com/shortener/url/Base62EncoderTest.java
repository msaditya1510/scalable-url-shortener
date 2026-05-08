package com.shortener.url;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.shortener.url.util.Base62Encoder;

class Base62EncoderTest {

	@Test
	void testEncodedValues() {
		assertEquals("b",Base62Encoder.encode(1),"Encoding failed to output correct value");
		assertEquals("9",Base62Encoder.encode(61),"Encoding failed to output correct value");
		assertEquals("ba",Base62Encoder.encode(62),"Encoding failed to output correct value");
		assertEquals("qi",Base62Encoder.encode(1000),"Encoding failed to output correct value");
	}

}
