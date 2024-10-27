/*-
 * Copyright (c) 2020-2024 Red Hat, Inc.
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * An entity type. Type of things about which the data should be stored.
 *
 * <p>When stored in XML form, an entity is represented by an XML element with a specified tag. Nested child elements
 * represent entity attributes, entity relationships and possibly other custom entity properties.
 *
 * <p>An entity has an associated base data type, which may be immutable or mutable - the library code makes no
 * assumptions about the mutability of the base type.
 *
 * <p>In addition to its main type, an entity also has a bean type, which is always mutable, and implements the
 * {@link Builder} interface, allowing an object of the main type to be built from that bean. In the case where the main
 * type is mutable, it is acceptable for the main type and the bean type to be the same type, and the
 * {@link Builder#build()} method to simply return {@code this}.
 *
 * @param <Type> data type of entity
 * @param <Bean> type of bean associated with the entity
 * @author Mikolaj Izdebski
 */
public class Entity<Type, Bean extends Builder<Type>> {
    private final String tag;
    private final Factory<Bean> beanFactory;
    private final List<Property<Type, Bean, ?>> properties;

    /**
     * Creates an entity.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param tag XML element tag name used to serialize the property in XML form (see {@link #getTag})
     * @param beanFactory factory used to create initial entity bean
     * @param properties one or more entity properties
     * @return created entity
     */
    @SafeVarargs
    public static <Type, Bean extends Builder<Type>> Entity<Type, Bean> of(
            String tag, Factory<Bean> beanFactory, Property<Type, Bean, ?>... properties) {
        return new Entity<>(tag, beanFactory, Arrays.asList(properties));
    }

    /**
     * Creates an entity.
     *
     * @param tag XML element tag name used to serialize the property in XML form (see {@link #getTag})
     * @param beanFactory factory used to create initial entity bean
     * @param properties one or more entity properties
     */
    public Entity(String tag, Factory<Bean> beanFactory, List<Property<Type, Bean, ?>> properties) {
        this.tag = tag;
        this.beanFactory = beanFactory;
        this.properties = List.copyOf(properties);
    }

    /**
     * Determines XML element tag name used to serialize the entity in XML form.
     *
     * @return XML element tag name
     */
    public String getTag() {
        return tag;
    }

    /**
     * Obtains a factory used to create initial entity bean.
     *
     * @return factory used to create initial entity bean
     */
    public Factory<Bean> getBeanFactory() {
        return beanFactory;
    }

    /**
     * Get entity properties, such as attributes, relationships and other custom properties.
     *
     * @return unmodifiable list of properties
     */
    public List<Property<Type, Bean, ?>> getProperties() {
        return properties;
    }

    /**
     * Deserializes entity from XML format, reading XML data from given {@link Reader}.
     *
     * @param reader the source to read XML data from
     * @return deserialized entity object
     * @throws XMLException in case exception occurs during XML deserialization
     */
    public Type readFromXML(Reader reader) throws XMLException {
        XMLParserImpl parser = new XMLParserImpl(reader);
        return parser.parseDocument(this);
    }

    /**
     * Deserializes entity from XML format, reading XML data from file at given {@link Path}.
     *
     * @param path path to the source file from which XML data is read
     * @return deserialized entity object
     * @throws IOException in case I/O exception occurs when reading form the file
     * @throws XMLException in case exception occurs during XML deserialization
     */
    public Type readFromXML(Path path) throws IOException, XMLException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return readFromXML(reader);
        }
    }

    /**
     * Deserializes entity from XML format.
     *
     * @param xml serialized entity in XML format
     * @return deserialized entity object
     * @throws XMLException in case exception occurs during XML deserialization
     */
    public Type fromXML(String xml) throws XMLException {
        try (StringReader reader = new StringReader(xml)) {
            return readFromXML(reader);
        }
    }

    /**
     * Serializes entity into XML format, writing XML data to given {@link Writer}.
     *
     * @param writer the sink to write XML data to
     * @param object entity object to serialize
     * @throws XMLException in case exception occurs during XML serialization
     */
    public void writeToXML(Writer writer, Type object) throws XMLException {
        XMLDumperImpl dumper = new XMLDumperImpl(writer);
        dumper.dumpDocument(this, object);
    }

    /**
     * Serializes entity into XML format, writing XML data to file at given {@link Path}.
     *
     * @param path path to the sink file to write XML data to
     * @param object entity object to serialize
     * @throws IOException in case I/O exception occurs when writing to the file
     * @throws XMLException in case exception occurs during XML serialization
     */
    public void writeToXML(Path path, Type object) throws IOException, XMLException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writeToXML(writer, object);
        }
    }

    /**
     * Serializes entity into XML format.
     *
     * @param object entity object to serialize
     * @return serialized entity in XML format
     * @throws XMLException in case exception occurs during XML serialization
     */
    public String toXML(Type object) throws XMLException {
        try (StringWriter writer = new StringWriter()) {
            writeToXML(writer, object);
            return writer.toString();
        } catch (IOException e) {
            // StringWriter.close() should never throw an IOException
            throw new UncheckedIOException(e);
        }
    }
}
