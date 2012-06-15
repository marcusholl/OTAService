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

import static com.sap.prd.mobile.ios.ota.lib.LibUtils.encode;
import static com.sap.prd.mobile.ios.ota.webapp.OtaHtmlServiceTest.TEST_BUNDLEIDENTIFIER;
import static com.sap.prd.mobile.ios.ota.webapp.OtaHtmlServiceTest.TEST_BUNDLEVERSION;
import static com.sap.prd.mobile.ios.ota.webapp.OtaHtmlServiceTest.TEST_IPA_LINK;
import static com.sap.prd.mobile.ios.ota.webapp.OtaHtmlServiceTest.TEST_REFERER;
import static com.sap.prd.mobile.ios.ota.webapp.OtaHtmlServiceTest.TEST_TITLE;
import static com.sap.prd.mobile.ios.ota.webapp.TestUtils.assertContains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator;
import com.sap.prd.mobile.ios.ota.webapp.OtaPlistService;

public class OtaPlistServiceTest
{
  private final static String STRING_TAG_START = "<string>";
  private final static String STRING_TAG_END = "</string>";

  @Test
  public void testWithURLParameters() throws ServletException, IOException
  {
    OtaPlistService service = new OtaPlistService();
    StringWriter writer = new StringWriter();

    HttpServletRequest request = mock(HttpServletRequest.class);

    Map<String, String[]> paramsDummy = new HashMap<String, String[]>();
    paramsDummy.put("x", null);
    when(request.getParameterMap()).thenReturn(paramsDummy); //size is checked in service
    when(request.getParameter(OtaPlistGenerator.REFERER)).thenReturn(TEST_REFERER);
    when(request.getParameter(OtaPlistGenerator.TITLE)).thenReturn(TEST_TITLE);
    when(request.getParameter(OtaPlistGenerator.BUNDLE_IDENTIFIER)).thenReturn(TEST_BUNDLEIDENTIFIER);
    when(request.getParameter(OtaPlistGenerator.BUNDLE_VERSION)).thenReturn(TEST_BUNDLEVERSION);

    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(writer));

    service.doGet(request, response);

    String result = writer.getBuffer().toString();
    assertContains(STRING_TAG_START + TEST_TITLE + STRING_TAG_END, result);
    assertContains(STRING_TAG_START + TEST_BUNDLEVERSION + STRING_TAG_END, result);
    assertContains(STRING_TAG_START + TEST_BUNDLEIDENTIFIER + STRING_TAG_END, result);
    assertContains(STRING_TAG_START + TEST_IPA_LINK + STRING_TAG_END, result);
  }

  @Test
  public void testWithSlashSeparatedParameters() throws ServletException, IOException
  {
    OtaPlistService service = new OtaPlistService();
    StringWriter writer = new StringWriter();

    HttpServletRequest request = mock(HttpServletRequest.class);

    StringBuilder sb = new StringBuilder();
    sb.append("/abc/").append(OtaPlistService.SERVICE_NAME).append("/");
    sb.append(encode(OtaPlistGenerator.REFERER + "=" + TEST_REFERER)).append("/");
    sb.append(encode(OtaPlistGenerator.TITLE + "=" + TEST_TITLE)).append("/");
    sb.append(encode(OtaPlistGenerator.BUNDLE_IDENTIFIER + "=" + TEST_BUNDLEIDENTIFIER)).append("/");
    sb.append(encode(OtaPlistGenerator.BUNDLE_VERSION + "=" + TEST_BUNDLEVERSION));
    when(request.getRequestURI()).thenReturn(sb.toString());

    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(writer));

    service.doGet(request, response);

    String result = writer.getBuffer().toString();
    assertContains(STRING_TAG_START + TEST_TITLE + STRING_TAG_END, result);
    assertContains(STRING_TAG_START + TEST_BUNDLEVERSION + STRING_TAG_END, result);
    assertContains(STRING_TAG_START + TEST_BUNDLEIDENTIFIER + STRING_TAG_END, result);
    assertContains(STRING_TAG_START + TEST_IPA_LINK + STRING_TAG_END, result);
  }

}
