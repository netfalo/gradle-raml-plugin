package com.netfalo.gradle.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.TestCase.assertTrue
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.MatcherAssert.assertThat

class GenerateRamlFunctionalTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File buildFile

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle")
        testProjectDir.newFolder("src", "main", "java")
    }

    @Test
    void canRunGenerateRaml() throws IOException {
        String buildFileContent = "\n" +
                "        plugins {\n" +
                "            id 'java'\n" +
                "            id 'com.netfalo.raml'\n" +
                "        }\n"

        writeFile(buildFile, buildFileContent)

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("generateRaml")
                .build()

        assertThat(result.task(":generateRaml").getOutcome(), is(SUCCESS))
        assertTrue(new File(testProjectDir.getRoot().toString() + "/build/api.raml").exists())
    }

    private static void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null
        try {
            output = new BufferedWriter(new FileWriter(destination))
            output.write(content)
        } finally {
            if (output != null) {
                output.close()
            }
        }
    }
}
