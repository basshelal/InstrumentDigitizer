/*
 * Copyright 2013 Phil Burk, Mobileer Inc
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

package com.jsyn.unitgen;

/**
 * Periodically transform the complex input signal using an FFT to a complex spectral stream. This
 * is probably not as useful as the SpectralFFT, which outputs complete spectra.
 *
 * @author Phil Burk (C) 2013 Mobileer Inc
 * @see IFFT
 * @see SpectralFFT
 */
public class FFT extends FFTBase {
    public FFT() {
        super();
    }

    @Override
    protected int getSign() {
        return 1; // 1 for FFT
    }
}
