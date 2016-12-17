package de.egym.recruiting.codingtask.jpa.dao;

import com.google.inject.Inject;
import de.egym.recruiting.codingtask.AbstractIntegrationTest;
import de.egym.recruiting.codingtask.TestData;
import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.UserMatcher;
import de.egym.recruiting.codingtask.jpa.domain.User;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.List;

import static de.egym.recruiting.codingtask.TestsHelper.checkException;
import static org.junit.Assert.*;

public class UserDaoIntegrationTest extends AbstractIntegrationTest {

    @Inject
    private UserDao userDao;

    @Test
    public void testFindByEmail() throws Exception {
        final User user = userDao.findByEmail("heinz@egym.de");

        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        assertThat(user, UserMatcher.copy(TestData.USER_1));
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
        assertThat(bond, UserMatcher.copy(TestData.USER_2));
    }

    @Test
    public void testFindByLastNameIgnoreCase() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("bond");
        assertEquals(1, users.size());

        User bond = users.get(0);
        assertThat(bond, UserMatcher.copy(TestData.USER_2));
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
        assertThat(mueller, UserMatcher.copy(TestData.USER_1));
    }

    @Test
    public void testFindByLastNamePrefixSeveralMatches() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("bon");
        assertEquals(2, users.size());

        assertThat(users, Matchers.hasItem(UserMatcher.copy(TestData.USER_2)));
        assertThat(users, Matchers.hasItem(UserMatcher.copy(TestData.USER_3)));
    }

    @Test
    public void testFindByLastNameSuffixNotMatch() throws Exception {
        final List<User> users = userDao.findByLastNamePrefix("eller");
        assertEquals(0, users.size());
    }

    @Test
    public void testCreateUserNullFields() throws Exception {
        User user = new User();
        try {
            checkException(() -> userDao.create(user), PersistenceException.class);
            user.setFirstName("");
            checkException(() -> userDao.create(user), PersistenceException.class);
            user.setLastName("");
            checkException(() -> userDao.create(user), PersistenceException.class);
            user.setBirthday(Timing.getMillis());
            checkException(() -> userDao.create(user), PersistenceException.class);
            user.setEmail("");//todo I think it's better to add validation constraint to hibernate also
            assertNotNull(userDao.create(user));
            assertNotNull(userDao.findById(user.getId()));
            assertEquals(4, userDao.findAll().size());
        } finally {
            userDao.delete(user);
        }
    }

    @Test
    public void testCreateUserEmailUnique() throws Exception {
        User user = new User();
        try {
            user.setFirstName("Arnold");
            user.setLastName("Schwarzenegger");
            user.setBirthday(0);//
            user.setEmail(TestData.USER_1.getEmail());
            checkException(() -> userDao.create(user), PersistenceException.class);
            assertEquals(3, userDao.findAll().size());
        } finally {
            userDao.delete(user);
        }
    }

}
