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

package com.musicg.experiment.math.cluster;
/**
 * @author Jacquet Wong
 *
 */
public class Segment {
	
	private int startPosition;
	private int size;
	private double mean;

	/**
	 * @return the start position
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param startPosition start position
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean mean
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}
}