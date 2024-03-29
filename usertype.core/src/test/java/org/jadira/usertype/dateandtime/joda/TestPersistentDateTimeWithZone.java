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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.hamcrest.core.IsEqual;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateTimeWithZoneHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPersistentDateTimeWithZone extends DatabaseCapable {

    private static final DateTime[] dateTimes = new DateTime[] { 
        new DateTime(2004, 2, 25, 12, 11, 10, 0, DateTimeZone.forOffsetHours(4)).withZone(DateTimeZone.UTC), 
        new DateTime(1980, 3, 11, 13, 12, 11, 500, DateTimeZone.UTC), 
        null };

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

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeWithZoneHolder item = new JodaDateTimeWithZoneHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dateTimes[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        Criteria criteria = ((Session)(manager.getDelegate())).createCriteria(JodaDateTimeWithZoneHolder.class); 
        criteria.setCacheable(true); 
        criteria.add(Restrictions.le("dateTime.datetime", new DateTime()));     
        @SuppressWarnings({ "unused", "unchecked" })
		List<JodaDateTimeWithZoneHolder> result = (List<JodaDateTimeWithZoneHolder>) criteria.list(); 

        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeWithZoneHolder item = manager.find(JodaDateTimeWithZoneHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateTimes[i] == null) {
            	assertNull(item.getDateTime());
            } else {
            	assertEquals(dateTimes[i].toString(), item.getDateTime().toString());
            }
        }
        
        verifyDatabaseTable(manager, JodaDateTimeWithZoneHolder.class.getAnnotation(Table.class).name());
        
		// Ensure use of criteria does not throw exception
        criteria = ((Session)(manager.getDelegate())).createCriteria(JodaDateTimeWithZoneHolder.class); 
        criteria.setCacheable(true); 
        criteria.add(Restrictions.le("dateTime.datetime", new LocalDateTime()));     
		result = (List<JodaDateTimeWithZoneHolder>) criteria.list(); 

        manager.close();
    }
    
	@Test // Added to investigate http://sourceforge.net/mailarchive/message.php?msg_id=29056453
	public void testDST() {

		DateTimeZone tz = DateTimeZone.forID("Europe/Berlin");
		assertFalse(tz.isFixed());
		DateTime dt = new DateTime(2010, 10, 31, 1, 0, 0, tz);

		EntityManager manager = factory.createEntityManager();

		for (int i = 0; i < 5; i++) {

			System.out.println("Saving: " + dt);

			manager.getTransaction().begin();

			JodaDateTimeWithZoneHolder item = new JodaDateTimeWithZoneHolder();
			item.setId(i + 10);
			item.setName("test_" + i);
			item.setDateTime(dt);

			manager.persist(item);
			manager.flush();
			manager.getTransaction().commit();

			JodaDateTimeWithZoneHolder readItem = manager.find(
					JodaDateTimeWithZoneHolder.class, Long.valueOf(i) + 10);

			System.out.println("ReadItem: " + readItem.getDateTime());

			assertThat("For record {" + i + "}", dt,
					IsEqual.equalTo(readItem.getDateTime()));

			dt = dt.plusHours(1);
		}
		manager.close();
	}

}
