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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;

import org.jadira.usertype.dateandtime.jsr310.testmodel.ZonedDateTimeAsStringStringOffsetAndStringZoneHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestPersistentZonedDateTimeAsStringStringOffsetAndStringZone extends DatabaseCapable {

    private static final ZonedDateTime[] zonedDateTimes = new ZonedDateTime[] {
        ZonedDateTime.of(OffsetDateTime.of(2004, 2, 25, 17, 3, 45, 760, ZoneOffset.ofHours(2)), TimeZone.of(ZoneOffset.ofHours(2))),
        ZonedDateTime.of(OffsetDateTime.of(1980, 3, 11, 2, 3, 45, 0, ZoneOffset.ofHours(2)), TimeZone.of(ZoneOffset.ofHours(2))) };

    private static EntityManagerFactory factory;

    @BeforeClass
    public static void setup() {
        factory = Persistence.createEntityManagerFactory("test1");
    }

    @AfterClass
    public static void tearDown() {
        factory.close();
    }

    @Test
    public void testPersist() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < zonedDateTimes.length; i++) {

            ZonedDateTimeAsStringStringOffsetAndStringZoneHolder item = new ZonedDateTimeAsStringStringOffsetAndStringZoneHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setZonedDateTime(zonedDateTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < zonedDateTimes.length; i++) {

            ZonedDateTimeAsStringStringOffsetAndStringZoneHolder item = manager.find(ZonedDateTimeAsStringStringOffsetAndStringZoneHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(zonedDateTimes[i], item.getZonedDateTime());
        }

        verifyDatabaseTable(manager, ZonedDateTimeAsStringStringOffsetAndStringZoneHolder.class.getAnnotation(Table.class).name());

        manager.close();
    }
}
