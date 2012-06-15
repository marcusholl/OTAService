/*
 * #%L
 * Over-the-air deployment webapp
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
package com.sap.prd.mobile.ios.ota.webapp;

import static com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.BUNDLE_IDENTIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.BUNDLE_VERSION;
import static com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.IPA_CLASSIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.OTA_CLASSIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.REFERER;
import static com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.TITLE;
import static com.sap.prd.mobile.ios.ota.webapp.TestUtils.assertContains;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator;
import com.sap.prd.mobile.ios.ota.webapp.OtaHtmlService;

public class OtaHtmlServiceTest
{

  final static String TEST_SERVICE_URL = "http://ota-server:8080/HTML";

  final static String TEST_REFERER = "http://nexus:8081/abc/MyHHH.htm";
  final static String TEST_IPA_LINK = "http://nexus:8081/abc/MyHHH.ipa";
  final static String TEST_TITLE = "TestXYZ";
  static final String TEST_BUNDLEIDENTIFIER = "com.sap.myapp.MyABC";
  static final String TEST_BUNDLEVERSION = "1.0.5";
  static final String TEST_OTACLASSIFIER = "otaClassifier";
  static final String TEST_IPACLASSIFIER = "ipaClassifier";
  final static String TEST_REFERER_WITH_CLASSIFIER = "http://nexus:8081/abc/MyHHH-" + OTA_CLASSIFIER + ".htm";
  final static String TEST_IPA_LINK_WITH_CLASSIFIER = "http://nexus:8081/abc/MyHHH-" + IPA_CLASSIFIER + ".ipa";

  private static URL TEST_PLIST_URL;
  private static String TEST_OTA_LINK;
  private static URL TEST_PLIST_URL_WITH_CLASSIFIERS;
  private static String TEST_OTA_LINK_WITH_CLASSIFIERS;


  private final static String CHECK_TITLE = String.format("Install App: %s", TEST_TITLE);

  @BeforeClass
  public static void beforeClass() throws IOException
  {
    TEST_PLIST_URL = OtaPlistGenerator.generatePlistRequestUrl(
          "http://ota-server:8080/PLIST", TEST_REFERER, TEST_TITLE, TEST_BUNDLEIDENTIFIER, TEST_BUNDLEVERSION,
          null, null);
    TEST_OTA_LINK = String.format("<a href='itms-services:///?action=download-manifest&url=%s'>", TEST_PLIST_URL);
    TEST_PLIST_URL_WITH_CLASSIFIERS = OtaPlistGenerator.generatePlistRequestUrl(
          "http://ota-server:8080/PLIST", TEST_REFERER_WITH_CLASSIFIER, TEST_TITLE, TEST_BUNDLEIDENTIFIER,
          TEST_BUNDLEVERSION, IPA_CLASSIFIER, OTA_CLASSIFIER);
    TEST_OTA_LINK_WITH_CLASSIFIERS = String.format("<a href='itms-services:///?action=download-manifest&url=%s'>",
          TEST_PLIST_URL_WITH_CLASSIFIERS);
  }

  @Before
  public void before()
  {
    assertNotNull(TEST_PLIST_URL);
    assertNotNull(TEST_OTA_LINK);
  }

  @Test
  public void testCorrectValues() throws ServletException, IOException
  {
    OtaHtmlService service = new OtaHtmlService();
    StringWriter writer = new StringWriter();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(REFERER)).thenReturn(TEST_REFERER);
    when(request.getRequestURL()).thenReturn(new StringBuffer(TEST_SERVICE_URL));
    when(request.getParameter(TITLE)).thenReturn(TEST_TITLE);
    when(request.getParameter(BUNDLE_IDENTIFIER)).thenReturn(TEST_BUNDLEIDENTIFIER);
    when(request.getParameter(BUNDLE_VERSION)).thenReturn(TEST_BUNDLEVERSION);
    when(request.getParameter(IPA_CLASSIFIER)).thenReturn(null);
    when(request.getParameter(OTA_CLASSIFIER)).thenReturn(null);

    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(writer));

    service.doPost(request, response);

    String result = writer.getBuffer().toString();
    assertContains(CHECK_TITLE, result);
    assertContains(TEST_IPA_LINK, result);
    assertContains(TEST_OTA_LINK, result);
    assertContains(TEST_PLIST_URL.toExternalForm(), result);
  }

  @Test
  public void testWithClassifiers() throws ServletException, IOException
  {
    OtaHtmlService service = new OtaHtmlService();
    StringWriter writer = new StringWriter();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(REFERER)).thenReturn(TEST_REFERER_WITH_CLASSIFIER);
    when(request.getRequestURL()).thenReturn(new StringBuffer(TEST_SERVICE_URL));
    when(request.getParameter(TITLE)).thenReturn(TEST_TITLE);
    when(request.getParameter(BUNDLE_IDENTIFIER)).thenReturn(TEST_BUNDLEIDENTIFIER);
    when(request.getParameter(BUNDLE_VERSION)).thenReturn(TEST_BUNDLEVERSION);
    when(request.getParameter(IPA_CLASSIFIER)).thenReturn(TEST_IPACLASSIFIER);
    when(request.getParameter(OTA_CLASSIFIER)).thenReturn(TEST_OTACLASSIFIER);

    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(writer));

    service.doPost(request, response);

    String result = writer.getBuffer().toString();
    assertContains(CHECK_TITLE, result);
    assertContains(TEST_IPA_LINK_WITH_CLASSIFIER, result);
    assertContains(TEST_OTA_LINK_WITH_CLASSIFIERS, result);
    assertContains(TEST_PLIST_URL_WITH_CLASSIFIERS.toExternalForm(), result);
  }

}
