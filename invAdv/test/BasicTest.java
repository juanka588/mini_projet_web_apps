import org.junit.*;

import com.sun.xml.internal.ws.api.pipe.NextAction;

import java.util.*;
import play.test.*;
import models.*;
import models.InvestementAdvice.Type;

public class BasicTest extends UnitTest {

	@Test
	public void createAnUser() {
		new User("jk@gmail.com", "secret", "juan").save();
		User user = User.find("byEmail", "jk@gmail.com").first();
		assertNotNull(user);
		assertEquals(user.fullname, "juan");
	}

	@Test
	public void createACategory() {
		Category cat1 = new Category(null, "finances de marché").save();
		Category cat11 = new Category(cat1, "salle de marché").save();
		Category cat12 = new Category(cat1, "mutual funds").save();
		
		Category result = Category.find("byCategoryTitle", "mutual funds").first();
		assertEquals(cat1, result.parentCategory);
		Category result2 = Category.find("byCategoryTitle","finances de marché").first();
		assertNotNull(result2);
		assertEquals(2, result2.categoryChilds.size());
		
		Category cat2 = new Category(null, "finances économique").save();
		Category cat21 = new Category(cat2, "Immobilier").save();
		Category cat22 = new Category(cat2, "matière première").save();
		Category cat221 = new Category(cat22, "nouvelles technologies").save();
		
		new User("jk4@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk4@gmail.com").first();
		double capitalGain = 500, confidenceIndex = 0.02;
		Date currentDate = new Date();
		new InvestementAdvice(currentDate, "test investement", user, Type.LONG_TERM, capitalGain, confidenceIndex, cat221)
				.save();
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();
		
		assertEquals(advice.category.parentCategory.categoryTitle, "matière première");
		
	}

	@Test
	public void createAnAdvice() {
		new User("jk2@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk2@gmail.com").first();
		double capitalGain = 500, confidenceIndex = 0.02;
		Date currentDate = new Date();
		new InvestementAdvice(currentDate, "test investement", user, Type.LONG_TERM, capitalGain, confidenceIndex, null)
				.save();
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();

		assertNotNull(advice);
		assertEquals(advice.creationDate, currentDate);
		assertEquals(advice.title, "test investement");
		assertEquals(advice.type, Type.LONG_TERM);
		double ans1 = advice.capitalGains.get(user.id);
		assertTrue(500 == ans1);
		double ans2 = advice.confidenceIndexs.get(user.id);
		assertTrue(ans2 == 0.02);
	}
	@Test
	public void addGradeAdvice() {
		new User("jk2@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk2@gmail.com").first();
		double capitalGain = 500, confidenceIndex = 0.02;
		Date currentDate = new Date();
		new InvestementAdvice(currentDate, "test investement", user, Type.LONG_TERM, capitalGain, confidenceIndex, null)
				.save();
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();
		User user2 = new User("jk3@gmail.com", "secret", "juan3").save();
		Long id=user2.id;
		advice.addCapitalGain(id, 300);
		advice.addConfidenceIndex(id, 0.05);
		assertEquals(2, advice.capitalGains.size());
		assertEquals(2, advice.confidenceIndexs.size());
		
		assertEquals(400, advice.getCapitalGain(),0.001);
		assertEquals(0.035, advice.getConfidenceIndex(),0.001);
		
		advice.addCapitalGain(id, 300);
		advice.addConfidenceIndex(id, 0.05);

		assertEquals(2, advice.capitalGains.size());
		assertEquals(2, advice.confidenceIndexs.size());
		
		InvestementAdvice advice2 = InvestementAdvice.find("byAuthor", user).first();
		assertEquals(2, advice2.capitalGains.size());
		assertEquals(2, advice2.confidenceIndexs.size());
		
		
	}
	

	@Test
	public void createAComment() {
		new User("jk3@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk3@gmail.com").first();

		double capitalGain = 500, confidenceIndex = 0.02;

		Date currentDate = new Date();
		new InvestementAdvice(currentDate, "test investement", user, Type.LONG_TERM, capitalGain, confidenceIndex, null)
				.save();

		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();

		new Comment(user, "good advice", advice, currentDate).save();
		Comment comment1 = Comment.find("byAdvice", advice).first();
		assertNotNull(comment1);
		assertEquals(comment1.author, user);
		assertEquals(comment1.comment, "good advice");

		new Comment(user, "regular advice", advice, currentDate).save();
		List<Comment> comments = Comment.find("byAdvice", advice).fetch();
		assertNotNull(comments);
		assertEquals(2, comments.size());
		assertEquals(2, advice.comments.size());

	}

}
