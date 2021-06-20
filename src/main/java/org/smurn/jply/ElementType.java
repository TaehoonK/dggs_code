/*
 * Copyright 2011 Stefan C. Mueller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smurn.jply;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ElementType {

    /** Name of this type. */
    private final String name;
    /** Properties of this type. */
    private final List<Property> properties;
    /** Maps property names to property index. */
    private final Map<String, Integer> propertyMap;

    /**
     * Creates an instance.
     * @param name  Name of the element type.
     * @param properties Properties of the elements of this type.
     * Must not be {@code null}.
     * @throws NullPointerException if {@code name} or {@code properties} is
     * {@code null}.
     * @throws IllegalArgumentException if the properties don't have unique
     * names.
     */
    public ElementType(final String name, final List<Property> properties) {
        if (name == null) {
            throw new NullPointerException("name must not be null.");
        }
        if (properties == null) {
            throw new NullPointerException("properties must not be null.");
        }
        this.name = name;
        this.properties = Collections.unmodifiableList(
                new ArrayList<Property>(properties));

        HashMap<String, Integer> propertyMapTmp =
                new HashMap<String, Integer>();
        for (int i = 0; i < properties.size(); i++) {
            propertyMapTmp.put(properties.get(i).getName(), i);
        }
        this.propertyMap = Collections.unmodifiableMap(propertyMapTmp);
        if (propertyMap.size() != properties.size()) {
            throw new IllegalArgumentException(
                    "properties must have unique names.");
        }
    }

    /**
     * Creates an instance.
     * @param name  Name of the element type.
     * @param properties Properties of the elements of this type.
     * @throws NullPointerException if {@code name} or {@code properties} is
     * {@code null}.
     * @throws IllegalArgumentException if the properties don't have unique
     * names.
     */
    public ElementType(final String name, final Property... properties) {
        this(name, Arrays.asList(properties));
    }

    /**
     * Gets the name of the element type.
     * <p>Some names are given a specific meaning by the PLY specification and
     * should only be used accordingly. See {@link ElementType}.</p>
     * @return Name of the element type. Is never {@code null} or empty,
     * contains only ASCII characters.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all properties defined for this element type.
     * @return Immutable list of properties. I never {@code null}.
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Gets the map that maps from property name to the index of that
     * property in the {@link #getProperties()} list.
     * @return Immutable map.
     */
    Map<String, Integer> getPropertyMap() {
        return propertyMap;
    }

    /**
     * Represents the result of parsing the header line.
     */
    static class HeaderEntry {

        private final String name;
        private final int count;

        /**
         * Creates an instance.
         * @param name  Name of the element type.
         * @param count Number of elements of this type.
         */
        public HeaderEntry(final String name, final int count) {
            this.name = name;
            this.count = count;
        }

        /**
         * Gets the number of elements of the type.
         * @return Number of elements.
         */
        public int getCount() {
            return count;
        }

        /**
         * Gets the name of the type.
         * @return Name of the type.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Parses a header line starting an element description.
     * @param elementLine Header line.
     * @return ElementType without properties.
     * @throws IOException if the header line has an invalid format.
     */
    static HeaderEntry parse(final String elementLine) throws IOException {
        if (!elementLine.startsWith("element ")) {
            throw new IOException("not an element: '"
                    + elementLine + "'");
        }
        String definition = elementLine.substring("element ".length());

        String[] parts = definition.split(" +", 2);
        if (parts.length != 2) {
            throw new IOException("Expected two parts in element definition: '"
                    + elementLine + "'");
        }
        String name = parts[0];
        String countStr = parts[1];
        int count;
        try {
            count = Integer.parseInt(countStr);
        }
        catch (NumberFormatException e) {
            throw new IOException("Invalid element entry. Not an integer: '"
                    + countStr + "'.");
        }
        return new HeaderEntry(name, count);
    }

    @Override
    public String toString() {
        return "element " + name + " properties=" + properties;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ElementType rhs = (ElementType) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(name, rhs.name);
        builder.append(properties, rhs.properties);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(name);
        builder.append(properties);
        return builder.toHashCode();
    }
}
