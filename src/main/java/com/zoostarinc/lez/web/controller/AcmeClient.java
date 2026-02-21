package com.zoostarinc.lez.web.controller;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.security.KeyPair;

public class AcmeClient {
    public void fetchCertificate() throws Exception {
        // 1. Setup a session (Let's Encrypt Staging for testing!)
        Session session = new Session("acme://letsencrypt.org/staging");

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
            Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
            
            // Output these values to your web server so Let's Encrypt can see them
            System.out.println("Path: " + challenge.getToken());
            System.out.println("Content: " + challenge.getAuthorization());

            // Trigger the check
            challenge.trigger();
            
            // Poll until valid (simplified)
            while (auth.getStatus() != Status.VALID) {
                Thread.sleep(3000);
                auth.update();
            }
        }

        // 6. Finalize with a CSR
        KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
        CSRBuilder csr = new CSRBuilder();
        csr.addDomain("yourdomain.com");
        csr.sign(domainKeyPair);

        order.execute(csr.getEncoded());

        // 7. Download the cert
        Certificate certificate = order.getCertificate();
        System.out.println("Cert URL: " + certificate.getLocation());
    }
}