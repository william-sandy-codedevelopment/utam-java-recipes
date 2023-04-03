/*
 * Copyright (c) 2021, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: MIT
 * For full license text, see the LICENSE file in the repo root
 * or https://opensource.org/licenses/MIT
 */
package utam.utils.salesforce;

import java.util.ResourceBundle;
import java.util.Set;

import com.maxtaf.ApiService;

/**
 * Utility that reads properties file with environment information, format assuming that "sandbox"
 * is an environment name:
 *
 * <p>sandbox.url=https://sandbox.salesforce.com/
 *
 * <p>sandbox.username=my.user@salesforce.com
 *
 * <p>sandbox.password=secretPassword
 *
 * <p>Only 3 properties are required to use login via UI method. When creating instance of this
 * class, pass environment name prefix as parameter to constructor: new TestEnvironment("sandbox")
 *
 * @since 2021
 * @author salesforce
 */
public class TestEnvironment {

  private static final String MISSING_PROPERTY_ERR = "Property '%s' is not set in env.properties";

  private final String envPrefix;
  private final ApiService mxService;

  public TestEnvironment(String envNamePrefix, ApiService mxService) {
    this.envPrefix = envNamePrefix;
    this.mxService = mxService;
  }

  private static String wrapUrl(String url) {
    String transformed = url;
    // if url does not start from http or https - add
    if (!url.startsWith("http")) {
      transformed = "http://" + url;
    }
    // if url does not end with "/", add
    if (!url.endsWith("/")) {
      transformed = transformed.concat("/");
    }
    return transformed;
  }

  public String getBaseUrl() {
    return mxService.getParam(envPrefix + "." + "BaseURL");
  }

  public String getUserName() {
    return mxService.getParam(envPrefix + "." + "UserName");
  }

  public String getPassword() {
    return mxService.getParam(envPrefix + "." + "Password");
  }

  public String getSfdxLoginUrl() {
    return mxService.getParam(envPrefix + "." + "SfdxLoginUrl");
  }

  public String getRedirectUrl() {
    return mxService.getParam(envPrefix + "." + "RedirectUrl");
  }

  public String getAccountId() {
    return mxService.getParam(envPrefix + "." + "AccountId");
  }

  public String getContactId() {
    return mxService.getParam(envPrefix + "." + "ContactId");
  }

  public String getLeadId() {
    return mxService.getParam(envPrefix + "." + "LeadId");
  }
}
