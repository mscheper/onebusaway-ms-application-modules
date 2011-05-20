package org.onebusaway.transit_data.model.tripplanning;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ConstraintsBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private boolean departNow = true;

  private boolean arriveBy = false;

  private int resultCount = 3;

  private boolean useRealTime = false;

  private Set<String> modes = null;

  private int maxTripDuration = -1;

  private String optimizeFor;

  private double walkSpeed = -1;

  private double walkReluctance = -1;

  private double maxWalkingDistance = -1;

  private double initialWaitReluctance = -1;

  private double waitReluctance = -1;

  private int minTransferTime = -1;

  private int transferCost = -1;

  private int maxTransfers = -1;

  private long maxComputationTime = -1;

  /**
   * Why do we include an alternate definition of the current time? It's useful
   * when attempting to run simulated queries in the past
   */
  private long currentTime = -1;

  private ItineraryBean selectedItinerary = null;

  public ConstraintsBean() {

  }

  public ConstraintsBean(ConstraintsBean c) {
    this.departNow = c.departNow;
    this.arriveBy = c.arriveBy;
    this.initialWaitReluctance = c.initialWaitReluctance;
    this.maxTransfers = c.maxTransfers;
    this.maxTripDuration = c.maxTripDuration;
    this.maxWalkingDistance = c.maxWalkingDistance;
    this.minTransferTime = c.minTransferTime;
    if (c.modes != null)
      this.modes = new HashSet<String>(c.modes);
    this.optimizeFor = c.optimizeFor;
    this.resultCount = c.resultCount;
    this.transferCost = c.transferCost;
    this.useRealTime = c.useRealTime;
    this.waitReluctance = c.waitReluctance;
    this.walkReluctance = c.walkReluctance;
    this.walkSpeed = c.walkSpeed;
  }

  public boolean isDepartNow() {
    return departNow;
  }

  public void setDepartNow(boolean departNow) {
    this.departNow = departNow;
  }

  public boolean isArriveBy() {
    return arriveBy;
  }

  public void setArriveBy(boolean arriveBy) {
    this.arriveBy = arriveBy;
  }

  public int getResultCount() {
    return resultCount;
  }

  public void setResultCount(int resultCount) {
    this.resultCount = resultCount;
  }

  public boolean isUseRealTime() {
    return useRealTime;
  }

  public void setUseRealTime(boolean useRealTime) {
    this.useRealTime = useRealTime;
  }

  public Set<String> getModes() {
    return modes;
  }

  public void setModes(Set<String> modes) {
    this.modes = modes;
  }

  /**
   * @return maximum trip duration, in seconds
   */
  public int getMaxTripDuration() {
    return maxTripDuration;
  }

  /**
   * @param maxTripDuration time, in seconds
   */
  public void setMaxTripDuration(int maxTripDuration) {
    this.maxTripDuration = maxTripDuration;
  }

  public String getOptimizeFor() {
    return optimizeFor;
  }

  public void setOptimizeFor(String optimizeFor) {
    this.optimizeFor = optimizeFor;
  }

  public double getWalkSpeed() {
    return walkSpeed;
  }

  public void setWalkSpeed(double walkSpeed) {
    this.walkSpeed = walkSpeed;
  }

  public double getWalkReluctance() {
    return walkReluctance;
  }

  public void setWalkReluctance(double walkReluctance) {
    this.walkReluctance = walkReluctance;
  }

  public double getMaxWalkingDistance() {
    return maxWalkingDistance;
  }

  public void setMaxWalkingDistance(double maxWalkingDistance) {
    this.maxWalkingDistance = maxWalkingDistance;
  }

  public double getInitialWaitReluctance() {
    return initialWaitReluctance;
  }

  public void setInitialWaitReluctance(double initialWaitReluctance) {
    this.initialWaitReluctance = initialWaitReluctance;
  }

  public double getWaitReluctance() {
    return waitReluctance;
  }

  public void setWaitReluctance(double waitReluctance) {
    this.waitReluctance = waitReluctance;
  }

  public int getMinTransferTime() {
    return minTransferTime;
  }

  public void setMinTransferTime(int minTransferTime) {
    this.minTransferTime = minTransferTime;
  }

  public int getTransferCost() {
    return transferCost;
  }

  public void setTransferCost(int transferCost) {
    this.transferCost = transferCost;
  }

  public int getMaxTransfers() {
    return maxTransfers;
  }

  public void setMaxTransfers(int maxTransfers) {
    this.maxTransfers = maxTransfers;
  }

  public long getMaxComputationTime() {
    return maxComputationTime;
  }

  public void setMaxComputationTime(long maxComputationTime) {
    this.maxComputationTime = maxComputationTime;
  }

  public long getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(long currentTime) {
    this.currentTime = currentTime;
  }

  public ItineraryBean getSelectedItinerary() {
    return selectedItinerary;
  }

  public void setSelectedItinerary(ItineraryBean selectedItinerary) {
    this.selectedItinerary = selectedItinerary;
  }
}
