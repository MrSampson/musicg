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

package com.musicg.math.rank;

import java.util.Arrays;

/**
 * @author Jacquet Wong
 *
 */
public class ArrayRankDouble {

    /**
     * Get the index position of maximum value the given array
     * 
     * @param array
     * @return index of the max value in array
     */
    public int getMaxValueIndex(double[] array) {

        int index = 0;
        double max = Integer.MIN_VALUE;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                index = i;
            }
        }

        return index;
    }

    /**
     * Get the index position of minimum value in the given array
     * 
     * @param array
     * @return index of the min value in array
     */
    public int getMinValueIndex(double[] array) {

        int index = 0;
        double min = Integer.MAX_VALUE;

        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                index = i;
            }
        }

        return index;
    }

    /**
     * @param array array to get the value
     * @param n an index into the array
     * @param ascending
     *            is ascending order or not
     * @return the n-th value in the array after sorted
     */
    public double getNthOrderedValue(double[] array, int n, boolean ascending) {

        if (n > array.length) {
            n = array.length;
        }

        int targetindex;
        if (ascending) {
            targetindex = n;
        } else {
            targetindex = array.length - n;
        }

        Arrays.sort(array);

        return array[n];
    }
}