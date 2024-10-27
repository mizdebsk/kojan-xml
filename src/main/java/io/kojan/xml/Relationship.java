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

/**
 * Relationship of one {@link Entity} type to another. A complex {@link Property} with no simple text representation.
 *
 * <p>When stored in XML form, a relationship is represented by zero or more XML subtrees describing each of related
 * entity instances.
 *
 * @param <EnclosingType> data type of entity
 * @param <EnclosingBean> type of bean associated with the entity
 * @param <RelatedType> data type of related entity
 * @param <RelatedBean> type of bean of related entity
 * @author Mikolaj Izdebski
 */
public class Relationship<EnclosingType, EnclosingBean, RelatedType, RelatedBean extends Builder<RelatedType>>
        extends Property<EnclosingType, EnclosingBean, RelatedType, RelatedBean> {
    private final Entity<RelatedType, RelatedBean> relatedEntity;

    /**
     * Creates a relationship between two entities.
     *
     * @param relatedEntity entity that is related to
     * @param getter relationship getter method
     * @param setter relationship setter method
     * @param optional whether the relationship is optional (see {@link #isOptional})
     * @param unique whether the relationship is unique (see {@link #isUnique})
     */
    public Relationship(
            Entity<RelatedType, RelatedBean> relatedEntity,
            Getter<EnclosingType, Iterable<RelatedType>> getter,
            Setter<EnclosingBean, RelatedType> setter,
            boolean optional,
            boolean unique) {
        super(relatedEntity.getTag(), getter, setter, optional, unique);
        this.relatedEntity = relatedEntity;
    }

    @Override
    protected void dump(XMLDumper dumper, RelatedType value) throws XMLException {
        dumper.dumpEntity(relatedEntity, value);
    }

    @Override
    protected RelatedType parse(XMLParser parser) throws XMLException {
        RelatedBean relatedBean = relatedEntity.newBean();
        parser.parseEntity(relatedEntity, relatedBean);
        return relatedBean.build();
    }
}
