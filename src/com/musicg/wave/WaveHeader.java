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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * WAV File Specification
 * http://www-mmsp.ece.mcgill.ca/documents/AudioFormats/WAVE/WAVE.html
 * 
 * @author Jacquet Wong
 * @author Oliver Sampson
 */
public class WaveHeader {

    /**
     * RIFF header type
     */
    public static final String RIFF_HEADER = "RIFF";
    /**
     * WAV header type
     */
    public static final String WAVE_HEADER = "WAVE";
    /**
     * FMT header type
     */
    public static final String FMT_HEADER = "fmt ";
    /**
     * Data header type
     */
    public static final String DATA_HEADER = "data";
    /**
     * 44 bytes for header
     */
    public static final int HEADER_BYTE_LENGTH = 44;

    private boolean m_valid;
    private String m_chunkId; // 4 bytes
    private long m_chunkSize; // unsigned 4 bytes, little endian
    private String m_format; // 4 bytes
    private String m_subChunk1Id; // 4 bytes
    private long m_subChunk1Size; // unsigned 4 bytes, little endian
    private int m_audioFormat; // unsigned 2 bytes, little endian
    private int m_channels; // unsigned 2 bytes, little endian
    private long m_sampleRate; // unsigned 4 bytes, little endian
    private long m_byteRate; // unsigned 4 bytes, little endian
    private int m_blockAlign; // unsigned 2 bytes, little endian
    private int m_bitsPerSample; // unsigned 2 bytes, little endian
    private String m_subChunk2Id; // 4 bytes
    private long m_subChunk2Size; // unsigned 4 bytes, little endian

    /**
     * Constructor.
     */
    public WaveHeader() {

    }

    /**
     * Constructor with stream
     * 
     * @param inputStream
     *            stream to get WAV file
     * @throws IOException
     *             IO exception
     */
    public WaveHeader(InputStream inputStream) throws IOException {
        this.m_valid = loadHeader(inputStream);
    }

    /**
     * Constructor with raw bytes of a header buffer.
     * 
     * @param headerBuffer
     *            the bytes of the header buffer.
     */
    public WaveHeader(byte[] headerBuffer) {
        parseHeader(headerBuffer);
    }

