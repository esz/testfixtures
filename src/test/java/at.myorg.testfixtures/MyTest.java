package at.myorg.testfixtures;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MyTest {

	private static MyTestFixture myTestFixture = new MyTestFixture();

	@BeforeAll
	static void beforeClass() {
		myTestFixture.insertTestdata();
	}

	@DisplayName("Classes from sourceSets.main should only be on the classpath once!")
	@Test
	void classesFromMainSourceSetArePresentMultipleTimes() throws IOException {
		List<URL> urlList = Collections.list(MyTest.class.getClassLoader().getResources("at/myorg/testfixtures/App.class"));
		urlList.forEach(System.out::println);
		Assertions.assertEquals(1, urlList.size());
	}

}
