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

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserInfo {

  private Long id;
  private String name;
  private String version;
  private String os;
  private Integer uploadSize = FileUploader.CHUNK_SIZE;
  private boolean serverReceivedHeartbeat;

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getOs() {
    return os;
  }

  public boolean serverReceivedHeartbeat() {
    return serverReceivedHeartbeat;
  }

  public void setServerReceivedHeartbeat(boolean serverReceivedHeartbeat) {
    this.serverReceivedHeartbeat = serverReceivedHeartbeat;
  }

  @Override
  public String toString() {
    return String.format("%s %s %s", name, version, os);
  }

  public String toUniqueString() {
    return toString() + " " + id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BrowserInfo that = (BrowserInfo) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    result = (id != null ? id.hashCode() : 0);
    return result;
  }

  public int getUploadSize() {
    return uploadSize;
  }

  public void setUploadSize(Integer uploadSize) {
    this.uploadSize = uploadSize;
  }
}
