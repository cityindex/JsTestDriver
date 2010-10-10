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

import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.Command;
import com.google.jstestdriver.DefaultURLRewriter;
import com.google.jstestdriver.DefaultURLTranslator;
import com.google.jstestdriver.FileSource;
import com.google.jstestdriver.ForwardingMapper;
import com.google.jstestdriver.JsonCommand;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.TimeImpl;
import com.google.jstestdriver.JsonCommand.CommandType;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandPostHandlerTest extends TestCase {

  private final Gson gson = new Gson();

  public void testRewriteUrls() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserInfo browserInfo = new BrowserInfo();
    browserInfo.setId(1);
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), "1", browserInfo, SlaveBrowser.TIMEOUT);

    capturedBrowsers.addSlave(slave);
    CommandPostHandler handler = new CommandPostHandler(null, new Gson(),
        capturedBrowsers);
    List<String> parameters = Lists.newArrayList();
    List<FileSource> fileSources = Lists.newArrayList();

    fileSources.add(new FileSource("http://server/file1.js", -1));
    fileSources.add(new FileSource("http://server/file2.js", -1));
    parameters.add(gson.toJson(fileSources));
    parameters.add("false");
    JsonCommand command = new JsonCommand(CommandType.LOADTEST, parameters);

    handler.service("1", gson.toJson(command));
    Command cmd = slave.dequeueCommand();

    command = gson.fromJson(cmd.getCommand(), JsonCommand.class);
    assertEquals(2, command.getParameters().size());
    List<FileSource> changedFileSources =
        gson.fromJson(command.getParameters().get(0), new TypeToken<List<FileSource>>() {}
            .getType());

    assertEquals(2, changedFileSources.size());
    assertEquals("/forward/file1.js", changedFileSources.get(0).getFileSrc());
    assertEquals("http://server/file1.js", changedFileSources.get(0).getBasePath());
    assertEquals("/forward/file2.js", changedFileSources.get(1).getFileSrc());
    assertEquals("http://server/file2.js", changedFileSources.get(1).getBasePath());
  }
}