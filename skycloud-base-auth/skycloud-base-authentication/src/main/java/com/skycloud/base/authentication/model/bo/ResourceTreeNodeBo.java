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
package com.skycloud.base.authentication.model.bo;

import com.skycloud.base.authentication.model.po.Resource;
import com.sky.framework.common.tree.ITreeNode;
import org.apache.commons.lang.StringUtils;

/**
 * @author
 */
public class ResourceTreeNodeBo implements ITreeNode {

    private Resource resource;

    public ResourceTreeNodeBo(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getNodeId() {
        return resource.getId() + "";
    }

    @Override
    public String getNodeName() {
        return resource.getName();
    }

    @Override
    public String getCode() {
        return resource.getCode();
    }

    @Override
    public String getNodeParentId() {
        return resource.getParentId() + "";
    }

    @Override
    public Integer getOrderNum() {
        return resource.getSort();
    }

    @Override
    public String getMethod() {
        return resource.getMethod();
    }

    @Override
    public String getUrl() {
        return StringUtils.isEmpty(resource.getUrl())?resource.getCode():resource.getUrl();
    }

    @Override
    public String getIcon() {
        return resource.getIcon();
    }
}
