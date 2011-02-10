/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.server.handlers;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.mortbay.servlet.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@link RequestHandler} that proxies requests by delegating to a
 * {@link ProxyServlet.Transparent}.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyRequestHandler implements RequestHandler {

  public interface Factory {
    ProxyRequestHandler create(ProxyServlet.Transparent proxy);
  }

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final ProxyServlet.Transparent proxy;

  @Inject
  public ProxyRequestHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      @Assisted ProxyServlet.Transparent proxy) {
    this.request = request;
    this.response = response;
    this.proxy = proxy;
  }

  public void handleIt() throws IOException {
    try {
      proxy.service(request, response);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
  }
}
