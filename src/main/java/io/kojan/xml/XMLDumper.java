/*-
 * Copyright (c) 2021-2024 Red Hat, Inc.
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
public interface XMLDumper {
    void dumpStartElement(String tag) throws XMLStreamException;

    void dumpEndElement() throws XMLStreamException;

    void dumpText(String text) throws XMLStreamException;

    <Type, Bean extends Builder<Type>> void dumpEntity(Entity<Type, Bean> entity, Type value) throws XMLStreamException;
}
