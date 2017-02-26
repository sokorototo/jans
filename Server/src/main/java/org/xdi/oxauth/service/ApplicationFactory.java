/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.xdi.model.SmtpConfiguration;
import org.xdi.oxauth.crypto.random.RandomChallengeGenerator;
import org.xdi.oxauth.crypto.signature.SHA256withECDSASignatureVerification;
import org.xdi.oxauth.model.appliance.GluuAppliance;
import org.xdi.service.cache.CacheConfiguration;
import org.xdi.service.cache.InMemoryConfiguration;
import org.xdi.util.StringHelper;
import org.xdi.util.security.StringEncrypter.EncryptionException;

/**
 * Holds factory methods to create services
 *
 * @author Yuriy Movchan Date: 05/22/2015
 */
@Scope(ScopeType.APPLICATION)
@Name("applicationFactory")
@Startup
public class ApplicationFactory {
    
    @In
    private ApplianceService applianceService;

    @Logger
    private Log log;

    @Factory(value = "randomChallengeGenerator", scope = ScopeType.APPLICATION, autoCreate = true)
    public RandomChallengeGenerator createRandomChallengeGenerator() {
        return new RandomChallengeGenerator();
    }

    @Factory(value = "sha256withECDSASignatureVerification", scope = ScopeType.APPLICATION, autoCreate = true)
    public SHA256withECDSASignatureVerification createBouncyCastleSignatureVerification() {
        return new SHA256withECDSASignatureVerification();
    }

	@Factory(value = "cacheConfiguration", scope = ScopeType.APPLICATION, autoCreate = true)
	public CacheConfiguration createCacheConfiguration() {
		CacheConfiguration cacheConfiguration = applianceService.getAppliance().getCacheConfiguration();
		if (cacheConfiguration == null || cacheConfiguration.getCacheProviderType() == null) {
			log.error("Failed to read cache configuration from LDAP. Please check appliance oxCacheConfiguration attribute " +
					"that must contain cache configuration JSON represented by CacheConfiguration.class. Applieance DN: " + applianceService.getAppliance().getDn());
			log.info("Creating fallback IN-MEMORY cache configuration ... ");

			cacheConfiguration = new CacheConfiguration();
			cacheConfiguration.setInMemoryConfiguration(new InMemoryConfiguration());

			log.info("IN-MEMORY cache configuration is created.");
		}
		log.info("Cache configuration: " + cacheConfiguration);
		return cacheConfiguration;
	}

	@Factory(value = "smtpConfiguration", scope = ScopeType.APPLICATION, autoCreate = true)
	public SmtpConfiguration createSmtpConfiguration() {
		GluuAppliance appliance = applianceService.getAppliance();
		SmtpConfiguration smtpConfiguration = appliance.getSmtpConfiguration();
		
		if (smtpConfiguration == null) {
			return null;
		}

		String password = smtpConfiguration.getPassword();
		if (StringHelper.isNotEmpty(password)) {
			try {
				EncryptionService securityService = EncryptionService.instance();
				smtpConfiguration.setPasswordDecrypted(securityService.decrypt(password));
			} catch (EncryptionException ex) {
				log.error("Failed to decript SMTP user password", ex);
			}
		}
		
		return smtpConfiguration;
	}

}