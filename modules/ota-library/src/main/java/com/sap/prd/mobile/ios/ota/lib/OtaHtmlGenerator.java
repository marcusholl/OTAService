/*
 * #%L
 * Over-the-air deployment library
 * %%
 * Copyright (C) 2012 SAP AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sap.prd.mobile.ios.ota.lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.Parameters;

/**
 * This class generates the HTML page which is created by the OTA HTML service.
 */
public class OtaHtmlGenerator extends VelocityBase<Parameters>
{

  public final static String IPA_URL = "ipaUrl";
  public final static String PLIST_URL = "plistUrl";
  public static final String TITLE = "title";
  public static final String BUNDLE_IDENTIFIER = "bundleIdentifier";
  public static final String BUNDLE_VERSION = "bundleVersion";
  public static final String IPA_CLASSIFIER = "ipaClassifier";
  public static final String OTA_CLASSIFIER = "otaClassifier";
  
  /**
   * Parameters required for the <code>OtaHtmlGenerator</code>.
   */
  public static class Parameters extends com.sap.prd.mobile.ios.ota.lib.VelocityBase.Parameters
  {
    /**
     * @param referer
     *          The original referer to the initial HTML page (e.g. in Nexus)
     * @param title
     *          The title of the App
     * @param bundleIdentifier
     *          The bundle identifier
     * @param plistUrl
     *          The complete OTA PLIST Service URL for this App containing all parameters.
     * @param ipaClassifier
     *          The classifier used in the IPA artifact. If null no classifier will be used.
     * @param otaClassifier
     *          The classifier used in the OTA HTML artifact. If null no classifier will be used.
     * @throws MalformedURLException
     */
    public Parameters(String referer, String title, String bundleIdentifier, URL plistUrl, String ipaClassifier,
          String otaClassifier, Map<String, String> initParams)
          throws MalformedURLException
    {
      super();
      URL ipaUrl = LibUtils.generateDirectIpaUrl(referer, ipaClassifier, otaClassifier);
      if(initParams != null) {
        for(String name : initParams.keySet()) {
          mappings.put(name, initParams.get(name));
        }
      }
      mappings.put(IPA_URL, ipaUrl.toExternalForm());
      mappings.put(BUNDLE_IDENTIFIER, bundleIdentifier);
      mappings.put(PLIST_URL, plistUrl.toExternalForm());
      mappings.put(TITLE, title);
    }
  }

  static final String DEFAULT_TEMPLATE = "template.html";
  private static OtaHtmlGenerator instance = null;

  public static synchronized OtaHtmlGenerator getInstance()
  {
    if (instance == null) {
      instance = new OtaHtmlGenerator(null);
    }
    return instance;
  }

  public static synchronized OtaHtmlGenerator getNewInstance(String template) throws FileNotFoundException
  {
    return new OtaHtmlGenerator(template);
  }

  private OtaHtmlGenerator(String template)
  {
    super(validateTemplate(template));
  }

  private static String validateTemplate(String template)
  {
    if(template == null || template.trim().length() == 0) return DEFAULT_TEMPLATE;
    return template;
  }

  @Override
  public synchronized String generate(Parameters parameters) throws IOException
  {
    return super.generate(parameters);
  }

  @Override
  public synchronized void generate(PrintWriter writer, Parameters parameters) throws IOException
  {
    super.generate(writer, parameters);
  }

  /**
   * Generates the URL for a specific request to the HTML service.
   * 
   * @param htmlServiceUrl
   *          The base URL to the service. E.g. http://apple-ota.wdf.sap.corp:1080/ota-service/HTML
   * @param title
   *          The title of the App
   * @param bundleIdentifier
   *          The bundleIdentifier of the App
   * @param bundleVersion
   *          The bundleVersion of the App
   * @param ipaClassifier
   *          The classifier used in the IPA artifact. If null no classifier will be used.
   * @param otaClassifier
   *          The classifier used in the OTA HTML artifact. If null no classifier will be used.
   * @return the URL
   * @throws MalformedURLException
   */
  public static URL generateHtmlServiceUrl(URL htmlServiceUrl, String title, String bundleIdentifier,
        String bundleVersion, String ipaClassifier, String otaClassifier) throws MalformedURLException
  {
    return new URL(String.format("%s?%s=%s&%s=%s&%s=%s%s%s",
          htmlServiceUrl.toExternalForm(),
          TITLE, LibUtils.urlEncode(title),
          BUNDLE_IDENTIFIER, LibUtils.urlEncode(bundleIdentifier),
          BUNDLE_VERSION, LibUtils.urlEncode(bundleVersion),
          (StringUtils.isEmpty(ipaClassifier) ? "" :
                String.format("&%s=%s", IPA_CLASSIFIER, LibUtils.urlEncode(ipaClassifier))),
          (StringUtils.isEmpty(otaClassifier) ? "" :
                String.format("&%s=%s", OTA_CLASSIFIER, LibUtils.urlEncode(otaClassifier)))
      ));
  }

}
