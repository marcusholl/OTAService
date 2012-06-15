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

import static com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator.BUNDLE_IDENTIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator.BUNDLE_VERSION;
import static com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator.IPA_CLASSIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator.OTA_CLASSIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator.TITLE;
import static com.sap.prd.mobile.ios.ota.lib.TestUtils.assertContains;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator;
import com.sap.prd.mobile.ios.ota.lib.OtaBuildHtmlGenerator.Parameters;
public class OtaBuildHtmlGeneratorTest
{

  private final static String HTML_SERVICE = "http://apple-ota.wdf.sap.corp:8080/ota-service/HTML";

  private final static String title = "MyApp";
  private final static String bundleIdentifier = "com.sap.xyz.MyApp";
  private final static String bundleVersion = "1.0.2";
  private final static String ipaClassifier = "ipaClassifier";
  private final static String otaClassifier = "otaClassifier";


  @Test
  public void testCorrectValues() throws IOException
  {
    URL htmlServiceUrl = new URL(HTML_SERVICE);
    String generated = OtaBuildHtmlGenerator.getInstance().generate(
          new Parameters(htmlServiceUrl, title, bundleIdentifier, bundleVersion, ipaClassifier, otaClassifier));
    assertContains(TITLE + "=" + title, generated);
    assertContains(BUNDLE_IDENTIFIER + "=" + bundleIdentifier, generated);
    assertContains(BUNDLE_VERSION + "=" + bundleVersion, generated);
    assertContains(IPA_CLASSIFIER + "=" + ipaClassifier, generated);
    assertContains(OTA_CLASSIFIER + "=" + otaClassifier, generated);
    assertContains(
          "<iframe src=\""
                + HTML_SERVICE
                + "?title=MyApp&bundleIdentifier=com.sap.xyz.MyApp&bundleVersion=1.0.2&ipaClassifier=ipaClassifier&otaClassifier=otaClassifier\"",
          generated);
    assertContains("<form action=\"" + HTML_SERVICE + "\"", generated);
    assertContains("<input type=\"hidden\" name=\"title\" value=\"MyApp\">", generated);
    assertContains("<input type=\"hidden\" name=\"bundleIdentifier\" value=\"com.sap.xyz.MyApp\">", generated);
    assertContains("<input type=\"hidden\" name=\"bundleVersion\" value=\"1.0.2\">", generated);
    assertContains("<input type=\"hidden\" name=\"ipaClassifier\" value=\"ipaClassifier\">", generated);
    assertContains("<input type=\"hidden\" name=\"otaClassifier\" value=\"otaClassifier\">", generated);
  }

  @Test
  public void testProject() throws MalformedURLException, IOException
  {
    URL htmlServiceUrl = new URL(HTML_SERVICE);
    String generated = OtaBuildHtmlGenerator.getInstance().generate(
          new Parameters(htmlServiceUrl, "MyApp", "com.sap.tip.production.ios.ota.test", "1.0", "Production-iphoneos",
                "OTA-Installer"));
    System.out.println(generated);
  }

}
