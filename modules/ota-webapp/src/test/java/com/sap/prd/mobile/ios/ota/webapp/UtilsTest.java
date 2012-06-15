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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.sap.prd.mobile.ios.ota.lib.LibUtils;
import com.sap.prd.mobile.ios.ota.webapp.Utils;

public class UtilsTest
{

  @Test
  public void testRemoveFilePartFromURL()
  {
    assertEquals("", Utils.removeFilePartFromURL(""));
    assertEquals("http://test:1234/Service", Utils.removeFilePartFromURL("http://test:1234/Service/file.htm"));
    assertEquals("http://test:1234/Service/abc", Utils.removeFilePartFromURL("http://test:1234/Service/abc"));
    assertEquals("http://test:1234/Service/abc/", Utils.removeFilePartFromURL("http://test:1234/Service/abc/"));
    assertEquals("http://test:1234/Service.txt/abc", Utils.removeFilePartFromURL("http://test:1234/Service.txt/abc"));
    assertEquals("http://test:1234/Ser", Utils.removeFilePartFromURL("http://test:1234/Ser/file.php?x=y"));
  }

  @Test
  public void testParseKeyValuePair() {
    testParseKeyValuePairCheck(null, true, 0);
    testParseKeyValuePairCheck("", false, 1, "");
    testParseKeyValuePairCheck("xyz", false, 1, "xyz");
    testParseKeyValuePairCheck("abc=xyz", false, 2, "abc", "xyz");
    testParseKeyValuePairCheck("=xyz", false, 2, "", "xyz");
    testParseKeyValuePairCheck("abc=", false, 2, "abc", "");
    testParseKeyValuePairCheck("=", false, 2, "", "");
    testParseKeyValuePairCheck("abc=xyz=hij", false, 2, "abc", "xyz=hij");
  }

  private void testParseKeyValuePairCheck(String value, boolean assertNull, int assertSize, String... expectedResult)
  {
    String[] result;
    result = Utils.parseKeyValuePair(value);
    if (assertNull) {
      assertNull(result);
    }
    else {
      assertNotNull(result);
      assertEquals(assertSize, result.length);
      for (int i = 0; i < assertSize; i++) {
        assertEquals(expectedResult[i], result[i]);
      }
    }
  }

  @Test
  public void testExtractParametersFromUri()
  {
    testExtractParametersFromUriCheck(
          "/blabla/SERVICE/" + LibUtils.encode("abc") + "/" + LibUtils.encode("def") + "/"
                + LibUtils.encode("xyz"),
          "SERVICE",
          3,
          new String[] { "abc" },
          new String[] { "def" },
          new String[] { "xyz" });
//    testExtractParametersFromUriCheck(
//          "/blabla/SERVICE/abc/def%3Dqwe/xyz",
//          "SERVICE",
//          3,
//          new String[] { "abc" },
//          new String[] { "def", "qwe" },
//          new String[] { "xyz" });
    testExtractParametersFromUriCheck(
          "/blabla/SERVICE/" + LibUtils.encode("abc") + "/" + LibUtils.encode("def=qwe") + "/"
                + LibUtils.encode("xyz"),
          "SERVICE",
          3,
          new String[] { "abc" },
          new String[] { "def", "qwe" },
          new String[] { "xyz" });
    testExtractParametersFromUriCheck(
          "/blabla/SERVICE/" + LibUtils.encode("abc") + "/",
          "SERVICE",
          1,
          new String[] { "abc" });
    testExtractParametersFromUriCheck(
          "/blabla/noservice/" + LibUtils.encode("abc") + "/",
          "SERVICE",
          -1); //null
    testExtractParametersFromUriCheck(
          null,
          "SERVICE",
          -1); //null
    testExtractParametersFromUriCheck(
          "",
          "SERVICE",
          -1); //null
  }

  private void testExtractParametersFromUriCheck(String uri, String serviceName, int expectedNr, String[]... expected)
  {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(uri);
    String[][] result = Utils.extractParametersFromUri(request, serviceName);
    if (expectedNr < 0) {
      assertNull(result);
      return;
    }
    assertEquals(expectedNr, result.length);
    assertEquals(expected.length, result.length);
    for (int i = 0; i < expectedNr; i++) {
      String[] resultElement = result[i];
      String[] expectedElement = expected[i];
      assertEquals(expectedElement.length, resultElement.length);
      for (int j = 0; j < expectedElement.length; j++) {
        assertEquals(expectedElement[j], resultElement[j]);
      }
    }
  }

  @Test
  public void generateBase64()
  {
    String referer = "Referer=http://wdfd00254211a.dhcp.wdf.sap.corp:8080/OTA/index.html";
    String title = "title=iSupport";
    String bundleIdentifier = "bundleIdentifier=com.sap.production.inhouse.isupport.internal";
    String bundleVersion = "bundleVersion=1.0";
    System.out.println(referer);
    System.out.println(LibUtils.encode(referer));
    System.out.println(title);
    System.out.println(LibUtils.encode(title));
    System.out.println(bundleIdentifier);
    System.out.println(LibUtils.encode(bundleIdentifier));
    System.out.println(bundleVersion);
    System.out.println(LibUtils.encode(bundleVersion));
  }

}
