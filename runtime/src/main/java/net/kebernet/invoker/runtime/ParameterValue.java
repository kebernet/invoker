/**
 *    Copyright (c) 2016 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.kebernet.invoker.runtime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by rcooper on 10/13/16.
 */
public class ParameterValue {
    private final String name;
    private final Object value;

    public ParameterValue(@Nonnull String name, @Nullable Object value) {
        this.name = name;
        this.value = value;
    }

    public @Nonnull String getName() {
        return name;
    }

    public @Nullable Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterValue that = (ParameterValue) o;

        if (!name.equals(that.name)) return false;
        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
