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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator;
import com.sap.prd.mobile.ios.ota.lib.OtaPlistGenerator.Parameters;

@SuppressWarnings("serial")
public class OtaPlistService extends HttpServlet
{

  private final Logger LOG = Logger.getLogger(OtaPlistService.class.getSimpleName());

  public final static String SERVICE_NAME = "PLIST"; //todo: dynamic

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    try {
      String title;
      String bundleIdentifier;
      String bundleVersion;
      String ipaClassifier = null;
      String otaClassifier = null;

      String originalReferer = Utils.getReferer(request);
      //String referer;

      if(request.getParameterMap().size() != 0) {
        title = request.getParameter(OtaPlistGenerator.TITLE);
        bundleIdentifier = request.getParameter(OtaPlistGenerator.BUNDLE_IDENTIFIER);
        bundleVersion = request.getParameter(OtaPlistGenerator.BUNDLE_VERSION);
        ipaClassifier = request.getParameter(OtaPlistGenerator.IPA_CLASSIFIER);
        otaClassifier = request.getParameter(OtaPlistGenerator.OTA_CLASSIFIER);
      }
      else { //handling with slashes to separate the parameters
        String[][] extractedParametersFromUri = Utils.extractParametersFromUri(request, SERVICE_NAME);
        String uriReferer = Utils.getValueFromUriParameterMap(extractedParametersFromUri, OtaPlistGenerator.REFERER);
        originalReferer = uriReferer == null ? originalReferer : uriReferer;
        title = Utils.getValueFromUriParameterMap(extractedParametersFromUri, OtaPlistGenerator.TITLE);
        bundleIdentifier = Utils.getValueFromUriParameterMap(extractedParametersFromUri,
              OtaPlistGenerator.BUNDLE_IDENTIFIER);
        bundleVersion = Utils.getValueFromUriParameterMap(extractedParametersFromUri, OtaPlistGenerator.BUNDLE_VERSION);
        ipaClassifier = Utils.getValueFromUriParameterMap(extractedParametersFromUri, OtaPlistGenerator.IPA_CLASSIFIER);
        otaClassifier = Utils.getValueFromUriParameterMap(extractedParametersFromUri, OtaPlistGenerator.OTA_CLASSIFIER);
      }

      if (originalReferer == null) {
        response.sendError(400, "Referer required");
        return;
      }
      //referer = removeFilePartFromURL(originalReferer);

      LOG.info(String.format("GET request from '%s' with referer '%s' and parameters '%s', '%s', '%s'",
            request.getRemoteAddr(), originalReferer, title, bundleIdentifier, bundleVersion));

      PrintWriter writer = response.getWriter();
      OtaPlistGenerator.getInstance().generate(writer,
            new Parameters(originalReferer, title, bundleIdentifier, bundleVersion, ipaClassifier, otaClassifier));
      writer.flush();
      writer.close();
    }
    catch (Exception e) {
      LOG.log(Level.SEVERE, String.format(
            "Exception while processing GET request from '%s'", request.getRemoteAddr()), e);
    }
  }

}
