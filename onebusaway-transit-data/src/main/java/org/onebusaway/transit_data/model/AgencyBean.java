/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
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
package org.onebusaway.transit_data.model;

import java.io.Serializable;

public class AgencyBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String url;

  private String timezone;

  private String lang;

  private String phone;

  private String disclaimer;

  private boolean privateService;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getDisclaimer() {
    return disclaimer;
  }

  public void setDisclaimer(String disclaimer) {
    this.disclaimer = disclaimer;
  }

  /**
   * @return if true, indicates the agency provides private service that is not
   *         available to the general public.
   */
  public boolean isPrivateService() {
    return privateService;
  }

  public void setPrivateService(boolean privateService) {
    this.privateService = privateService;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AgencyBean{ id=");
    builder.append(id);
    builder.append("; name=");
    builder.append(name);
    builder.append("; url=");
    builder.append(url);
    builder.append("; timezone=");
    builder.append(timezone);
    builder.append("; lang=");
    builder.append(lang);
    builder.append("; phone=");
    builder.append(phone);
    builder.append("; disclaimer=");
    builder.append(disclaimer);
    builder.append("; privateService=");
    builder.append(privateService);
    builder.append(" }");
    return builder.toString();
  }

}
