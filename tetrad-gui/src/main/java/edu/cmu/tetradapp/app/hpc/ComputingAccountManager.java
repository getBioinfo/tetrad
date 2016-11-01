package edu.cmu.tetradapp.app.hpc;

import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import edu.pitt.dbmi.tetrad.db.entity.ComputingAccount;
import edu.pitt.dbmi.tetrad.db.service.ComputingAccountService;

/**
 * 
 * Oct 31, 2016 1:50:12 PM
 * 
 * @author Chirayu (Kong) Wongchokprasitti, PhD
 * 
 */
public class ComputingAccountManager {

    private final ComputingAccountService computingAccountService;

    public ComputingAccountManager(
	    final ComputingAccountService computingAccountService) {
	this.computingAccountService = computingAccountService;

	boolean initComputingAccount = (Preferences.userRoot()
		.get("computingAccountInitiated", "false").trim()
		.equalsIgnoreCase("true"));

	if (!initComputingAccount) {
	    initDemoAccount();
	}

    }

    private void initDemoAccount() {
	// Add ComputingAccount
	ComputingAccount computingAccount = computingAccountService
		.findByConnectionName("PSC");
	if (computingAccount == null) {
	    computingAccount = new ComputingAccount();
	    computingAccount.setConnectionName("PSC");
	    computingAccount.setUsername("chw20@pitt.edu");
	    computingAccount.setPassword("kongman20");
	    computingAccount.setScheme("https");
	    computingAccount.setHostname("ccd1.vm.bridges.psc.edu");
	    computingAccount.setPort(443);
	    computingAccount.setCreatedDate(new Date());
	    computingAccountService.add(computingAccount);
	}

	computingAccount = computingAccountService.findByConnectionName("AWS");
	if (computingAccount == null) {
	    computingAccount = new ComputingAccount();
	    computingAccount.setConnectionName("AWS");
	    computingAccount.setUsername("chw20@pitt.edu");
	    computingAccount.setPassword("kongman20");
	    computingAccount.setScheme("https");
	    computingAccount.setHostname("cloud.ccd.pitt.edu");
	    computingAccount.setPort(443);
	    computingAccount.setCreatedDate(new Date());
	    computingAccountService.add(computingAccount);
	}

	Preferences.userRoot().put("computingAccountInitiated", "true");
    }

    public List<ComputingAccount> getComputingAccounts() {
	List<ComputingAccount> computingAccounts = computingAccountService
		.get();
	return computingAccounts;
    }

}
