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

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.time.calendar.Period;

import junit.framework.Assert;

import org.jadira.usertype.dateandtime.jsr310.testmodel.PeriodAsStringHolder;
import org.jadira.usertype.dateandtime.jsr310.testmodel.PeriodJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentPeriodAsString extends DatabaseCapable {

    private static final Period[] periods = new Period[] {
        Period.ofDays(2),
        Period.ofSeconds(30),
        Period.ofMonths(3),
        Period.ofSeconds(30),
        Period.of(4, 35, 40, 141, 0, 0, 0),
        Period.of(28, 10, 2, 2, 4, 35, 40000000),
        Period.of(28, 10, 0, 16, 4, 35, 40000000)
    };

    private static final org.joda.time.Period[] jodaPeriods = new org.joda.time.Period[] {
        org.joda.time.Period.days(2),
        org.joda.time.Period.seconds(30),
        org.joda.time.Period.months(3),
        org.joda.time.Period.seconds(30),
        new org.joda.time.Period(4, 35, 0, 40, 141, 0, 0, 0),
        new org.joda.time.Period(28, 10, 0, 2, 2, 4, 35, 40),
        new org.joda.time.Period(28, 10, 0, 0, 16, 4, 35, 40)
    };

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
    public void testPersist() throws SQLException, IOException {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < periods.length; i++) {
            PeriodAsStringHolder item = new PeriodAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setPeriod(periods[i]);
            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < periods.length; i++) {

            PeriodAsStringHolder item = manager.find(PeriodAsStringHolder.class, Long.valueOf(i));

            Assert.assertNotNull(item);
            Assert.assertEquals(i, item.getId());
            Assert.assertEquals("test_" + i, item.getName());
            Assert.assertEquals(periods[i], item.getPeriod());
        }

        verifyDatabaseTable(manager, PeriodAsStringHolder.class.getAnnotation(Table.class).name());

        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < periods.length; i++) {
            manager.remove(manager.find(PeriodAsStringHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        for (int i = 0; i < periods.length; i++) {

            PeriodJoda item = new PeriodJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setPeriod(jodaPeriods[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < periods.length; i++) {

            PeriodAsStringHolder item = manager.find(PeriodAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());

            Period expected = periods[i];
            long nanos = expected.getNanos();
            nanos = (nanos / 1000000L) * 1000000L;
            expected = expected.withNanos(nanos);

            assertEquals(expected, item.getPeriod());
        }
        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testNanosWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < periods.length; i++) {
            manager.remove(manager.find(PeriodAsStringHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        PeriodAsStringHolder item = new PeriodAsStringHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setPeriod(Period.ofNanos(111444444));

        manager.persist(item);
        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        PeriodJoda jodaItem = manager.find(PeriodJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.Period(0, 0, 0, 111), jodaItem.getPeriod());

        manager.close();

        manager = factory.createEntityManager();

        item = manager.find(PeriodAsStringHolder.class, Long.valueOf(1));

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(Period.ofNanos(111444444), item.getPeriod());

        manager.close();
    }
}
