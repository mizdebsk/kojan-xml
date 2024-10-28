/*-
 * Copyright (c) 2020-2021 Red Hat, Inc.
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

import java.util.function.Function;

/**
 * Attribute of an {@link Entity}. A simple {@link Property} with text representation.
 *
 * <p>Attribute values have a specified Java type. There are converter {@link Function}s that allow to convert attribute
 * values to and from their text ({@link String}) representation.
 *
 * <p>When stored in XML form, an attribute is represented by a XML element with specified tag. Text content of the
 * element specifies property value.
 *
 * @param <EnclosingType> data type of entity
 * @param <EnclosingBean> type of bean associated with the entity
 * @param <AttributeType> data type of attribute value
 * @author Mikolaj Izdebski
 */
public class Attribute<EnclosingType, EnclosingBean, AttributeType>
        extends Property<EnclosingType, EnclosingBean, AttributeType> {
    private final Function<AttributeType, String> toStringAdapter;
    private final Function<String, AttributeType> fromStringAdapter;

    /**
     * Creates a unique, non-optional String attribute.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param tag attribute XML tag name
     * @param getter entity bean getter method that returns value of the attribute
     * @param setter entity bean setter method that returns value of the attribute
     * @return created attribute
     */
    public static <Type, Bean> Attribute<Type, Bean, String> of(
            String tag, Getter<Type, String> getter, Setter<Bean, String> setter) {
        return of(tag, getter, setter, Function.identity(), Function.identity());
    }

    /**
     * Creates a unique, non-optional attribute.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param <AttributeType> type of attribute value
     * @param tag attribute XML tag name
     * @param getter entity bean getter method that returns value of the attribute
     * @param setter entity bean setter method that returns value of the attribute
     * @param toStringAdapter function that converts attribute value into a text form
     * @param fromStringAdapter function that converts attribute value from a text form
     * @return created attribute
     */
    public static <Type, Bean, AttributeType> Attribute<Type, Bean, AttributeType> of(
            String tag,
            Getter<Type, AttributeType> getter,
            Setter<Bean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter) {
        return new Attribute<>(
                tag, x -> singleton(getter.get(x)), setter, toStringAdapter, fromStringAdapter, false, true);
    }

    /**
     * Creates a unique, optional String attribute.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param tag attribute XML tag name
     * @param getter entity bean getter method that returns value of the attribute
     * @param setter entity bean setter method that returns value of the attribute
     * @return created attribute
     */
    public static <Type, Bean> Attribute<Type, Bean, String> ofOptional(
            String tag, Getter<Type, String> getter, Setter<Bean, String> setter) {
        return ofOptional(tag, getter, setter, Function.identity(), Function.identity());
    }

    /**
     * Creates a unique, optional attribute.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param <AttributeType> type of attribute value
     * @param tag attribute XML tag name
     * @param getter entity bean getter method that returns value of the attribute
     * @param setter entity bean setter method that returns value of the attribute
     * @param toStringAdapter function that converts attribute value into a text form
     * @param fromStringAdapter function that converts attribute value from a text form
     * @return created attribute
     */
    public static <Type, Bean, AttributeType> Attribute<Type, Bean, AttributeType> ofOptional(
            String tag,
            Getter<Type, AttributeType> getter,
            Setter<Bean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter) {
        return new Attribute<>(
                tag, x -> singleton(getter.get(x)), setter, toStringAdapter, fromStringAdapter, true, true);
    }

    /**
     * Creates a non-unique, optional String attribute.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param tag attribute XML tag name
     * @param getter entity bean getter method that returns value of the attribute
     * @param setter entity bean setter method that returns value of the attribute
     * @return created attribute
     */
    public static <Type, Bean> Attribute<Type, Bean, String> ofMulti(
            String tag, Getter<Type, Iterable<String>> getter, Setter<Bean, String> setter) {
        return ofMulti(tag, getter, setter, Function.identity(), Function.identity());
    }

    /**
     * Creates a non-unique, optional attribute.
     *
     * @param <Type> data type of entity
     * @param <Bean> type of bean associated with the entity
     * @param <AttributeType> type of attribute value
     * @param tag attribute XML tag name
     * @param getter entity bean getter method that returns value of the attribute
     * @param setter entity bean setter method that returns value of the attribute
     * @param toStringAdapter function that converts attribute value into a text form
     * @param fromStringAdapter function that converts attribute value from a text form
     * @return created attribute
     */
    public static <Type, Bean, AttributeType> Attribute<Type, Bean, AttributeType> ofMulti(
            String tag,
            Getter<Type, Iterable<AttributeType>> getter,
            Setter<Bean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter) {
        return new Attribute<>(tag, getter, setter, toStringAdapter, fromStringAdapter, true, false);
    }

    /**
     * Creates an attribute of an entity.
     *
     * @param tag XML element tag name used to serialize the attribute in XML form (see {@link #getTag})
     * @param getter attribute getter method
     * @param setter attribute setter method
     * @param toStringAdapter function that converts attribute value into a text form
     * @param fromStringAdapter function that converts attribute value from a text form
     * @param optional whether the attribute is optional (see {@link #isOptional})
     * @param unique whether the attribute is unique (see {@link #isUnique})
     */
    public Attribute(
            String tag,
            Getter<EnclosingType, Iterable<AttributeType>> getter,
            Setter<EnclosingBean, AttributeType> setter,
            Function<AttributeType, String> toStringAdapter,
            Function<String, AttributeType> fromStringAdapter,
            boolean optional,
            boolean unique) {
        super(tag, getter, setter, optional, unique);
        this.toStringAdapter = toStringAdapter;
        this.fromStringAdapter = fromStringAdapter;
    }

    @Override
    protected void dump(XMLDumper dumper, AttributeType value) throws XMLException {
        dumper.dumpStartElement(getTag());
        dumper.dumpText(toStringAdapter.apply(value));
        dumper.dumpEndElement();
    }

    @Override
    protected AttributeType parse(XMLParser parser) throws XMLException {
        parser.parseStartElement(getTag());
        String text = parser.parseText();
        parser.parseEndElement(getTag());
        return fromStringAdapter.apply(text);
    }
}
