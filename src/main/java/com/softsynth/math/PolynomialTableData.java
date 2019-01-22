/*
 * Copyright 1997 Phil Burk, Mobileer Inc
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

package com.softsynth.math;

/**
 * PolynomialTableData<br>
 * Provides an array of double[] containing data generated by a polynomial.<br>
 * This is typically used with ChebyshevPolynomial. Input to Polynomial is -1..1, output is -1..1.
 *
 * @author Nick Didkovsky (C) 1997 Phil Burk and Nick Didkovsky
 * @see ChebyshevPolynomial
 * @see Polynomial
 */

public class PolynomialTableData {

    double[] data;
    Polynomial polynomial;

    /**
     * Constructor which fills double[numFrames] with Polynomial data -1..1<br>
     * Note that any Polynomial can plug in here, just make sure output is -1..1 when input ranges
     * from -1..1
     */
    public PolynomialTableData(Polynomial polynomial, int numFrames) {
        data = new double[numFrames];
        this.polynomial = polynomial;
        buildData();
    }

    public double[] getData() {
        return data;
    }

    void buildData() {
        double xInterval = 2.0 / (data.length - 1); // FIXED, added "- 1"
        double x;
        for (int i = 0; i < data.length; i++) {
            x = i * xInterval - 1.0;
            data[i] = polynomial.evaluate(x);
            // System.out.println("x = " + x + ", p(x) = " + data[i] );
        }

    }

    public static void main(String args[]) {
        PolynomialTableData chebData = new PolynomialTableData(ChebyshevPolynomial.T(2), 8);
    }

}
