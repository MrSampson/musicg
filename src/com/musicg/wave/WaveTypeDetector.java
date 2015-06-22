/*
 * Copyright (C) 2011 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.musicg.wave;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.musicg.api.WhistleApi;

/**
 * @author Jacquet Wong
 *
 */
public class WaveTypeDetector {

    private Wave wave;

    /**
     * @param wave WAV file
     */
    public WaveTypeDetector(Wave wave) {
        this.wave = wave;
    }

    /**
     * @return
     */
    public double getWhistleProbability() {

        double probability = 0;

        WaveHeader wavHeader = wave.getWaveHeader();

        // fft size 1024, no overlap
        int fftSampleSize = 1024;
        int fftSignalByteLength = fftSampleSize * wavHeader.getBitsPerSample()
                / 8;
        byte[] audioBytes = wave.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);

        WhistleApi whistleApi = new WhistleApi(wavHeader);

        // read the byte signals
        try {
            int numFrames = inputStream.available() / fftSignalByteLength;
            byte[] bytes = new byte[fftSignalByteLength];
            int checkLength = 3;
            int passScore = 3;

            ArrayList<Boolean> bufferList = new ArrayList<Boolean>();
            int numWhistles = 0;
            int numPasses = 0;

            // first 10(checkLength) frames
            for (int frameNumber = 0; frameNumber < checkLength; frameNumber++) {
                inputStream.read(bytes);
                boolean isWhistle = whistleApi.isWhistle(bytes);
                bufferList.add(isWhistle);
                if (isWhistle) {
                    numWhistles++;
                }
                if (numWhistles >= passScore) {
                    numPasses++;
                }
                // System.out.println(frameNumber+": "+numWhistles);
            }

            // other frames
            for (int frameNumber = checkLength; frameNumber < numFrames; frameNumber++) {
                inputStream.read(bytes);
                boolean isWhistle = whistleApi.isWhistle(bytes);
                if (bufferList.get(0)) {
                    numWhistles--;
                }
                bufferList.remove(0);
                bufferList.add(isWhistle);

                if (isWhistle) {
                    numWhistles++;
                }
                if (numWhistles >= passScore) {
                    numPasses++;
                }
                // System.out.println(frameNumber+": "+numWhistles);
            }
            probability = (double) numPasses / numFrames;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return probability;
    }
}