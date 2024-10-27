/*-
 * Copyright (c) 2024 Red Hat, Inc.
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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj3.XmlAssert;

class WheelProperty extends Property<Car, Car.B, Wheel> {

    public WheelProperty() {
        super("wheel", Car::getWheels, Car.B::addWheel, true, false);
    }

    @Override
    protected void dump(XMLDumper dumper, Wheel wheel) throws XMLException {
        dumper.dumpStartElement("wheel");
        dumper.dumpStartElement(wheel.getTire());
        dumper.dumpEndElement();
        dumper.dumpEndElement();
    }

    @Override
    protected Wheel parse(XMLParser parser) throws XMLException {
        parser.parseStartElement("wheel");
        String tire = parser.parseStartElement();
        parser.parseEndElement(tire);
        parser.parseEndElement("wheel");
        return new Wheel(tire);
    }
}

class BasicTest {

    Entity<Car, Car.B> entity = Entity.of(
            "car",
            Car.B::new,
            Attribute.of("vin", Car::getVin, Car.B::setVin),
            Attribute.of("year", Car::getYear, Car.B::setYear, Object::toString, Integer::parseInt),
            new Relationship<>(
                    Entity.of(
                            "engine",
                            Engine.B::new,
                            Attribute.ofOptional("fuel", Engine::getFuel, Engine.B::setFuel),
                            Attribute.ofOptional(
                                    "power",
                                    Engine::getPower,
                                    Engine.B::setPower,
                                    Object::toString,
                                    Integer::parseInt)),
                    car -> List.of(car.getEngine()),
                    Car.B::setEngine,
                    false,
                    true),
            Relationship.ofSingular(
                    Entity.of("trailer", Trailer.B::new, Attribute.of("vin", Trailer::getVin, Trailer.B::setVin)),
                    Car::getTrailer,
                    Car.B::setTrailer),
            Attribute.ofMulti("name", Car::getNames, Car.B::addName),
            Attribute.ofMulti("review", Car::getReviews, Car.B::addReview, Object::toString, Integer::parseInt),
            new WheelProperty());

    String xml;
    String xml2;
    String expectException;

    void performTest() throws Exception {
        try {
            Car car = entity.fromXML(xml);
            xml2 = entity.toXML(car);
            assertNull(expectException);
            XmlAssert.assertThat(xml2).and(xml).ignoreWhitespace().areSimilar();
        } catch (XMLException e) {
            if (expectException != null) {
                assertTrue(e.getMessage().contains(expectException));
            } else {
                throw e;
            }
        }
    }

    @Test
    void testMalformedXML() throws Exception {
        xml = "boom!";
        expectException = "XMLStreamException";
        performTest();
    }

    @Test
    void testUnclosedComment() throws Exception {
        xml = "<car><!--";
        expectException = "XMLStreamException";
        performTest();
    }

    @Test
    void testTextParseError() throws Exception {
        xml = "<car><vin>>";
        expectException = "XMLStreamException";
        performTest();
    }

    @Test
    void testTextInWrongPlace() throws Exception {
        xml = "<car>text</car>";
        expectException = "Expected white space";
        performTest();
    }

    @Test
    void testWrongRootTag() throws Exception {
        xml = "<hello></hello>";
        expectException = "Expected <car> start element";
        performTest();
    }

    @Test
    void testCarValid() throws Exception {
        xml =
                """
                <car>
                  <vin>123ABC</vin>
                  <year>2004</year>
                  <engine>
                    <power>42</power>
                  </engine>
                </car>
                """;
        performTest();
    }

    @Test
    void testCarNoEngine() throws Exception {
        xml =
                """
                <car>
                  <vin>123ABC</vin>
                  <year>2004</year>
                </car>
                """;
        expectException = "Mandatory <engine> property of <car> has not been set";
        performTest();
    }

    @Test
    void testWheels() throws Exception {
        xml =
                """
                <car>
                  <vin>123ABC</vin>
                  <year>2004</year>
                  <engine/>
                  <wheel>
                    <something/>
                  </wheel>
                </car>
                """;
        performTest();
    }

    @Test
    void testWheelNoTire() throws Exception {
        xml =
                """
                <car>
                  <vin>123ABC</vin>
                  <year>2004</year>
                  <engine/>
                  <wheel>
                  </wheel>
                </car>
                """;
        expectException = "Expected a start element";
        performTest();
    }

    @Test
    void testWheelTireNested() throws Exception {
        xml =
                """
                <car>
                  <vin>123ABC</vin>
                  <year>2004</year>
                  <engine/>
                  <wheel>
                    <aa>
                      <bb/>
                    </aa>
                  </wheel>
                </car>
                """;
        expectException = "Expected </aa> end element";
        performTest();
    }

    @Test
    void testFull() throws Exception {
        xml =
                """
                <car>
                  <vin>123ABC</vin>
                  <year>2004</year>
                  <engine>
                    <fuel>diesel</fuel>
                    <power>240</power>
                  </engine>
                  <trailer>
                    <vin>AA22</vin>
                  </trailer>
                  <name>C1</name>
                  <name>V2</name>
                  <review>2005</review>
                  <review>2006</review>
                  <wheel>
                    <aa/>
                  </wheel>
                  <wheel>
                    <bb/>
                  </wheel>
                </car>
                """;
        performTest();
    }
}
