package de.egym.recruiting.codingtask;

import de.egym.recruiting.codingtask.jpa.domain.User;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class UserMatcher extends TypeSafeMatcher<User> {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthday;

    private UserMatcher(Long id, String firstName, String lastName, String email, Date birthday) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
    }

    public static UserMatcher copy(User user) {
        return new UserMatcher(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getBirthday());
    }

    public static UserMatcher empty() {
        return new UserMatcher(null, null, null, null, null);
    }

    public UserMatcher withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserMatcher withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserMatcher withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserMatcher withBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    @Override
    protected boolean matchesSafely(User item) {
        boolean match = true;

        if (id != null)
            match = item.getId().equals(id);

        if (firstName != null)
            match = item.getFirstName().equals(firstName);

        if (lastName != null)
            match &= item.getLastName().equals(lastName);

        if (email != null)
            match &= item.getEmail().equals(email);

        if (birthday != null)
            match &= item.getBirthday().getTime() == birthday.getTime();

        return match;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValueList("", ", ", "", firstName, lastName, email);
    }
}
