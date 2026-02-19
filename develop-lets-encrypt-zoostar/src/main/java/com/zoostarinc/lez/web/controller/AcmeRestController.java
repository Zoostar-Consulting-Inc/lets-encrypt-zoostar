package com.zoostarinc.lez.web.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.common.Meta;
import net.zoostar.common.StringWrapper;
import net.zoostar.common.Utils;

@Slf4j
@RestController
public class AcmeRestController implements InitializingBean {

	@Value("${user.home}")
	private String userHome;

	private RestClient client = RestClient.create();

	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug("User Home directory: {}", userHome);
	}

	@GetMapping(path = "/csr/generate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StringWrapper> generateCSR(@AuthenticationPrincipal OidcUser user,
			@RequestParam String domainCommonName) {

		Utils.assertNotEmpty(domainCommonName, "Domain Common Name is a Required Param!");

		try {
			var keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
			keyPairGenerator.initialize(4096);
			var keyPair = keyPairGenerator.generateKeyPair();

			// Create a signer using the private key and a signature algorithm (e.g.
			// SHA256withRSA)
			JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
			ContentSigner signer = csBuilder.build(keyPair.getPrivate());

			// Define the subject DN (e.g., "CN=Requested Test Certificate, O=Test Inc,
			// C=US")
			X500Principal subject = new X500Principal("CN=" + domainCommonName);

			// Build the CSR request
			JcaPKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(subject,
					keyPair.getPublic());

			// Generate the CSR object
			PKCS10CertificationRequest csr = p10Builder.build(signer);
			StringWriter stringWriter = new StringWriter();
			try (JcaPEMWriter pemWriter = new JcaPEMWriter(
					new BufferedWriter(new FileWriter(domainCommonName + ".pem")))) {
				pemWriter.writeObject(csr);
				log.info("{}.", "Cert Signing Request completed");
			}
			return ResponseEntity.ok(new StringWrapper(stringWriter.toString()));
		} catch (GeneralSecurityException e) {
			log.error("Error getting KeyPair Generator instance: {}", e.getMessage());
		} catch (OperatorCreationException e) {
			log.error("Error signing Private Key: {}", e.getMessage());
		} catch (IOException e) {
			log.error("Error with PEM Writer: {}", e.getMessage());
		}

		return ResponseEntity.ok(new StringWrapper("Hello World"));
	}

	@GetMapping(path = "/csr/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Meta> getJsonResponse(@AuthenticationPrincipal OidcUser user) {
		var response = client.get().uri("https://acme-staging-v02.api.letsencrypt.org/directory")
		.accept(MediaType.APPLICATION_JSON).retrieve().body(Meta.class);
		log.info("Response: {}", response);
		return ResponseEntity.ok(response);
	}
}
