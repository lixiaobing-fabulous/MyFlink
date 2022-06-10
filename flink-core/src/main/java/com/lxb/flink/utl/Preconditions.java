/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ----------------------------------------------------------------------------
//  This class is largely adapted from "com.google.common.base.Preconditions",
//  which is part of the "Guava" library.
//
//  Because of frequent issues with dependency conflicts, this class was
//  added to the Flink code base to reduce dependency on Guava.
// ----------------------------------------------------------------------------

package com.lxb.flink.utl;


public final class Preconditions {
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference,
                                     String errorMessageTemplate,
                                     Object... errorMessageArgs) {

        if (reference == null) {
            throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean condition, Object errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean condition,
                                     String errorMessageTemplate,
                                     Object... errorMessageArgs) {

        if (!condition) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkState(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean condition, Object errorMessage) {
        if (!condition) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static void checkState(boolean condition,
                                  String errorMessageTemplate,
                                  Object... errorMessageArgs) {

        if (!condition) {
            throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkElementIndex(int index, int size) {
        checkArgument(size >= 0, "Size was negative.");
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    public static void checkElementIndex(int index, int size, String errorMessage) {
        checkArgument(size >= 0, "Size was negative.");
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.valueOf(errorMessage) + " Index: " + index + ", Size: " + size);
        }
    }

    private static String format(String template, Object... args) {
        final int numArgs = args == null ? 0 : args.length;
        template = String.valueOf(template); // null -> "null"

        StringBuilder builder = new StringBuilder(template.length() + 16 * numArgs);
        int templateStart = 0;
        int i = 0;
        while (i < numArgs) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        if (i < numArgs) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < numArgs) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }

    // ------------------------------------------------------------------------

    /**
     * Private constructor to prevent instantiation.
     */
    private Preconditions() {
    }
}
