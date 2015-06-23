package com.musicg.math.rank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Jacquet Wong
 *
 */
public class MapRankInteger {

    private Map<Integer, Integer> map;
    private boolean acsending = true;

    /**
     * @param map
     * @param acsending
     */
    public MapRankInteger(Map<Integer, Integer> map, boolean acsending) {
	this.map = map;
	this.acsending = acsending;
    }

    /**
     * @param numKeys
     * @param sharpLimit
     * @return list of ordered keys
     */
    public List<Integer> getOrderedKeyList(int numKeys, boolean sharpLimit) {
	// if sharp limited, will return sharp numKeys, otherwise will return
	// until the values not equals the exact key's value

	Set<Entry<Integer, Integer>> mapEntrySet = map.entrySet();
	List<Integer> keyList = new LinkedList<>();

	// if the numKeys is larger than map size, limit it
	if (numKeys > map.size()) {
	    numKeys = map.size();
	}
	// end if the numKeys is larger than map size, limit it

	if (map.size() > 0) {
	    int[] array = new int[map.size()];
	    int count = 0;

	    // get the pass values
	    
	    for (Entry<Integer, Integer> entry : mapEntrySet) {		
		array[count++] = entry.getValue();
	    }
	    // end get the pass values

	    int targetindex;
	    if (acsending) {
		targetindex = numKeys;
	    } else {
		targetindex = array.length - numKeys;
	    }

	    // this value is the value of the numKey-th element
	    
	    int passValue = getOrderedValue(array, targetindex);
	    
	    // get the passed keys and values
	    Map<Integer, Integer> passedMap = new HashMap<>();
	    List<Integer> valueList = new LinkedList<>();
	   
	    for (Entry<Integer, Integer> entry :mapEntrySet) {
		 
		int value = entry.getValue();
		if ((acsending && value <= passValue)
			|| (!acsending && value >= passValue)) {
		    passedMap.put(entry.getKey(), value);
		    valueList.add(value);
		}
	    }
	    // end get the passed keys and values

	    // sort the value list
	    Integer[] listArr = new Integer[valueList.size()];
	    valueList.toArray(listArr);
	    Arrays.sort(listArr);

	    // get the list of keys
	    int resultCount = 0;
	    int index;
	    if (acsending) {
		index = 0;
	    } else {
		index = listArr.length - 1;
	    }

	    if (!sharpLimit) {
		numKeys = listArr.length;
	    }

	    while (true) {
		int targetValue = listArr[index];
		Iterator<Entry<Integer, Integer>> passedMapIterator = passedMap
			.entrySet().iterator();
		while (passedMapIterator.hasNext()) {
		    Entry<Integer, Integer> entry = passedMapIterator.next();
		    if (entry.getValue() == targetValue) {
			keyList.add(entry.getKey());
			passedMapIterator.remove();
			resultCount++;
			break;
		    }
		}

		if (acsending) {
		    index++;
		} else {
		    index--;
		}

		if (resultCount >= numKeys) {
		    break;
		}
	    }
	    // end get the list of keys
	}

	return keyList;
    }

    private int getOrderedValue(int[] array, int index) {
	locate(array, 0, array.length - 1, index);
	return array[index];
    }

    // sort the partitions by quick sort, and locate the target index
    private void locate(int[] array, int left, int right, int index) {

	int mid = (left + right) / 2;
	// System.out.println(left+" to "+right+" ("+mid+")");

	if (right == left) {
	    // System.out.println("* "+array[targetIndex]);
	    // result=array[targetIndex];
	    return;
	}

	if (left < right) {
	    int s = array[mid];
	    int i = left - 1;
	    int j = right + 1;

	    while (true) {
		while (array[++i] < s)
		    ;
		while (array[--j] > s)
		    ;
		if (i >= j)
		    break;
		swap(array, i, j);
	    }

	    // System.out.println("2 parts: "+left+"-"+(i-1)+" and "+(j+1)+"-"+right);

	    if (i > index) {
		// the target index in the left partition
		// System.out.println("left partition");
		locate(array, left, i - 1, index);
	    } else {
		// the target index in the right partition
		// System.out.println("right partition");
		locate(array, j + 1, right, index);
	    }
	}
    }

    private static void swap(int[] array, int i, int j) {
	int t = array[i];
	array[i] = array[j];
	array[j] = t;
    }
}