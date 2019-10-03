/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.skycloud.base.geteway.chain;

import com.sky.framework.common.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 过滤器管理者
 *
 * @author
 */
@Slf4j
public class FilterChainManager extends AbstractFilterChainManager {

    private FilterChainManager() {
    }

    public ConcurrentSkipListSet<FilterChain> set = new ConcurrentSkipListSet((Comparator<FilterChain>) (o1, o2) -> o1.getOrder() - o2.getOrder());


    @Override
    public void doFilter(Context context) {
        try {
            for (Iterator<FilterChain> iterator = set.iterator(); iterator.hasNext(); ) {
                iterator.next().filter(context);
            }
        } catch (Exception e) {
            LogUtils.error(log, "filter chain exception:{}", e);
            throw e;
        }
    }

    @Override
    public void add(FilterChain filter) {
        set.add(filter);
    }

    @Override
    public void remove(FilterChain filter) {
        set.remove(filter);
    }


    public static FilterChainManager getInstance() {
        return FilterChainManagerSingleton.INSTANCE.getInstance();
    }

    private enum FilterChainManagerSingleton {
        INSTANCE;

        private final FilterChainManager instance;

        FilterChainManagerSingleton() {
            instance = new FilterChainManager();
        }

        private FilterChainManager getInstance() {
            return instance;
        }

    }
}
