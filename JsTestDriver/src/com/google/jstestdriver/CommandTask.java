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

import static java.lang.String.format;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.jstestdriver.hooks.FileInfoScheme;
import com.google.jstestdriver.model.JstdTestCase;
import com.google.jstestdriver.util.StopWatch;

/**
 * Handles the communication of a command to the JsTestDriverServer from the
 * JsTestDriverClient.
 *
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTask {

  private static final Logger logger = LoggerFactory.getLogger(CommandTask.class);

  private final Gson gson = new Gson();

  private final ResponseStream stream;
  private final String baseUrl;
  private final Server server;
  private final Map<String, String> params;
  private final boolean upload;

  private final StopWatch stopWatch;

  private final FileUploader fileUploader;


  public CommandTask(JsTestDriverFileFilter filter, ResponseStream stream, String baseUrl,
      Server server, Map<String, String> params, FileLoader fileLoader,
      boolean upload, StopWatch stopWatch, Set<FileInfoScheme> schemes) {
    this.stream = stream;
    this.baseUrl = baseUrl;
    this.server = server;
    this.params = params;
    this.upload = upload;
    this.stopWatch = stopWatch;
    fileUploader = new FileUploader(stopWatch, server, baseUrl, fileLoader, filter, schemes);
  }
  
  /**
   * Throws an exception if the expected browser is not available for this task.
   */
  private void checkBrowser() {
    String alive = server.fetch(baseUrl + "/heartbeat?id=" + params.get("id"));

    if (!alive.equals("OK")) {
      throw new FailureException(
          format("Browser is not available\n {} \nfor\n {}", alive, params));
    }
  }

  public void run(JstdTestCase testCase) {
    String browserId = params.get("id");
    try {
      checkBrowser();

      logger.debug("Starting upload for {}", browserId);
      if (upload) {
        final Set<FileInfo> fileSet = Sets.newLinkedHashSet();
        fileSet.addAll(testCase.getDependencies());
        fileSet.addAll(testCase.getTests());
        fileUploader.uploadFileSet(browserId, fileSet, stream);
      }
      logger.debug("Finished upload for {}", browserId);
      server.post(baseUrl + "/cmd", params);
      StreamMessage streamMessage = null;

      stopWatch.start("execution %s", params.get("data"));
      logger.debug("Starting {} for {}", params.get("data"), browserId);
      do {
        String response = server.fetch(baseUrl + "/cmd?id=" + browserId);
        streamMessage = gson.fromJson(response, StreamMessage.class);
        Response resObj = streamMessage.getResponse();
        stream.stream(resObj);
      } while (!streamMessage.isLast());
      stopWatch.stop("execution %s", params.get("data"));
    } finally {
      logger.debug("finished {} for {}", params.get("data"), browserId);
    }
  }
}
