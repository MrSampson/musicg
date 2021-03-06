/*
 * Copyright (C) 2012 Jacquet Wong
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
package com.musicg.main.demo;

import java.io.IOException;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveFileManager;
import com.musicg.wave.WaveTrimmer;

public class WaveDemo {

    public static void main(String[] args) {

        String filename = "audio_work/cock_a_1.wav";
        String outFolder = "out";

        // create a wave object
        Wave wave = null;
        try {
            wave = new Wave(filename);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // print the wave header and info
        System.out.println(wave);

        // trim the wav

        WaveTrimmer t = new WaveTrimmer(wave);
        try {
            t.leftTrim(1);
            t.rightTrim(0.5F);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        wave = t.getWave();
        // save the trimmed wav
        try {
            wave.save(outFolder + "/out.wav");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}