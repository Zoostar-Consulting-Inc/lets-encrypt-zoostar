package com.zoostarinc.lez.web.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.common.StringWrapper;

@Slf4j
@RestController
public class AcmeRestController implements InitializingBean {

	@Value("${user.home}")
	private String userHome;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug("User Home directory: {}", userHome);
	}

	@GetMapping(path = "/csr/generate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StringWrapper> generateCSR(@RequestParam String domainCommonName) throws IOException,
			NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
		
		if(!StringUtils.hasText(domainCommonName)) {
			throw new IllegalArgumentException("Domain Common Name is a Required Param!");
		}

		// Get the KeyPair Generator
		var keyPairGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
		keyPairGenerator.initialize(4096);
		var keyPair = keyPairGenerator.generateKeyPair();

		// Create a signer using the private key and a signature algorithm (e.g. SHA256withRSA)
		var csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
		var signer = csBuilder.build(keyPair.getPrivate());

		// Define the subject DN (e.g. "CN=Requested Test Certificate, O=Test Inc, C=US")
		var subject = new X500Principal("CN=" + domainCommonName);

		// Build the CSR request
		var p10Builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());

		// Generate the CSR object
		var csr = p10Builder.build(signer);
		var file = new File(userHome, domainCommonName + ".csr");
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(new BufferedWriter(new FileWriter(file)))) {
			pemWriter.writeObject(csr);
			log.info("Cert Signing Request completed successfully at: {}", file.getCanonicalPath());
		}

		return ResponseEntity.ok(new StringWrapper(file.getCanonicalPath()));
	}

}
