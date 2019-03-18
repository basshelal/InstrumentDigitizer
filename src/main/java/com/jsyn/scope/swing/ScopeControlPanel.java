/*
 * Copyright 2009 Phil Burk, Mobileer Inc
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

package com.jsyn.scope.swing;

import com.jsyn.scope.AudioScopeModel;

import javax.swing.*;
import java.awt.*;

public class ScopeControlPanel extends JPanel {
    private static final long serialVersionUID = 7738305116057614812L;
    private AudioScopeModel audioScopeModel;
    private ScopeTriggerPanel triggerPanel;
    private JPanel probeRows;

    public ScopeControlPanel(AudioScopeView audioScopeView) {
        setLayout(new GridLayout(0, 1));
        this.audioScopeModel = audioScopeView.getModel();
        triggerPanel = new ScopeTriggerPanel(audioScopeModel);
        add(triggerPanel);

        probeRows = new JPanel();
        probeRows.setLayout(new GridLayout(1, 0));
        add(probeRows);
        for (AudioScopeProbeView probeView : audioScopeView.getProbeViews()) {
            ScopeProbePanel probePanel = new ScopeProbePanel(probeView);
            probeRows.add(probePanel);
        }
    }

}