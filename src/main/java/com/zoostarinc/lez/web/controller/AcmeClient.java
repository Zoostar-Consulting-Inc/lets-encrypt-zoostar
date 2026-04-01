package com.zoostarinc.lez.web.controller;
import java.security.KeyPair;
import java.util.Optional;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcmeClient {
    public void fetchCertificate() throws Exception {
        // 1. Setup a session (Let's Encrypt Staging for testing!)
        Session session = new Session("https://acme-staging-v02.api.letsencrypt.org/directory");

        // 2. Get or Create a User KeyPair
        KeyPair userKeyPair = KeyPairUtils.createKeyPair(2048);

        // 3. Log in or Create Account
        Account account = new AccountBuilder()
                .agreeToTermsOfService()
                .useKeyPair(userKeyPair)
                .create(session);

        // 4. Order a certificate for your domain
        Order order = account.newOrder()
                .domain("yourdomain.com")
                .create();

        // 5. Handle Challenges (HTTP-01 example)
        for (Authorization auth : order.getAuthorizations()) {
        	Optional<Http01Challenge> object = auth.findChallenge(Http01Challenge.TYPE);
            if(object.isPresent()) {
            	var challenge = object.get();

            	// Output these values to your web server so Let's Encrypt can see them
	            log.info("Path: {}", challenge.getToken());
	            log.info("Content: {}", challenge.getAuthorization());
	
	            // Trigger the check
	            challenge.trigger();
	            
	            // Poll until valid (simplified)
	            while (auth.getStatus() != Status.VALID) {
	                Thread.sleep(3000);
	                var status = auth.getStatus();
	                log.info("Auth status: {}", status);
	            }
            }
        }

        // 6. Finalize with a CSR
        KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
        CSRBuilder csr = new CSRBuilder();
        csr.addDomain("poker.apigator.net");
        csr.sign(domainKeyPair);

        order.execute(csr.getEncoded());

        // 7. Download the cert
        Certificate certificate = order.getCertificate();
        log.info("Cert URL: {}", certificate.getLocation());
    }
}