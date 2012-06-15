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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.sap.prd.mobile.ios.ota.lib.LibUtils;

public class LibUtilsTest
{

  @Test
  public void testGenerateDirectIpaUrl() throws MalformedURLException
  {
    assertEquals("http://localhost:8080/abc/test.ipa", LibUtils.generateDirectIpaUrl(
          "http://localhost:8080/abc/test.htm", null, null).toExternalForm());
    assertEquals("http://localhost:8080/abc/test.ipa", LibUtils.generateDirectIpaUrl(
          "http://localhost:8080/abc/test.html", null, null).toExternalForm());
    assertEquals("http://localhost:8080/abc/test.ipa", LibUtils.generateDirectIpaUrl(
          "http://localhost:8080/abc/test.jsp", null, null).toExternalForm());
    String ipaClassifier = "Production-iphoneos";
    String otaClassifier = "OTA";
    assertEquals("http://localhost:8080/abc/test-" + ipaClassifier + ".ipa", LibUtils.generateDirectIpaUrl(
          "http://localhost:8080/abc/test-" + otaClassifier + ".htm", ipaClassifier, otaClassifier).toExternalForm());
    assertEquals("http://localhost:8080/abc/test.ipa", LibUtils.generateDirectIpaUrl(
          "http://localhost:8080/abc/test-" + otaClassifier + ".htm", null, otaClassifier).toExternalForm());
    assertEquals("http://localhost:8080/abc/test-" + ipaClassifier + ".ipa", LibUtils.generateDirectIpaUrl(
          "http://localhost:8080/abc/test.htm", ipaClassifier, null).toExternalForm());
    assertEquals(
          "http://localhost:8080/abc/anything-" + otaClassifier + "xxx/test-" + ipaClassifier + ".ipa",
          LibUtils.generateDirectIpaUrl(
                "http://localhost:8080/abc/anything-" + otaClassifier + "xxx/test-" + otaClassifier + ".htm",
                ipaClassifier, otaClassifier).toExternalForm());
    try {
      LibUtils.generateDirectIpaUrl("http://localhost:8080/abc", null, null);
      fail("No exception");
    } catch(MalformedURLException e) {
    }
  }

  @Test
  public void testUrlurlEncodeurlDecode()
  {
    String s;
    s = "";
    assertEquals(s, LibUtils.decode(LibUtils.encode(s)));
    s = "abc";
    assertEquals(s, LibUtils.decode(LibUtils.encode(s)));
    s = "http://test.sap.com:8080/xyz";
    assertEquals(s, LibUtils.decode(LibUtils.encode(s)));
    s = "http://test.sap.com:8080/xyz?abc=xyz";
    assertEquals(s, LibUtils.decode(LibUtils.encode(s)));
    s = "http://test.sap.com:8080/xyz?abc=xyz%2F%3Dkkk";
    assertEquals(s, LibUtils.decode(LibUtils.encode(s)));
    s = "http://test.sap.com:8080/xyz?abc=xyz%2F%3Dpsduaspsahgpoahreghareiogreghrepogaporegnarehgnearugnp9rehvm0√ºeobnre√üuobnvnmvomregu08btrnhjbvu9ipsmdf,coipwrmv9wrenmv√ü9wr0g9eauonornvmw0e9rjpfmk√üsrojgm√ºreaiogn√ºwrapiongmreapinjheat√ºognea0b√ºpneamputezohirnhglxf.n,bm√ºiosth√º0seiornkhm dknhmd√ºo";
    assertEquals(OtaPlistGeneratorTest.long_identifier,
          LibUtils.decode(LibUtils.encode(OtaPlistGeneratorTest.long_identifier)));
    assertEquals(OtaPlistGeneratorTest.long_referer,
          LibUtils.decode(LibUtils.encode(OtaPlistGeneratorTest.long_referer)));
    assertEquals(OtaPlistGeneratorTest.long_service,
          LibUtils.decode(LibUtils.encode(OtaPlistGeneratorTest.long_service)));
    assertEquals(OtaPlistGeneratorTest.long_title,
          LibUtils.decode(LibUtils.encode(OtaPlistGeneratorTest.long_title)));
    assertEquals(OtaPlistGeneratorTest.long_version,
          LibUtils.decode(LibUtils.encode(OtaPlistGeneratorTest.long_version)));
  }

