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
package org.jadira.usertype.dateandtime.joda;

import java.sql.Timestamp;

import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnTimeOfDayMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;
import org.joda.time.TimeOfDay;

/**
 * Persist {@link TimeOfDay} via Hibernate using a JDBC Timestamp datatype with a reference date.  - note that sub-second values will not
 * be retained.
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and {@link PersistentLocalTimeAsTimestamp}
 */
public class PersistentTimeOfDayAsTimestamp extends AbstractSingleColumnUserType<TimeOfDay, Timestamp, TimestampColumnTimeOfDayMapper> {

    private static final long serialVersionUID = -2642237430338499187L;
}