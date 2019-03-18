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

package com.jsyn.unitgen;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;

/**
 * ParabolicEnvelope unit. Output goes from zero to amplitude then back to zero in a parabolic arc.
 * <P>
 * Generate a short parabolic envelope that could be used for granular synthesis. The output starts
 * at zero, peaks at the value of amplitude then returns to zero. This unit has two states, IDLE and
 * RUNNING. If a trigger is received when IDLE, the envelope is started and another trigger is sent
 * out the triggerOutput port. This triggerOutput can be used to latch values for the synthesis of a
 * grain. If a trigger is received when RUNNING, then it is ignored and passed out the triggerPass
 * port. The triggerPass can be connected to the triggerInput of another ParabolicEnvelope. Thus you
 * can implement a simple grain allocation scheme by daisy chaining the triggers of
 * ParabolicEnvelopes.
 * <P>
 * The envelope is generated by a double integrator method so it uses relatively little CPU time.
 *
 * @author (C) 1997 Phil Burk, SoftSynth.com
 * @see EnvelopeDAHDSR
 */
public class ParabolicEnvelope extends UnitGenerator {

    /**
     * Fastest repeat rate of envelope if it were continually retriggered in Hertz.
     */
    public UnitInputPort frequency;
    /**
     * True value triggers envelope when in resting state.
     */
    public UnitInputPort triggerInput;
    public UnitInputPort amplitude;

    /**
     * Trigger output when envelope started.
     */
    public UnitOutputPort triggerOutput;
    /**
     * Input trigger passed out if ignored for daisy chaining.
     */
    public UnitOutputPort triggerPass;
    public UnitOutputPort output;

    private double slope;
    private double curve;
    private double level;
    private boolean running;

    /* Define Unit Ports used by connect() and set(). */
    public ParabolicEnvelope() {
        addPort(triggerInput = new UnitInputPort("Input"));
        addPort(frequency = new UnitInputPort("Frequency", UnitOscillator.DEFAULT_FREQUENCY));
        addPort(amplitude = new UnitInputPort("Amplitude", UnitOscillator.DEFAULT_AMPLITUDE));

        addPort(output = new UnitOutputPort("Output"));
        addPort(triggerOutput = new UnitOutputPort("TriggerOutput"));
        addPort(triggerPass = new UnitOutputPort("TriggerPass"));
    }

    @Override
    public void generate(int start, int limit) {
        double[] frequencies = frequency.getValues();
        double[] amplitudes = amplitude.getValues();
        double[] triggerInputs = triggerInput.getValues();
        double[] outputs = output.getValues();
        double[] triggerPasses = triggerPass.getValues();
        double[] triggerOutputs = triggerOutput.getValues();

        for (int i = start; i < limit; i++) {
            if (!running) {
                if (triggerInputs[i] > 0) {
                    double freq = frequencies[i] * synthesisEngine.getInverseNyquist();
                    freq = (freq > 1.0) ? 1.0 : ((freq < -1.0) ? -1.0 : freq);
                    double ampl = amplitudes[i];
                    double freq2 = freq * freq; /* Square frequency. */
                    slope = 4.0 * ampl * (freq - freq2);
                    curve = -8.0 * ampl * freq2;
                    level = 0.0;
                    triggerOutputs[i] = UnitGenerator.TRUE;
                    running = true;
                } else {
                    triggerOutputs[i] = UnitGenerator.FALSE;
                }
                triggerPasses[i] = UnitGenerator.FALSE;
            } else /* RUNNING */ {
                level += slope;
                slope += curve;
                if (level <= 0.0) {
                    level = 0.0;
                    running = false;
                    /* Autostop? - FIXME */
                }

                triggerOutputs[i] = UnitGenerator.FALSE;
                triggerPasses[i] = triggerInputs[i];
            }
            outputs[i] = level;
        }
    }
}