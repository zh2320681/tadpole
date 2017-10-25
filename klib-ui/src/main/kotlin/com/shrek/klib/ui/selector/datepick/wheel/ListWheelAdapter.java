/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.shrek.klib.ui.selector.datepick.wheel;

import java.util.List;
import java.util.Map;

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
public class ListWheelAdapter<T> implements WheelAdapter
{
    
    /** The default items length */
    public static final int DEFAULT_LENGTH = -1;
    
    // items
    private List<Map<String, Object>> items;
    
    // length
    private int length;
    
    /**
     * Constructor
     * @param data the items
     * @param length the max items length
     */
    public ListWheelAdapter(List<Map<String, Object>> data, int length)
    {
        this.items = data;
        this.length = length;
    }
    
    /**
     * Contructor
     * @param data the items
     */
    public ListWheelAdapter(List<Map<String, Object>> data)
    {
        this(data, DEFAULT_LENGTH);
    }
    
    @Override
    public int getMaximumLength()
    {
        return length;
    }
    
    @Override
    public int getItemsCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public String getItem(int index)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
