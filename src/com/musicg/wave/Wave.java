/*
 * Copyright (C) 2011 Jacquet Wong
 * Copyright (C) 2015 Oliver Sampson
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.knime.core.util.FileUtil;

import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.extension.NormalizedSampleAmplitudes;
import com.musicg.wave.extension.Spectrogram;

/**
 * @author Jacquet Wong
 * @author Oliver Sampson, University of Konstanz
 */
public class Wave implements Serializable {

    private static final long serialVersionUID = 1L;
    private WaveHeader m_waveHeader;
    private byte[] m_data; // little endian
    private byte[] fingerprint;

    /**
     * Constructor
     * 
     */
    public Wave() {
        this.m_waveHeader = new WaveHeader();
        this.m_data = new byte[0];
    }

    /**
     * Constructor
     * 
     * @param filename
     *            Wave file
     * @throws IOException
     */
    public Wave(String filename) throws IOException {
        FileInputStream inputStream = new FileInputStream(filename);
        initWaveWithFileInputStream(inputStream);
        inputStream.close();
    }

    private void initWaveWithFileInputStream(FileInputStream in)
            throws IOException {
        this.m_waveHeader = new WaveHeader(in);
        if (this.m_waveHeader.isValid()) {
            this.m_data = new byte[in.available()];
            in.read(this.m_data);
        }

    }

    /**
     * Constructor.
     * 
     * @param inputStream
     *            Wave file input stream
     * @throws IOException
     *             IO exception
     */
    public Wave(InputStream inputStream) throws IOException {
        initWaveWithInputStream(inputStream);
    }

    /**
     * Constructor.
     * 
     * @param waveHeader
     *            the WaveHeader
     * @param data
     *            the audio data
     */
    public Wave(WaveHeader waveHeader, byte[] data) {
        this.m_waveHeader = waveHeader;
        this.m_data = data;
    }

    /**
     * Constructor with {@link File}.
     * 
     * @param f
     *            the file to load into the WAV object
     * @throws IOException
     *             IO exception
     */
    public Wave(File f) throws IOException {
        InputStream is = new FileInputStream(f);
        initWaveWithInputStream(is);
        is.close();
    }

    private void initWaveWithInputStream(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileUtil.copy(in, out);
        out.close();

        byte[] buffer = out.toByteArray();

        this.m_waveHeader = new WaveHeader(Arrays.copyOf(buffer,
                WaveHeader.HEADER_BYTE_LENGTH));

        this.m_data = Arrays.copyOfRange(buffer, WaveHeader.HEADER_BYTE_LENGTH,
                buffer.length);

    }

    /**
     * Trim the wave data
     * 
     * @param leftTrimNumberOfSample
     *            Number of sample trimmed from beginning
     * @param rightTrimNumberOfSample
     *            Number of sample trimmed from ending
     */
    public void trim(int leftTrimNumberOfSample, int rightTrimNumberOfSample) {

        long chunkSize = this.m_waveHeader.getChunkSize();
        long subChunk2Size = this.m_waveHeader.getSubChunk2Size();

        long totalTrimmed = leftTrimNumberOfSample + rightTrimNumberOfSample;

        if (totalTrimmed > subChunk2Size) {
            leftTrimNumberOfSample = (int) subChunk2Size;
        }

        // update wav info
        chunkSize -= totalTrimmed;
        subChunk2Size -= totalTrimmed;

        if (chunkSize >= 0 && subChunk2Size >= 0) {
            this.m_waveHeader.setChunkSize(chunkSize);
            this.m_waveHeader.setSubChunk2Size(subChunk2Size);

            byte[] trimmedData = new byte[(int) subChunk2Size];
            System.arraycopy(this.m_data, (int) leftTrimNumberOfSample,
                    trimmedData, 0, (int) subChunk2Size);
            this.m_data = trimmedData;
        } else {
            System.err.println("Trim error: Negative length");
        }
    }

    /**
     * Trim the wave data from beginning
     * 
     * @param numberOfSample
     *            numberOfSample trimmed from beginning
     */
    public void leftTrim(int numberOfSample) {
        trim(numberOfSample, 0);
    }

    /**
     * Trim the wave data from ending
     * 
     * @param numberOfSample
     *            numberOfSample trimmed from ending
     */
    public void rightTrim(int numberOfSample) {
        trim(0, numberOfSample);
    }

    /**
     * Trim the wave data
     * 
     * @param leftTrimSecond
     *            Seconds trimmed from beginning
     * @param rightTrimSecond
     *            Seconds trimmed from ending
     */
    public void trim(double leftTrimSecond, double rightTrimSecond) {

        int sampleRate = this.m_waveHeader.getSampleRate();
        int bitsPerSample = this.m_waveHeader.getBitsPerSample();
        int channels = this.m_waveHeader.getChannels();

        int leftTrimNumberOfSample = (int) (sampleRate * bitsPerSample / 8
                * channels * leftTrimSecond);
        int rightTrimNumberOfSample = (int) (sampleRate * bitsPerSample / 8
                * channels * rightTrimSecond);

        trim(leftTrimNumberOfSample, rightTrimNumberOfSample);
    }