  @Test
  public void testUrlEncode() throws UnsupportedEncodingException
  {
    assertNull(LibUtils.urlEncode(null));
    assertEquals("", LibUtils.urlEncode(""));
    assertEquals("http%3A%2F%2Fwdfd00254211a.dhcp.wdf.sap.corp%3A8080%2Fota-webapp%2FPLIST", LibUtils.urlEncode(
      "http://wdfd00254211a.dhcp.wdf.sap.corp:8080/ota-webapp/PLIST"));
    assertEquals("MyAppName", LibUtils.urlEncode(
      "MyAppName"));
    assertEquals("com.sap.xyz.MyApp", LibUtils.urlEncode(
      "com.sap.xyz.MyApp"));
    assertEquals("1.0.4", LibUtils.urlEncode(
      "1.0.4"));
    assertEquals("Referer%3Dhttp%3A%2F%2Fwdfd00254211a.dhcp.wdf.sap.corp%3A8080%2Fota-webapp%2FPLIST",
          LibUtils.urlEncode(
      "Referer=http://wdfd00254211a.dhcp.wdf.sap.corp:8080/ota-webapp/PLIST"));
    assertEquals("title%3DMyAppName", LibUtils.urlEncode(
      "title=MyAppName"));
    assertEquals("bundleIdentifier%3Dcom.sap.xyz.MyApp", LibUtils.urlEncode(
      "bundleIdentifier=com.sap.xyz.MyApp"));
    assertEquals("bundleVersion%3D1.0.4", LibUtils.urlEncode(
      "bundleVersion=1.0.4"));
  }

  @Test
  public void testUrlDecode() throws UnsupportedEncodingException
  {
    assertNull(LibUtils.urlDecode(null));
    assertEquals("", LibUtils.urlDecode(""));
    assertEquals("http://wdfd00254211a.dhcp.wdf.sap.corp:8080/ota-webapp/PLIST", LibUtils.urlDecode(
      "http%3A%2F%2Fwdfd00254211a.dhcp.wdf.sap.corp%3A8080%2Fota-webapp%2FPLIST"));
    assertEquals("MyAppName", LibUtils.urlDecode(
      "MyAppName"));
    assertEquals("com.sap.xyz.MyApp", LibUtils.urlDecode(
      "com.sap.xyz.MyApp"));
    assertEquals("1.0.4", LibUtils.urlDecode(
      "1.0.4"));
    assertEquals("Referer=http://wdfd00254211a.dhcp.wdf.sap.corp:8080/ota-webapp/PLIST", LibUtils.urlDecode(
      "Referer%3Dhttp%3A%2F%2Fwdfd00254211a.dhcp.wdf.sap.corp%3A8080%2Fota-webapp%2FPLIST"));
    assertEquals("title=MyAppName", LibUtils.urlDecode(
      "title%3DMyAppName"));
    assertEquals("bundleIdentifier=com.sap.xyz.MyApp", LibUtils.urlDecode(
      "bundleIdentifier%3Dcom.sap.xyz.MyApp"));
    assertEquals("bundleVersion=1.0.4", LibUtils.urlDecode(
      "bundleVersion%3D1.0.4"));
  }

  @Test
  public void testReplaceLast()
  {
    assertEquals("Hello-WXrld", LibUtils.replaceLast(
          "Hello-World", "o", "X"));
    assertEquals("Hello-Xorld", LibUtils.replaceLast(
          "Hello-World", "W", "X"));
    assertEquals("Hello-World", LibUtils.replaceLast(
          "Hello-World", "V", "X"));
    assertEquals("Hello-World", LibUtils.replaceLast(
          "Hello-World", "", "X"));
    assertEquals("Hello-Wrld", LibUtils.replaceLast(
          "Hello-World", "o", ""));
    assertEquals("Hello-World", LibUtils.replaceLast(
          "Hello-World", "", ""));
    try {
      assertEquals("Hello-World", LibUtils.replaceLast(null, "x", "x"));
      fail("No NullPointerException");
    }
    catch (NullPointerException e) {
    }
    try {
      assertEquals("Hello-World", LibUtils.replaceLast("Hello-World", null, "x"));
      fail("No NullPointerException");
    }
    catch (NullPointerException e) {
    }
    try {
      assertEquals("Hello-World", LibUtils.replaceLast("Hello-World", "x", null));
      fail("No NullPointerException");
    }
    catch (NullPointerException e) {
    }
  }

}
