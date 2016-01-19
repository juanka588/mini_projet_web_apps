import org.junit.*;

import com.sun.xml.internal.ws.api.pipe.NextAction;

import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

	@Test
	public void createAnUser() {
		new User("jk@gmail.com", "secret", "juan").save();
		User user = User.find("byEmail", "jk@gmail.com").first();
		assertNotNull(user);
		assertEquals(user.fullname, "juan");
	}

	@Test
	public void createAnAdvice() {
		new User("jk2@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk2@gmail.com").first();

		assertNotNull(user);
		double capitalGain = 500, confidenceIndex = 0.02;

		Date currentDate = new Date();
		new InvestementAdvice(currentDate, "test investement", user, capitalGain, confidenceIndex, null, null).save();
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();

		assertNotNull(advice);
		assertEquals(advice.creationDate, currentDate);
		assertEquals(advice.title, "test investement");
		double ans1 = advice.capitalGains.get(user.id);
		assertTrue(500 == ans1);
		double ans2 = advice.confidenceIndexs.get(user.id);
		assertTrue(ans2 == 0.02);
	}
}
