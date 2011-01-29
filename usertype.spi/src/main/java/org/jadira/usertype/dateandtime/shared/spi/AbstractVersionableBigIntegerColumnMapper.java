/*
 *  Copyright 2010 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.dateandtime.shared.spi;

import java.math.BigInteger;

import org.jadira.usertype.dateandtime.shared.reflectionutils.TypeHelper;

public abstract class AbstractVersionableBigIntegerColumnMapper<T> extends AbstractBigIntegerColumnMapper<T> implements VersionableColumnMapper<T, BigInteger> {

    private static final long serialVersionUID = -1406891387234804017L;
    
    public BigInteger generateCurrentValue() {
        try {
            @SuppressWarnings("unchecked") T current = (T) TypeHelper.getTypeArguments(AbstractVersionableBigIntegerColumnMapper.class, this.getClass()).get(0).newInstance();
            return toNonNullValue(current);
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Cannot construct versionable type value", ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Cannot access versionable type value", ex);
        }
    }
}
