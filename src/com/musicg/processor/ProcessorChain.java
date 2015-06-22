package com.musicg.processor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jacquet Wong
 *
 */
public class ProcessorChain {

    private double[][] intensities;
    List<IntensityProcessor> processorList = new LinkedList<IntensityProcessor>();

    /**
     * @param intensities
     */
    public ProcessorChain(double[][] intensities) {
        this.intensities = intensities;
        RobustIntensityProcessor robustProcessor = new RobustIntensityProcessor(
                intensities, 1);
        processorList.add(robustProcessor);
        process();
    }

    private void process() {
        Iterator<IntensityProcessor> iterator = processorList.iterator();
        while (iterator.hasNext()) {
            IntensityProcessor processor = iterator.next();
            processor.execute();
            intensities = processor.getIntensities();
        }
    }

    /**
     * @return
     */
    public double[][] getIntensities() {
        return intensities;
    }
}