/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.transit_data.model;

import java.util.List;

import org.onebusaway.transit_data.model.service_alerts.ServiceAlertBean;

public class StopsWithArrivalsAndDeparturesBean extends ApplicationBean {

  private static final long serialVersionUID = 1L;

  private List<StopBean> stops;

  private List<ArrivalAndDepartureBean> arrivalsAndDepartures;

  private List<StopBean> nearbyStops;

  private List<ServiceAlertBean> situations;

  private String timeZone;

  public StopsWithArrivalsAndDeparturesBean() {

  }

  public StopsWithArrivalsAndDeparturesBean(List<StopBean> stops,
      List<ArrivalAndDepartureBean> arrivalsAndDepartures,
      List<StopBean> nearbyStops, List<ServiceAlertBean> situations) {
    this.stops = stops;
    this.arrivalsAndDepartures = arrivalsAndDepartures;
    this.nearbyStops = nearbyStops;
    this.situations = situations;
  }

  public List<StopBean> getStops() {
    return stops;
  }

  public void setStops(List<StopBean> stops) {
    this.stops = stops;
  }

  public List<ArrivalAndDepartureBean> getArrivalsAndDepartures() {
    return arrivalsAndDepartures;
  }

  public void setArrivalsAndDepartures(
      List<ArrivalAndDepartureBean> arrivalsAndDepartures) {
    this.arrivalsAndDepartures = arrivalsAndDepartures;
  }

  public List<StopBean> getNearbyStops() {
    return nearbyStops;
  }

  public void setNearbyStops(List<StopBean> nearbyStops) {
    this.nearbyStops = nearbyStops;
  }

  public List<ServiceAlertBean> getSituations() {
    return situations;
  }

  public void setSituations(List<ServiceAlertBean> situations) {
    this.situations = situations;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  @Override
  public String toString() {
    final int maxLen = 64;
    StringBuilder builder = new StringBuilder();
    builder.append("StopsWithArrivalsAndDeparturesBean{ stops=");
    builder.append(stops != null ? stops.subList(0,
        Math.min(stops.size(), maxLen)) : null);
    builder.append("; arrivalsAndDepartures=");
    builder.append(arrivalsAndDepartures != null
        ? arrivalsAndDepartures.subList(0,
            Math.min(arrivalsAndDepartures.size(), maxLen)) : null);
    builder.append("; nearbyStops=");
    builder.append(nearbyStops != null ? nearbyStops.subList(0,
        Math.min(nearbyStops.size(), maxLen)) : null);
    builder.append("; situations=");
    builder.append(situations != null ? situations.subList(0,
        Math.min(situations.size(), maxLen)) : null);
    builder.append("; timeZone=");
    builder.append(timeZone);
    builder.append(" }");
    return builder.toString();
  }
}
