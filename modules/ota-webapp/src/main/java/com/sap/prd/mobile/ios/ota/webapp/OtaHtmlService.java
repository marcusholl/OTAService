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

import static com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.BUNDLE_IDENTIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.BUNDLE_VERSION;
import static com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.IPA_CLASSIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.OTA_CLASSIFIER;
import static com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.TITLE;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator;
import com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator;
import com.sap.prd.mobile.ios.ota.lib.OtaHtmlGenerator.Parameters;

@SuppressWarnings("serial")
public class OtaHtmlService extends HttpServlet
{

  private final Logger LOG = Logger.getLogger(OtaPlistService.class.getSimpleName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    //TODO: REWORK. PlistService now uses Base64+URLEncoded parameters, and no URL Parameters but slashes!

    try {
      String originalReferer = Utils.getRefererSendError(request, response);
      //String referer = removeFilePartFromURL(originalReferer);

      URL plistUrl = OtaPlistGenerator.generatePlistRequestUrl(
            getPlistServiceUrl(request),
            originalReferer,
            request.getParameter(TITLE),
            request.getParameter(BUNDLE_IDENTIFIER),
            request.getParameter(BUNDLE_VERSION),
            request.getParameter(IPA_CLASSIFIER),
            request.getParameter(OTA_CLASSIFIER)
          );

      LOG.info(String.format("GET request from '%s' with referer '%s' and parameters '%s', '%s', '%s', '%s', '%s'",
            request.getRemoteAddr(), originalReferer, request.getParameter(TITLE), request
              .getParameter(BUNDLE_IDENTIFIER), request.getParameter(BUNDLE_VERSION),
            request.getParameter(IPA_CLASSIFIER), request.getParameter(OTA_CLASSIFIER)));

      PrintWriter writer = response.getWriter();
      OtaHtmlGenerator.getInstance().generate(writer,
            new Parameters(originalReferer, request.getParameter(TITLE), request.getParameter(BUNDLE_IDENTIFIER), plistUrl,
                  request.getParameter(IPA_CLASSIFIER), request.getParameter(OTA_CLASSIFIER)));
      writer.flush();
      writer.close();
    }
    catch (Exception e) {
      LOG.log(Level.SEVERE, String.format(
            "Exception while processing GET request from '%s'", request.getRemoteAddr()), e);
    }
  }

  String getPlistServiceUrl(HttpServletRequest request)
  {
    if (request.getRequestURL() == null) {
      return null;
    }
    String serviceUrl = request.getRequestURL().toString();
    int lastSlash = serviceUrl.lastIndexOf("/");
    serviceUrl = serviceUrl.substring(0, lastSlash);
    String plistServiceUrl = serviceUrl + "/" + OtaPlistService.SERVICE_NAME;
    return plistServiceUrl;
  }

}
