/*
 *     Copyright (C) 2015  higherfrequencytrading.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.openhft.chronicle.wire;

import net.openhft.chronicle.bytes.Bytes;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Collection;

import static net.openhft.chronicle.bytes.NativeBytes.nativeBytes;

@RunWith(value = Parameterized.class)
public class BinaryWirePerfTest {
    final int testId;
    final boolean fixed;
    final boolean numericField;
    final boolean fieldLess;
    @NotNull
    Bytes bytes = nativeBytes();

    public BinaryWirePerfTest(int testId, boolean fixed, boolean numericField, boolean fieldLess) {
        this.testId = testId;
        this.fixed = fixed;
        this.numericField = numericField;
        this.fieldLess = fieldLess;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> combinations() {
        return Arrays.asList(
                new Object[]{0, false, false, false},
                new Object[]{1, true, false, false},
                new Object[]{2, false, true, false},
                new Object[]{3, true, true, false},
                new Object[]{4, false, false, true},
                new Object[]{5, true, false, true}
        );
    }

    @NotNull
    private Wire createBytes() {
        bytes.clear();
        if (testId == -1)
            return new RawWire(bytes);
        return new BinaryWire(bytes, fixed, numericField, fieldLess);
    }

    @Test
    public void wirePerf() throws StreamCorruptedException {
        System.out.println("TestId: " + testId + ", fixed: " + fixed + ", numberField: " + numericField + ", fieldLess: " + fieldLess);
        Wire wire = createBytes();
        MyTypes a = new MyTypes();
        for (int t = 0; t < 3; t++) {
            a.text.setLength(0);
            a.text.append("Hello World");
            wirePerf0(wire, a, new MyTypes(), t);
        }
    }

    private void wirePerf0(@NotNull Wire wire, @NotNull MyTypes a, @NotNull MyTypes b, int t) throws StreamCorruptedException {
        long start = System.nanoTime();
        int runs = 200000;
        for (int i = 0; i < runs; i++) {
            wire.clear();
            a.b = (i & 1) != 0;
            a.d = i;
            a.i = i;
            a.l = i;
            a.writeMarshallable(wire);

            b.readMarshallable(wire);
        }
        long rate = (System.nanoTime() - start) / runs;
        System.out.printf("(vars) %,d : %,d ns avg, len= %,d%n", t, rate, wire.bytes().readPosition());
    }

    @Test
    public void wirePerfInts() {
        System.out.println("TestId: " + testId + ", fixed: " + fixed + ", numberField: " + numericField + ", fieldLess: " + fieldLess);
        Wire wire = createBytes();
        MyType2 a = new MyType2();
        for (int t = 0; t < 3; t++) {
            wirePerf0(wire, a, new MyType2(), t);
        }
    }

    private void wirePerf0(@NotNull Wire wire, @NotNull MyType2 a, @NotNull MyType2 b, int t) {
        long start = System.nanoTime();
        int runs = 300000;
        for (int i = 0; i < runs; i++) {
            wire.clear();
            a.i = i;
            a.l = i;
            a.writeMarshallable(wire);

            b.readMarshallable(wire);
        }
        long rate = (System.nanoTime() - start) / runs;
        System.out.printf("(ints) %,d : %,d ns avg, len= %,d%n", t, rate, wire.bytes().readPosition());
    }

    static class MyType2 implements Marshallable {
        int i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x;

        @Override
        public void writeMarshallable(@NotNull WireOut wire) {
            wire.write(Fields.I).int32(i)
                    .write(Fields.J).int32(j)
                    .write(Fields.K).int32(k)
                    .write(Fields.L).int32(l)
                    .write(Fields.M).int32(m)
                    .write(Fields.N).int32(n)
                    .write(Fields.O).int32(o)
                    .write(Fields.P).int32(p)

                    .write(Fields.Q).int32(q)
                    .write(Fields.R).int32(r)
                    .write(Fields.S).int32(s)
                    .write(Fields.T).int32(t)
                    .write(Fields.U).int32(u)
                    .write(Fields.V).int32(v)
                    .write(Fields.W).int32(w)
                    .write(Fields.X).int32(v)
            ;
        }

        @Override
        public void readMarshallable(@NotNull WireIn wire) {
            wire.read(Fields.I).int32(this, (o, x) -> o.i = x)
                    .read(Fields.J).int32(this, (o, x) -> o.j = x)
                    .read(Fields.K).int32(this, (o, x) -> o.k = x)
                    .read(Fields.L).int32(this, (o, x) -> o.l = x)
                    .read(Fields.M).int32(this, (o, x) -> o.m = x)
                    .read(Fields.N).int32(this, (o, x) -> o.n = x)
                    .read(Fields.O).int32(this, (t, x) -> t.o = x)
                    .read(Fields.P).int32(this, (o, x) -> o.p = x)
                    .read(Fields.Q).int32(this, (o, x) -> o.q = x)
                    .read(Fields.R).int32(this, (o, x) -> o.r = x)
                    .read(Fields.S).int32(this, (o, x) -> o.s = x)
                    .read(Fields.T).int32(this, (o, x) -> o.t = x)
                    .read(Fields.U).int32(this, (o, x) -> o.u = x)
                    .read(Fields.V).int32(this, (o, x) -> o.v = x)
                    .read(Fields.W).int32(this, (o, x) -> o.w = x)
                    .read(Fields.X).int32(this, (o, x) -> o.x = x)
            ;
        }

        enum Fields implements WireKey {
            I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X;

            @Override
            public int code() {
                return ordinal();
            }
        }
    }
}