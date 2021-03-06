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
package com.google.jstestdriver;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class HttpServerTest extends TestCase {

  public void testConvertMapToUrlEncodedString() throws Exception {
    Map<String, String> map = new LinkedHashMap<String, String>(); // preserves order
    HttpServer server = new HttpServer();

    map.put("data", "1+3");
    map.put("id", "2");
    assertEquals("data=1%2B3&id=2", server.convertParamsToString(map));
  }
}
