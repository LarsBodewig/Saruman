package dev.bodewig.saruman.keygen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.security.auth.DestroyFailedException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-keys", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class KeyGen extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.resources[0].directory}", property = "outputDirectoryPublic", required = true)
	private File outputDirectoryPublic;
	@Parameter(defaultValue = "${project.build.resources[0].directory}", property = "outputDirectoryPrivate", required = true)
	private File outputDirectoryPrivate;
	@Parameter(defaultValue = "saruman_public.key", property = "outputFileNamePublic", required = true)
	private String outputFileNamePublic;
	@Parameter(defaultValue = "saruman_private.key", property = "outputFileNamePrivate", required = true)
	private String outputFileNamePrivate;

	public void execute() throws MojoExecutionException {
		try {
			File filePublic = initFile(this.outputDirectoryPublic, this.outputFileNamePublic);
			File filePrivate = initFile(this.outputDirectoryPrivate, this.outputFileNamePrivate);

			KeyPair keys = getKeyPair();
			PublicKey keyPublic = keys.getPublic();
			PrivateKey keyPrivate = keys.getPrivate();

			try {
				writeFile(filePublic, keyPublic.getEncoded());
				getLog().info("Public key file " + filePublic.getAbsolutePath() + " created");
				writeFile(filePrivate, keyPrivate.getEncoded());
				getLog().info("Private key file " + filePrivate.getAbsolutePath() + " created");
			} finally {
				try {
					keyPrivate.destroy();
				} catch (DestroyFailedException dfe) {
					getLog().warn("Failed to destroy private key, the generated key file should not be used");
					getLog().warn(dfe);
				}
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Error generating key files", e);
		}
	}

	private static File initFile(File dir, String name) throws IOException {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, name);
		if (file.exists()) {
			if (!file.delete()) {
				throw new IOException("Error deleting file " + file.getAbsolutePath());
			}
		}
		if (!file.createNewFile()) {
			throw new IOException("Error creating file " + file.getAbsolutePath());
		}
		return file;
	}

	private static KeyPair getKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair keys = keyGen.generateKeyPair();
		return keys;
	}

	private static void writeFile(File file, byte[] content) throws IOException {
		try (FileOutputStream output = new FileOutputStream(file)) {
			output.write(content);
		}
	}
}
