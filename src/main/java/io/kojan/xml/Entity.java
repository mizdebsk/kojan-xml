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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** @author Mikolaj Izdebski */
public class Entity<Type, Bean extends Builder<Type>> {
    private final String tag;
    private final Factory<Bean> beanFactory;
    private final List<Constituent<Type, Bean, ?, ?>> elements = new ArrayList<>();

    public Entity(String tag, Factory<Bean> beanFactory) {
        this.tag = tag;
        this.beanFactory = beanFactory;
    }

    public String getTag() {
        return tag;
    }

    public Bean newBean() {
        return beanFactory.newInstance();
    }

    public List<Constituent<Type, Bean, ?, ?>> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public void addAttribute(String tag, Getter<Type, String> getter, Setter<Bean, String> setter) {
        elements.add(new Attribute<>(
                tag, x -> List.of(getter.get(x)), setter, Function.identity(), Function.identity(), false, true));
    }

    public <AttributeType> void addAttribute(
            String tag,
            Getter<Type, AttributeType> getter,
            Setter<Bean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter) {
        elements.add(new Attribute<>(
                tag, x -> List.of(getter.get(x)), setter, toStringAdapter, fromStringAdapter, false, true));
    }

    public void addOptionalAttribute(String tag, Getter<Type, String> getter, Setter<Bean, String> setter) {
        elements.add(new Attribute<>(
                tag, x -> List.of(getter.get(x)), setter, Function.identity(), Function.identity(), true, true));
    }

    public <AttributeType> void addOptionalAttribute(
            String tag,
            Getter<Type, AttributeType> getter,
            Setter<Bean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter) {
        elements.add(new Attribute<>(
                tag, x -> List.of(getter.get(x)), setter, toStringAdapter, fromStringAdapter, true, true));
    }

    public void addMultiAttribute(String tag, Getter<Type, Iterable<String>> getter, Setter<Bean, String> setter) {
        elements.add(new Attribute<>(tag, getter, setter, Function.identity(), Function.identity(), true, false));
    }

    public <AttributeType> void addMultiAttribute(
            String tag,
            Getter<Type, Iterable<AttributeType>> getter,
            Setter<Bean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter) {
        elements.add(new Attribute<>(tag, getter, setter, toStringAdapter, fromStringAdapter, true, false));
    }

    public <RelatedType, RelatedBean extends Builder<RelatedType>> void addSingularRelationship(
            Entity<RelatedType, RelatedBean> relatedEntity,
            Getter<Type, RelatedType> getter,
            Setter<Bean, RelatedType> setter) {
        elements.add(new Relationship<>(relatedEntity, x -> List.of(getter.get(x)), setter, true, true));
    }

    public <RelatedType, RelatedBean extends Builder<RelatedType>> void addRelationship(
            Entity<RelatedType, RelatedBean> relatedEntity,
            Getter<Type, Iterable<RelatedType>> getter,
            Setter<Bean, RelatedType> setter) {
        elements.add(new Relationship<>(relatedEntity, getter, setter, true, false));
    }

    public void addCustomElement(Constituent<Type, Bean, ?, ?> element) {
        elements.add(element);
    }

    public Type readFromXML(Reader reader) throws XMLException {
        XMLParserImpl parser = new XMLParserImpl(reader);
        return parser.parseDocument(this);
    }

    public Type readFromXML(Path path) throws IOException, XMLException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return readFromXML(reader);
        }
    }

    public Type fromXML(String xml) throws XMLException {
        try (StringReader reader = new StringReader(xml)) {
            return readFromXML(reader);
        }
    }

    public void writeToXML(Writer writer, Type object) throws XMLException {
        XMLDumperImpl dumper = new XMLDumperImpl(writer);
        dumper.dumpDocument(this, object);
    }

    public void writeToXML(Path path, Type object) throws IOException, XMLException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writeToXML(writer, object);
        }
    }

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
