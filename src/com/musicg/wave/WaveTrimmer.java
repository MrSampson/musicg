package com.musicg.wave;

/**
 * @author Oliver Sampson, University of Konstanz
 *
 */
public class WaveTrimmer {
    private Wave m_wave = null;

    /**
     * Trim the wave data from beginning
     * 
     * @author Jacquet Wong
     * @param numberOfSample
     *            numberOfSample trimmed from beginning
     * @throws Exception 
     */
    public void leftTrim(int numberOfSample) throws Exception {
        trim(numberOfSample, 0);
    }

    /**
     * Trim the wave data from ending
     * 
     * @author Jacquet Wong
     * @param numberOfSample
     *            numberOfSample trimmed from ending
     * @throws Exception 
     */
    public void rightTrim(int numberOfSample) throws Exception {
        trim(0, numberOfSample);
    }

    /**
     * Trim the wave data
     * 
     * @author Jacquet Wong
     * @param leftTrimSecond
     *            Seconds trimmed from beginning
     * @param rightTrimSecond
     *            Seconds trimmed from ending
     * @throws Exception 
     */
    public void trim(double leftTrimSecond, double rightTrimSecond) throws Exception {

        int sampleRate = this.m_wave.getHeader().getSampleRate();
        int bitsPerSample = this.m_wave.getHeader().getBitsPerSample();
        int channels = this.m_wave.getHeader().getChannels();

        int leftTrimNumberOfSample = (int) (sampleRate * bitsPerSample / 8
                * channels * leftTrimSecond);
        int rightTrimNumberOfSample = (int) (sampleRate * bitsPerSample / 8
                * channels * rightTrimSecond);

        trim(leftTrimNumberOfSample, rightTrimNumberOfSample);
    }

    /**
     * Trim the wave data from beginning
     * 
     * @author Jacquet Wong
     * @param second
     *            Seconds trimmed from beginning
     * @throws Exception 
     */
    public void leftTrim(double second) throws Exception {
        trim(second, 0);
    }

    /**
     * Trim the wave data from ending
     * 
     * @author Jacquet Wong
     * @param second
     *            Seconds trimmed from ending
     * @throws Exception 
     */
    public void rightTrim(double second) throws Exception {
        trim(0, second);
    }
    
    /**
     * Constructor with the WAV object
     * @param w a WAV object
     */
    public WaveTrimmer(Wave w){
        this.m_wave  = w;
    }
    
    /**
     * Trim the wave data
     * 
     * @param leftTrimNumberOfSample
     *            Number of sample trimmed from beginning
     * @param rightTrimNumberOfSample
     *            Number of sample trimmed from ending
     * @throws Exception exception
     */
    public void trim(int leftTrimNumberOfSample, int rightTrimNumberOfSample) throws Exception {

        long chunkSize = this.m_wave.getHeader().getChunkSize();
        long subChunk2Size = this.m_wave.getHeader().getSubChunk2Size();

        long totalTrimmed = leftTrimNumberOfSample + rightTrimNumberOfSample;

        if (totalTrimmed > subChunk2Size) {
            leftTrimNumberOfSample = (int) subChunk2Size;
        }

        // update wav info
        chunkSize -= totalTrimmed;
        subChunk2Size -= totalTrimmed;

        if (chunkSize >= 0 && subChunk2Size >= 0) {
            this.m_wave.getHeader().setChunkSize(chunkSize);
            this.m_wave.getHeader().setSubChunk2Size(subChunk2Size);

            byte[] trimmedData = new byte[(int) subChunk2Size];
            System.arraycopy(this.m_wave.getBytes(), (int) leftTrimNumberOfSample,
                    trimmedData, 0, (int) subChunk2Size);
            this.m_wave.setBytes(trimmedData);
        } else{ 
            throw new Exception("Error: negative trimming length.");
        }
    }
    
    /**
     * @return the WAV object
     */
    public Wave getWave() {
        return this.m_wave;
    }

}
