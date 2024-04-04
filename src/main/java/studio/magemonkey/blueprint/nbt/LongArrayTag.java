/*
 * JNBT License
 *
 * Copyright (c) 2024 Mage Monkey Studios
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     * Neither the name of the JNBT team nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package studio.magemonkey.blueprint.nbt;

import java.util.Arrays;
import java.util.List;

/**
 * @author Frostalf
 */
public class LongArrayTag extends Tag {

    private final long[] value;

    public LongArrayTag(final long[] value) {
        super();
        this.value = value;
    }

    public LongArrayTag(List<Long> value) {
        super();
        this.value = toArray(value);
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    public int getTypeId() {
        return 12;
    }

    public long[] toArray(List<Long> list) {
        long[] array = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    @Override
    public String toString() {

        final StringBuilder longS = new StringBuilder();
        for (long b : value) {
            longS.append(b).append(" ");
        }
        return "TAG_Long_Array(" + longS.toString() + ")";
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime  = 31;
        int       result = super.hashCode();
        result = (prime * result) + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String asString() {
        StringBuilder var0 = new StringBuilder("[L;");
        for (int var1 = 0; var1 < this.value.length; ++var1) {
            if (var1 != 0) {
                var0.append(',');
            }
            var0.append(this.value[var1]).append('L');
        }
        return var0.append(']').toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof IntArrayTag)) {
            return false;
        }
        final LongArrayTag other = (LongArrayTag) obj;
        return Arrays.equals(value, other.value);
    }
}
