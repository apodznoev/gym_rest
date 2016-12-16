package de.egym.recruiting.codingtask.jpa.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.*;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;

import de.egym.recruiting.codingtask.AbstractIntegrationTest;
import de.egym.recruiting.codingtask.jpa.domain.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UserDaoIntegrationTest extends AbstractIntegrationTest {

    @Inject
    private UserDao userDao;

    @Test
    public void testFindByEmail() throws Exception {
        final User user = userDao.findByEmail("heinz@egym.de");

        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals("heinz@egym.de", user.getEmail());
        Assert.assertEquals("Heinz", user.getFirstName());
        Assert.assertEquals("Mueller", user.getLastName());
        final Date expectedBirthday = DateUtils.parseDate("1983-02-01", "yyyy-MM-dd");
        Assert.assertEquals(expectedBirthday, user.getBirthday());
    }

    @Test
    public void testFindByEmailNotFound() {
        final User user = userDao.findByEmail("notfound@egym.de");
        Assert.assertNull(user);
    }

    @Test
    public void testFindByEmailWithNullInput() {
        final User user = userDao.findByEmail(null);
        Assert.assertNull(user);
    }

    @Test
    public void testFindByLastNameNull() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix(null);
        assertEquals(3, users.size());
    }

    @Test
    public void testFindByLastNameEmpty() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("");
        assertEquals(3, users.size());
    }

    @Test
    public void testFindByLastNameExactMatch() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("Bond");
        assertEquals(1, users.size());

        User bond = users.get(0);
        assertEquals("James", bond.getFirstName());
        assertEquals("Bond", bond.getLastName());
        assertEquals("007@mi6.co.uk", bond.getEmail());
    }

    @Test
    public void testFindByLastNameIgnoreCase() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("bond");
        assertEquals(1, users.size());

        User bond = users.get(0);
        assertEquals("James", bond.getFirstName());
        assertEquals("Bond", bond.getLastName());
        assertEquals("007@mi6.co.uk", bond.getEmail());
    }

    @Test
    public void testFindByLastNameNoMatch() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("Cooper");
        assertEquals(0, users.size());
    }

    @Test
    public void testFindByLastNamePrefixMatch() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("Muell");
        assertEquals(1, users.size());

        User mueller = users.get(0);
        assertEquals("Heinz", mueller.getFirstName());
        assertEquals("Mueller", mueller.getLastName());
        assertEquals("heinz@egym.de", mueller.getEmail());
    }

    @Test
    public void testFindByLastNamePrefixSeveralMatches() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("bon");
        assertEquals(2, users.size());

        assertThat(users, Matchers.hasItem(new UserMatcher("James", "Bond", "007@mi6.co.uk")));
        assertThat(users, Matchers.hasItem(new UserMatcher("John", "Bongiovi", "bon@bonjovi.com")));
    }

    @Test
    public void testFindByLastNameSuffixNotMatch() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("eller");
        assertEquals(0, users.size());
    }

    private static class UserMatcher extends TypeSafeMatcher<User> {
        private final String firstName;
        private final String lastName;
        private final String email;

        private UserMatcher(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        @Override
        protected boolean matchesSafely(User item) {
            return item.getFirstName().equals(firstName)
                    && item.getLastName().equals(lastName)
                    && item.getEmail().equals(email);
        }

        @Override
        public void describeTo(Description description) {
            description.appendValueList("", ", ", "", firstName, lastName, email);
        }
    }
}