    /**
     * Trim the wave data from beginning
     * 
     * @param second
     *            Seconds trimmed from beginning
     */
    public void leftTrim(double second) {
        trim(second, 0);
    }

    /**
     * Trim the wave data from ending
     * 
     * @param second
     *            Seconds trimmed from ending
     */
    public void rightTrim(double second) {
        trim(0, second);
    }

    /**
     * Get the wave header
     * 
     * @return waveHeader
     */
    public WaveHeader getWaveHeader() {
        return this.m_waveHeader;
    }

    /**
     * Get the wave spectrogram
     * 
     * @return spectrogram
     */
    public Spectrogram getSpectrogram() {
        return new Spectrogram(this);
    }

    /**
     * Get the wave spectrogram
     * 
     * @param fftSampleSize
     *            number of sample in fft, the value needed to be a number to
     *            power of 2
     * @param overlapFactor
     *            1/overlapFactor overlapping, e.g. 1/4=25% overlapping, 0 for
     *            no overlapping
     * 
     * @return spectrogram
     */
    public Spectrogram getSpectrogram(int fftSampleSize, int overlapFactor) {
        return new Spectrogram(this, fftSampleSize, overlapFactor);
    }

    /**
     * Get the wave data in bytes
     * 
     * @return wave data
     */
    public byte[] getBytes() {
        return this.m_data;
    }

    /**
     * Data byte size of the wave excluding header size
     * 
     * @return byte size of the wave
     */
    public int size() {
        return this.m_data.length;
    }

    /**
     * Length of the wave in second
     * 
     * @return length in second
     */
    public float length() {
        float second = (float) this.m_waveHeader.getSubChunk2Size()
                / this.m_waveHeader.getByteRate();
        return second;
    }

    /**
     * Timestamp of the wave length
     * 
     * @return timestamp
     */
    public String timestamp() {
        float totalSeconds = this.length();
        float second = totalSeconds % 60;
        int minute = (int) totalSeconds / 60 % 60;
        int hour = (int) (totalSeconds / 3600);

        StringBuffer sb = new StringBuffer();
        if (hour > 0) {
            sb.append(hour + ":");
        }
        if (minute > 0) {
            sb.append(minute + ":");
        }
        sb.append(second);

        return sb.toString();
    }

    /**
     * Get the amplitudes of the wave samples (depends on the header)
     * 
     * @return amplitudes array (signed 16-bit)
     */
    public short[] getSampleAmplitudes() {
        int bytePerSample = this.m_waveHeader.getBitsPerSample() / 8;
        int numSamples = this.m_data.length / bytePerSample;
        short[] amplitudes = new short[numSamples];

        int pointer = 0;
        for (int i = 0; i < numSamples; i++) {
            short amplitude = 0;
            for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
                // little endian
                amplitude |= (short) ((this.m_data[pointer++] & 0xFF) << (byteNumber * 8));
            }
            amplitudes[i] = amplitude;
        }

        return amplitudes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(this.m_waveHeader.toString());
        sb.append("\n");
        sb.append("length: " + timestamp());
        return sb.toString();
    }

    /**
     * @return
     */
    public double[] getNormalizedAmplitudes() {
        NormalizedSampleAmplitudes amplitudes = new NormalizedSampleAmplitudes(
                this);
        return amplitudes.getNormalizedAmplitudes();
    }

    /**
     * @return
     */
    public byte[] getFingerprint() {
        if (this.fingerprint == null) {
            FingerprintManager fingerprintManager = new FingerprintManager();
            this.fingerprint = fingerprintManager.extractFingerprint(this);
        }
        return this.fingerprint;
    }

    /**
     * @param wave
     * @return
     */
    public FingerprintSimilarity getFingerprintSimilarity(Wave wave) {
        FingerprintSimilarityComputer fingerprintSimilarityComputer = new FingerprintSimilarityComputer(
                this.getFingerprint(), wave.getFingerprint());
        return fingerprintSimilarityComputer.getFingerprintsSimilarity();
    }

    /**
     * Save the wave file using a filename.
     * 
     * @param filename
     *            filename to be saved
     * @throws IOException
     *             IO Exception
     * 
     */
    public void save(String filename) throws IOException {

        FileOutputStream fos = null;
        fos = new FileOutputStream(filename);
        save(fos);
        fos.close();
    }

    /**
     * Save the wave file using an output stream
     * 
     * @param os
     *            the output stream to save the file to
     * @throws IOException
     *             IOException
     */
    public void save(OutputStream os) throws IOException {

        this.m_waveHeader.save(os);
        os.write(this.getBytes());
    }

    /**
     * @return the WAV header in packed format
     */
    public byte[] getHeaderBuffer() {

        return this.m_waveHeader.getBytes();
    }

    /**
     * @return the WAV header
     */
    public WaveHeader getHeader() {
        return this.m_waveHeader;
    }
}