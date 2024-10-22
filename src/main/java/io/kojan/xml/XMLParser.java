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

import javax.xml.stream.XMLStreamException;

/** @author Mikolaj Izdebski */
public interface XMLParser {

    String parseText() throws XMLStreamException;

    boolean hasStartElement() throws XMLStreamException;

    boolean hasStartElement(String tag) throws XMLStreamException;

    String parseStartElement() throws XMLStreamException;

    void parseStartElement(String tag) throws XMLStreamException;

    void parseEndElement(String tag) throws XMLStreamException;

    <Type, Bean extends Builder<Type>> void parseEntity(Entity<Type, Bean> entity, Bean bean) throws XMLStreamException;
}
