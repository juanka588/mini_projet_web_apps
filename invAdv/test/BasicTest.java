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
	public void createACategory() {
		// creation du noeud père
		Category cat1 = new Category(null, "finances de marché").save();
		Category result = Category.find("byCategoryTitle", "finances de marché").first();
		assertNotNull(result);
		// création du fils
		Category cat11 = new Category(result, "salle de marché").save();
		Category result2 = Category.find("byCategoryTitle", "salle de marché").first();
		assertNotNull(result2);

		assertEquals(cat11, result.categoryChilds.get(0));

		// creation du user et advice
		new User("jk4@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk4@gmail.com").first();
		double capitalGain = 500, confidenceIndex = 0.02;
		Date currentDate = new Date();
		Type type = new Type("LONG TERM").save();
		new InvestementAdvice(currentDate, "test investement", "Empty content", user, type, capitalGain,
				confidenceIndex, cat11).save();
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();

		assertNotNull(advice.category);
		assertEquals(advice.category.parentCategory.categoryTitle, "finances de marché");
	}

	@Test
	public void createAnAdvice() {
		
		new User("jk2@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk2@gmail.com").first();
		double capitalGain = 500, confidenceIndex = 0.02;
		Date currentDate = new Date();
		Type type = new Type("LONG TERM").save();
		Category cat12 = new Category(null, "mutual funds").save();
		new InvestementAdvice(currentDate, "test investement", "Empty content", user, type, capitalGain,
				confidenceIndex, cat12).save();
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user).first();

		assertNotNull(advice);
		assertEquals(advice.creationDate, currentDate);
		assertEquals(advice.title, "test investement");
		assertEquals(advice.type.type, "LONG TERM");
		double ans1 = advice.capitalGain;
		assertTrue(500 == ans1);
		double ans2 = advice.confidenceIndex;
		assertTrue(ans2 == 0.02);
	}

	@Test
	public void createAComment() {
		
		new User("jk3@gmail.com", "secret", "juan2").save();
		User user = User.find("byEmail", "jk3@gmail.com").first();

		double capitalGain = 500, confidenceIndex = 0.02;
		Category cat12 = new Category(null, "mutual funds").save();
		Date currentDate = new Date();
		Type type = new Type("MOYEN TERM").save();
		new InvestementAdvice(currentDate, "test investement", "Empty content", user, type, capitalGain,
				confidenceIndex, cat12).save();

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

	@Test
	public void addGradeAdvice() {

		// Création du user et de l'advice
		User user = new User("jk2@gmail.com", "secret", "juan2").save();
		User user2 = new User("jk3@gmail.com", "secret", "juan3").save();
		User user3 = new User("jk3@gmail.com", "secret", "juan3").save();
		Category cat12 = new Category(null, "mutual funds").save();
		double capitalGain = 500, confidenceIndex = 0.02;
		Date currentDate = new Date();
		Type type = new Type("MOYEN TERM").save();
		User user1 = User.find("byEmail", "jk2@gmail.com").first();
		new InvestementAdvice(currentDate, "test investement", "Empty content", user1, type, capitalGain,
				confidenceIndex, cat12).save();

		// récupération des données de la base de donnée
		InvestementAdvice advice = InvestementAdvice.find("byAuthor", user1).first();

		long id1 = user1.id;
		long id2 = user2.id;
		long id3 = user3.id;

		// ajout de la data
		advice.addRate(id2, 300, 0.02);
		assertEquals(2, advice.dataRate.size());

		assertEquals(400, advice.getcapital(), 0.001);
		assertEquals(0.02, advice.getconfidence(), 0.001);

		// ajout d'un capital gain qui dépasse 100000 et
		// d'un confidence index qui depasse 10
		advice.addRate(id3, 30000000, 30);
		assertEquals(2, advice.dataRate.size());
		assertEquals(400, advice.getcapital(), 0.001);
		assertEquals(0.02, advice.getconfidence(), 0.001);


	}

}
