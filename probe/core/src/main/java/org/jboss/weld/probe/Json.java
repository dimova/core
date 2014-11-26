/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.probe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.weld.util.Preconditions;

/**
 * A simple JSON generator.
 *
 * @author Martin Kouba
 */
class Json {

    private static final String VALUE = "value";
    private static final String NAME = "name";
    private static final String OBJECT_START = "{";
    private static final String OBJECT_END = "}";
    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";
    private static final String NAME_VAL_SEPARATOR = ":";
    private static final String ENTRY_SEPARATOR = ",";

    private static final char CHAR_QUOTATION_MARK = '"';
    private static final char CHAR_REVERSE_SOLIDUS = '\\';

    private Json() {
    }

    public static JsonArrayBuilder newArrayBuilder() {
        return new JsonArrayBuilder();
    }

    public static JsonObjectBuilder newObjectBuilder() {
        return new JsonObjectBuilder();
    }

    abstract static class JsonBuilder {

        protected boolean ignoreEmptyBuilders = false;

        /**
         *
         * @return <code>true</code> if there are no elements/properties, <code>false</code> otherwise
         */
        abstract boolean isEmpty();

        /**
         *
         * @return a string representation
         */
        abstract String build();

        /**
         *
         * @param value
         * @return <code>true</code> if the value is an empty builder and {@link #ignoreEmptyBuilders} is set to <code>true</code>, <code>false</code> otherwise
         */
        protected boolean shouldBeIgnored(Object value) {
            if (ignoreEmptyBuilders && value instanceof JsonBuilder) {
                JsonBuilder jsonBuilder = (JsonBuilder) value;
                if (jsonBuilder.isEmpty()) {
                    return true;
                }
            }
            return false;
        }

    }

    static class JsonArrayBuilder extends JsonBuilder {

        private final List<Object> values;

        private JsonArrayBuilder() {
            this.values = new ArrayList<Object>();
        }

        JsonArrayBuilder add(JsonArrayBuilder value) {
            addInternal(value);
            return this;
        }

        JsonArrayBuilder add(JsonObjectBuilder value) {
            addInternal(value);
            return this;
        }

        JsonArrayBuilder add(String value) {
            addInternal(value);
            return this;
        }

        JsonArrayBuilder add(Boolean value) {
            addInternal(value);
            return this;
        }

        JsonArrayBuilder add(Integer value) {
            addInternal(value);
            return this;
        }

        JsonArrayBuilder add(Long value) {
            addInternal(value);
            return this;
        }

        private void addInternal(Object value) {
            Preconditions.checkArgumentNotNull(value, VALUE);
            values.add(value);
        }

        boolean isEmpty() {
            return values.isEmpty();
        }

        String build() {
            // First remove the empty builders if required
            // We can't do this inside the main loop as we wouldn't be able to place entry separators correctly
            if (ignoreEmptyBuilders) {
                for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
                    if (shouldBeIgnored(iterator.next())) {
                        iterator.remove();
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(ARRAY_START);
            for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
                Object value = iterator.next();
                appendValue(builder, value);
                if (iterator.hasNext()) {
                    builder.append(ENTRY_SEPARATOR);
                }
            }
            builder.append(ARRAY_END);
            return builder.toString();
        }

    }

    static class JsonObjectBuilder extends JsonBuilder {

        private final Map<String, Object> properties;

        private JsonObjectBuilder() {
            this.properties = new LinkedHashMap<String, Object>();
        }

        JsonObjectBuilder setIgnoreEmptyBuilders(boolean value) {
            this.ignoreEmptyBuilders = value;
            return this;
        }

        JsonObjectBuilder add(String name, String value) {
            addInternal(name, value);
            return this;
        }

        JsonObjectBuilder add(String name, JsonObjectBuilder value) {
            addInternal(name, value);
            return this;
        }

        JsonObjectBuilder add(String name, JsonArrayBuilder value) {
            addInternal(name, value);
            return this;
        }

        JsonObjectBuilder add(String name, Boolean value) {
            addInternal(name, value);
            return this;
        }

        JsonObjectBuilder add(String name, Integer value) {
            addInternal(name, value);
            return this;
        }

        JsonObjectBuilder add(String name, Long value) {
            addInternal(name, value);
            return this;
        }

        private void addInternal(String name, Object value) {
            Preconditions.checkArgumentNotNull(name, NAME);
            Preconditions.checkArgumentNotNull(value, VALUE);
            properties.put(name, value);
        }

        boolean isEmpty() {
            return properties.isEmpty();
        }

        String build() {
            // First remove the empty builders if required
            // We can't do this inside the main loop as we wouldn't be able to place entry separators correctly
            if (ignoreEmptyBuilders) {
                for (Iterator<Entry<String, Object>> iterator = properties.entrySet().iterator(); iterator.hasNext();) {
                    if (shouldBeIgnored(iterator.next().getValue())) {
                        iterator.remove();
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(OBJECT_START);
            for (Iterator<Entry<String, Object>> iterator = properties.entrySet().iterator(); iterator.hasNext();) {
                Entry<String, Object> entry = iterator.next();
                appendStringValue(builder, entry.getKey());
                builder.append(NAME_VAL_SEPARATOR);
                appendValue(builder, entry.getValue());
                if (iterator.hasNext()) {
                    builder.append(ENTRY_SEPARATOR);
                }
            }
            builder.append(OBJECT_END);
            return builder.toString();
        }

    }

    static void appendValue(StringBuilder builder, Object value) {
        if (value instanceof JsonObjectBuilder) {
            builder.append(((JsonObjectBuilder) value).build());
        } else if (value instanceof JsonArrayBuilder) {
            builder.append(((JsonArrayBuilder) value).build());
        } else if (value instanceof String) {
            appendStringValue(builder, value.toString());
        } else if (value instanceof Boolean || value instanceof Integer || value instanceof Long) {
            builder.append(value.toString());
        } else {
            throw new IllegalStateException("Unsupported value type: " + value);
        }
    }

    static void appendStringValue(StringBuilder builder, String value) {
        builder.append(CHAR_QUOTATION_MARK);
        builder.append(escape(value));
        builder.append(CHAR_QUOTATION_MARK);
    }

    /**
     * TODO control characters (U+0000 through U+001F)
     *
     * @param value
     * @return escaped value
     * @see <a href="http://www.ietf.org/rfc/rfc4627.txt">http://www.ietf.org/rfc/rfc4627.txt</a>
     */
    static String escape(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case CHAR_REVERSE_SOLIDUS:
                case CHAR_QUOTATION_MARK:
                    builder.append(CHAR_REVERSE_SOLIDUS);
                    builder.append(c);
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

}