    void parseHeader(byte[] headerBuffer) {
        int pointer = 0;
        this.m_chunkId = new String(new byte[] { headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++] });
        // little endian
        this.m_chunkSize = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff << 24);
        this.m_format = new String(new byte[] { headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++] });
        this.m_subChunk1Id = new String(new byte[] { headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++] });
        this.m_subChunk1Size = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        this.m_audioFormat = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        this.m_channels = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        this.m_sampleRate = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        this.m_byteRate = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;
        this.m_blockAlign = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        this.m_bitsPerSample = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
        this.m_subChunk2Id = new String(new byte[] { headerBuffer[pointer++],
                headerBuffer[pointer++], headerBuffer[pointer++],
                headerBuffer[pointer++] });
        this.m_subChunk2Size = (long) (headerBuffer[pointer++] & 0xff)
                | (long) (headerBuffer[pointer++] & 0xff) << 8
                | (long) (headerBuffer[pointer++] & 0xff) << 16
                | (long) (headerBuffer[pointer++] & 0xff) << 24;

    }

    /**
     * @return byte array representation of the WAV header
     */
    public byte[] getBytes() {
        byte[] buffer = new byte[HEADER_BYTE_LENGTH];

        int byteRate = this.getByteRate();
        int audioFormat = this.getAudioFormat();
        int sampleRate = this.getSampleRate();
        int bitsPerSample = this.getBitsPerSample();
        int channels = this.getChannels();
        long chunkSize = this.getChunkSize();
        long subChunk1Size = this.getSubChunk1Size();
        long subChunk2Size = this.getSubChunk2Size();
        int blockAlign = this.getBlockAlign();

        int i = 0;
        for (byte b : RIFF_HEADER.getBytes()) {
            buffer[i++] = b;
        }

        for (byte b : new byte[] { (byte) (chunkSize), (byte) (chunkSize >> 8),
                (byte) (chunkSize >> 16), (byte) (chunkSize >> 24) }) {
            buffer[i++] = b;
        }

        for (byte b : WaveHeader.WAVE_HEADER.getBytes()) {
            buffer[i++] = b;
        }
        for (byte b : WaveHeader.FMT_HEADER.getBytes()) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (subChunk1Size),
                (byte) (subChunk1Size >> 8), (byte) (subChunk1Size >> 16),
                (byte) (subChunk1Size >> 24) }) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (audioFormat),
                (byte) (audioFormat >> 8) }) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (channels), (byte) (channels >> 8) }) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (sampleRate),
                (byte) (sampleRate >> 8), (byte) (sampleRate >> 16),
                (byte) (sampleRate >> 24) }) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (byteRate), (byte) (byteRate >> 8),
                (byte) (byteRate >> 16), (byte) (byteRate >> 24) }) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (blockAlign),
                (byte) (blockAlign >> 8) }) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (bitsPerSample),
                (byte) (bitsPerSample >> 8) }) {
            buffer[i++] = b;
        }
        for (byte b : WaveHeader.DATA_HEADER.getBytes()) {
            buffer[i++] = b;
        }
        for (byte b : new byte[] { (byte) (subChunk2Size),
                (byte) (subChunk2Size >> 8), (byte) (subChunk2Size >> 16),
                (byte) (subChunk2Size >> 24) }) {
            buffer[i++] = b;
        }

        return buffer;
    }

    private boolean loadHeader(InputStream inputStream) throws IOException {

        byte[] headerBuffer = new byte[HEADER_BYTE_LENGTH];

        inputStream.read(headerBuffer);

        parseHeader(headerBuffer);

        // FIXME Do header checks.

        // if (bitsPerSample != 8 && bitsPerSample != 16) {
        // System.err
        // .println("WaveHeader: only supports bitsPerSample 8 or 16");
        // return false;
        // }
        //
        // // check the format is support
        // if (chunkId.toUpperCase().equals(RIFF_HEADER)
        // && format.toUpperCase().equals(WAVE_HEADER) && audioFormat == 1) {
        // return true;
        // } else {
        // System.err.println("WaveHeader: Unsupported header format");
        // }

        return true;
    }

    /**
     * @param os
     *            OutputStream to write to
     * @throws IOException
     *             IO Exception
     */
    public void save(OutputStream os) throws IOException {
        int byteRate = this.getByteRate();
        int audioFormat = this.getAudioFormat();
        int sampleRate = this.getSampleRate();
        int bitsPerSample = this.getBitsPerSample();
        int channels = this.getChannels();
        long chunkSize = this.getChunkSize();
        long subChunk1Size = this.getSubChunk1Size();
        long subChunk2Size = this.getSubChunk2Size();
        int blockAlign = this.getBlockAlign();

        os.write(RIFF_HEADER.getBytes());
        // little endian
        os.write(new byte[] { (byte) (chunkSize), (byte) (chunkSize >> 8),
                (byte) (chunkSize >> 16), (byte) (chunkSize >> 24) });
        os.write(WaveHeader.WAVE_HEADER.getBytes());
        os.write(WaveHeader.FMT_HEADER.getBytes());
        os.write(new byte[] { (byte) (subChunk1Size),
                (byte) (subChunk1Size >> 8), (byte) (subChunk1Size >> 16),
                (byte) (subChunk1Size >> 24) });
        os.write(new byte[] { (byte) (audioFormat), (byte) (audioFormat >> 8) });
        os.write(new byte[] { (byte) (channels), (byte) (channels >> 8) });
        os.write(new byte[] { (byte) (sampleRate), (byte) (sampleRate >> 8),
                (byte) (sampleRate >> 16), (byte) (sampleRate >> 24) });
        os.write(new byte[] { (byte) (byteRate), (byte) (byteRate >> 8),
                (byte) (byteRate >> 16), (byte) (byteRate >> 24) });
        os.write(new byte[] { (byte) (blockAlign), (byte) (blockAlign >> 8) });
        os.write(new byte[] { (byte) (bitsPerSample),
                (byte) (bitsPerSample >> 8) });
        os.write(WaveHeader.DATA_HEADER.getBytes());
        os.write(new byte[] { (byte) (subChunk2Size),
                (byte) (subChunk2Size >> 8), (byte) (subChunk2Size >> 16),
                (byte) (subChunk2Size >> 24) });
    }

    /**
     * @return true if the header is a valid format
     */
    public boolean isValid() {
        return this.m_valid;
    }

    /**
     * @return chunk ID
     */
    public String getChunkId() {
        return this.m_chunkId;
    }

    /**
     * @return chunk size
     */
    public int getChunkSize() {
        return (int) this.m_chunkSize;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return this.m_format;
    }

    /**
     * @return
     */
    public String getSubChunk1Id() {
        return this.m_subChunk1Id;
    }

    /**
     * @return
     */
    public long getSubChunk1Size() {
        return this.m_subChunk1Size;
    }

    /**
     * @return
     */
    public int getAudioFormat() {
        return this.m_audioFormat;
    }

    /**
     * @return
     */
    public int getChannels() {
        return this.m_channels;
    }

    /**
     * @return
     */
    public int getSampleRate() {
        return (int) this.m_sampleRate;
    }

    /**
     * @return
     */
    public int getByteRate() {
        return (int) this.m_byteRate;
    }

    /**
     * @return
     */
    public int getBlockAlign() {
        return this.m_blockAlign;
    }

    /**
     * @return
     */
    public int getBitsPerSample() {
        return this.m_bitsPerSample;
    }

    /**
     * @return
     */
    public String getSubChunk2Id() {
        return this.m_subChunk2Id;
    }

    /**
     * @return
     */
    public int getSubChunk2Size() {
        return (int) this.m_subChunk2Size;
    }

    /**
     * @param sampleRate
     */
    public void setSampleRate(int sampleRate) {
        int newSubChunk2Size = (int) (this.m_subChunk2Size * sampleRate / this.m_sampleRate);
        // if num bytes for each sample is even, the size of newSubChunk2Size
        // also needed to be in even number
        if ((this.m_bitsPerSample / 8) % 2 == 0) {
            if (newSubChunk2Size % 2 != 0) {
                newSubChunk2Size++;
            }
        }

        this.m_sampleRate = sampleRate;
        this.m_byteRate = sampleRate * this.m_bitsPerSample / 8;
        this.m_chunkSize = newSubChunk2Size + 36;
        this.m_subChunk2Size = newSubChunk2Size;
    }

    /**
     * @param chunkId
     */
    public void setChunkId(String chunkId) {
        this.m_chunkId = chunkId;
    }

    /**
     * @param chunkSize
     */
    public void setChunkSize(long chunkSize) {
        this.m_chunkSize = chunkSize;
    }

    /**
     * @param format
     */
    public void setFormat(String format) {
        this.m_format = format;
    }

    /**
     * @param subChunk1Id
     */
    public void setSubChunk1Id(String subChunk1Id) {
        this.m_subChunk1Id = subChunk1Id;
    }

    /**
     * @param subChunk1Size
     */
    public void setSubChunk1Size(long subChunk1Size) {
        this.m_subChunk1Size = subChunk1Size;
    }

    /**
     * @param audioFormat
     */
    public void setAudioFormat(int audioFormat) {
        this.m_audioFormat = audioFormat;
    }

    /**
     * @param channels
     */
    public void setChannels(int channels) {
        this.m_channels = channels;
    }

    /**
     * @param byteRate
     */
    public void setByteRate(long byteRate) {
        this.m_byteRate = byteRate;
    }

    /**
     * @param blockAlign
     */
    public void setBlockAlign(int blockAlign) {
        this.m_blockAlign = blockAlign;
    }

    /**
     * @param bitsPerSample
     */
    public void setBitsPerSample(int bitsPerSample) {
        this.m_bitsPerSample = bitsPerSample;
    }

    /**
     * @param subChunk2Id
     */
    public void setSubChunk2Id(String subChunk2Id) {
        this.m_subChunk2Id = subChunk2Id;
    }

    /**
     * @param subChunk2Size
     */
    public void setSubChunk2Size(long subChunk2Size) {
        this.m_subChunk2Size = subChunk2Size;
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("chunkId: " + this.m_chunkId);
        sb.append("\n");
        sb.append("chunkSize: " + this.m_chunkSize);
        sb.append("\n");
        sb.append("format: " + this.m_format);
        sb.append("\n");
        sb.append("subChunk1Id: " + this.m_subChunk1Id);
        sb.append("\n");
        sb.append("subChunk1Size: " + this.m_subChunk1Size);
        sb.append("\n");
        sb.append("audioFormat: " + this.m_audioFormat);
        sb.append("\n");
        sb.append("channels: " + this.m_channels);
        sb.append("\n");
        sb.append("sampleRate: " + this.m_sampleRate);
        sb.append("\n");
        sb.append("byteRate: " + this.m_byteRate);
        sb.append("\n");
        sb.append("blockAlign: " + this.m_blockAlign);
        sb.append("\n");
        sb.append("bitsPerSample: " + this.m_bitsPerSample);
        sb.append("\n");
        sb.append("subChunk2Id: " + this.m_subChunk2Id);
        sb.append("\n");
        sb.append("subChunk2Size: " + this.m_subChunk2Size);
        return sb.toString();
    }
}
