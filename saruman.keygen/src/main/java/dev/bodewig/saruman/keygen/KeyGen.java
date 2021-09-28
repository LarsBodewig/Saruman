package dev.bodewig.saruman.keygen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mojo(name = "generate-keys", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class KeyGen extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	public void execute() throws MojoExecutionException {
		if (!this.outputDirectory.exists()) {
			this.outputDirectory.mkdirs();
		}

		File filePublic = new File(this.outputDirectory, "saruman_public.key");
		File filePrivate = new File(this.outputDirectory, "saruman_private.key");

		try (FileWriter writerPublic = new FileWriter(filePublic);
				FileWriter writerPrivate = new FileWriter(filePrivate)) {
			writerPublic.write("public");
			writerPrivate.write("private");
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating key files", e);
		}
	}
}
