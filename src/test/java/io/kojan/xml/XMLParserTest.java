/*-
 * Copyright (c) 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kojan.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import org.junit.jupiter.api.Test;

/**
 * @author Mikolaj Izdebski
 */
class XMLParserTest {
    @Test
    void testParse() throws Exception {
        String xml =
                """
                <root>
                  <foo> abc</foo>
                  <bar/>
                </root>
                """;
        StringReader r = new StringReader(xml);
        XMLParserImpl p = new XMLParserImpl(r);

        assertFalse(p.hasStartElement());
        assertTrue(p.parseText().isBlank());
        p.parseStartDocument();

        assertTrue(p.hasStartElement());
        assertTrue(p.hasStartElement("root"));
        assertTrue(p.parseText().isBlank());
        assertEquals("root", p.parseStartElement());

        assertTrue(p.hasStartElement());
        assertTrue(p.hasStartElement("foo"));
        assertTrue(p.parseText().isBlank());
        assertEquals("foo", p.parseStartElement());

        assertFalse(p.hasStartElement());
        assertEquals(" abc", p.parseText());

        assertFalse(p.hasStartElement());
        assertTrue(p.parseText().isEmpty());
        p.parseEndElement("foo");

        assertTrue(p.hasStartElement());
        assertTrue(p.hasStartElement("bar"));
        assertTrue(p.parseText().isBlank());
        assertEquals("bar", p.parseStartElement());

        assertFalse(p.hasStartElement());
        assertTrue(p.parseText().isEmpty());
        p.parseEndElement("bar");

        assertFalse(p.hasStartElement());
        assertTrue(p.parseText().isBlank());
        p.parseEndElement("root");

        assertFalse(p.hasStartElement());
        assertTrue(p.parseText().isBlank());
        p.parseEndDocument();
    }
}
