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
package com.google.jstestdriver.eclipse.ui.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.views.JsTestDriverView;
import com.google.jstestdriver.eclipse.ui.views.TestResultsPanel;

/**
 * Handles a Js Test Driver launch.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JsTestDriverLaunchConfigurationDelegate implements
    ILaunchConfigurationDelegate2 {

  private final Logger logger = new Logger();
  private final ActionRunnerFactory actionRunnerFactory = new ActionRunnerFactory();

  public void launch(final ILaunchConfiguration configuration, String mode,
      final ILaunch launch, IProgressMonitor monitor) throws CoreException {
    if (!mode.equals(ILaunchManager.RUN_MODE)) {
      throw new IllegalStateException(
          "Can only launch JS Test Driver configuration from Run mode");
    }
    final List<String> testsToRun = configuration.getAttribute(
        LaunchConfigurationConstants.TESTS_TO_RUN, new ArrayList<String>());

    Display.getDefault().asyncExec(new Runnable() {

      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          JsTestDriverView view = (JsTestDriverView) page
              .showView("com.google.jstestdriver.eclipse.ui.views.JsTestDriverView");
          TestResultsPanel panel = view.getTestResultsPanel();
          panel.setupForNextTestRun(configuration);
          if (!testsToRun.isEmpty()) {
            panel.addNumberOfTests(testsToRun.size());
          }
        } catch (PartInitException e) {
          logger.logException(e);
        }
      }
    });

    if (testsToRun.isEmpty()) {
      actionRunnerFactory.getDryActionRunner(configuration).runActions();
      actionRunnerFactory.getAllTestsActionRunner(configuration).runActions();
    } else {
      // TODO(shyamseshadri): Handle the case where all the tests might not be run by the browser. 
      actionRunnerFactory.getSpecificTestsActionRunner(configuration, testsToRun).runActions();
    }
  }

  public boolean buildForLaunch(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) {
    return mode.equals(ILaunchManager.RUN_MODE) && !monitor.isCanceled();
  }

  public boolean finalLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) {
    return mode.equals(ILaunchManager.RUN_MODE) && !monitor.isCanceled();
  }

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) {
    if (mode.equals(ILaunchManager.RUN_MODE)) {
      return new Launch(configuration, mode, new JavascriptSourceLocator());
    } else {
      throw new IllegalStateException(
          "Can only launch JS Test Driver configuration from Run mode");
    }
  }

  public boolean preLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) {
    return mode.equals(ILaunchManager.RUN_MODE) && !monitor.isCanceled();
  }

}