/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.usertype.dateandtime.jsr310;

import javax.time.calendar.LocalTime;

import org.jadira.usertype.dateandtime.jsr310.columnmapper.IntegerColumnLocalTimeMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;


/**
 * Persist {@link LocalTime} via Hibernate using milliseconds of the day. This type is
 * mostly compatible with {@link org.joda.time.contrib.hibernate.PersistentLocalTimeExact} however
 * you should note that JodaTime's {@link org.joda.time.LocalTime} has only millisecond precision,
 * whilst JSR 310 offers nanosecond precision. When interpreting nanosecond values, Joda time will
 * round down to the nearest millisecond.
 * @see PersistentLocalTimeAsNanosLong
 */
public class PersistentLocalTimeAsMillisInteger extends AbstractSingleColumnUserType<LocalTime, Integer, IntegerColumnLocalTimeMapper> {

    private static final long serialVersionUID = 5231874917398959855L;
}
