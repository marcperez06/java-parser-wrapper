package io.github.marcperez06.java_parser.test;

import org.junit.Test;

import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerRequestInfo;
import io.github.marcperez06.java_parser.scripts.patterns.BuilderPatternGenerator;

public class BuilderGeneratorTest {
	
	@Test
	public void test() {
		BuilderPatternGenerator.generateBuilder(SwaggerRequestInfo.class);
	}

}