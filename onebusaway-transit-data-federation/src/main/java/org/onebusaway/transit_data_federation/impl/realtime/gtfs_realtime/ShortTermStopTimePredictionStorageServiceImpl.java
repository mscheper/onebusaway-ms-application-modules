package org.onebusaway.transit_data_federation.impl.realtime.gtfs_realtime;

import org.onebusaway.collections.tuple.Pair;
import org.onebusaway.collections.tuple.Tuples;
import org.onebusaway.transit_data.services.ShortTermStopTimePredictionStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * A memory-based short-term store for stop time predictions.
 * 
 * @author <a href="mailto:git@michaelscheper.com">Michael Scheper</a>,
 *         <a href="mailto:Michael.Scheper@rms.nsw.gov.au">New South Wales Roads &amp; Maritime Service</a>
 */
@Component
public class ShortTermStopTimePredictionStorageServiceImpl implements
    ShortTermStopTimePredictionStorageService {

  private static final Logger _log = LoggerFactory.getLogger(
      ShortTermStopTimePredictionStorageServiceImpl.class);

  private int _maximumPredictionAgeSeconds
      = ShortTermStopTimePredictionStorageService.DEFAULT_MAXIMUM_PREDICTION_AGE_SECONDS;

  private int _bucketSizeSeconds
      = ShortTermStopTimePredictionStorageService.DEFAULT_BUCKET_SIZE_SECONDS;

  /**
   * The predictions map. The keys are Pair&lt;trip,stop&gt;.
   */
  private Map<Pair<String>, Prediction> _predictions
      = new HashMap<Pair<String>, Prediction>();

  /**
   * A map of expiry time intervals to lists of prediction keys. For each entry,
   * the key is the bucket timestamp, and the value is a set of keys to the
   * {@link _predictions} map. The bucket timestamp is the most recent timestamp
   * possible for any entry in the _predictions map whose &lt;trip,stop&gt; key
   * is in the bucket.
   */
  private SortedMap<Long, Set<Pair<String>>> _expiryBuckets
      = new TreeMap<Long, Set<Pair<String>>>();

  private Timer _expiredPredictionCleanUpTimer;

  public ShortTermStopTimePredictionStorageServiceImpl() {
    constructExpiredPredictionCleanUpTimer();
  }

  @Override
  public Pair<Long> getPrediction(String trip, String stop) {
    Pair<String> tripIdAndStopId = Tuples.pair(trip, stop);
    Prediction prediction = _predictions.get(tripIdAndStopId);
    if (prediction == null) {
      return null;
    }
    if (isPredictionExpired(prediction.predictionTimestampSeconds)) {
      _log.info(String.format("Not returning expired prediction %s for %s",
          prediction, tripIdAndStopId));
      return null;
    }
    return Tuples.pair(prediction.arrivalTimeSeconds,
        prediction.departureTimeSeconds);
  }

  @Override
  public void putPrediction(String trip, String stop,
      Long arrivalTimeSeconds, Long departureTimeSeconds,
      long predictionTimestampSeconds) {

    Pair<String> tripIdAndStopId = Tuples.pair(trip, stop);

    Prediction prediction = new Prediction(arrivalTimeSeconds,
        departureTimeSeconds, predictionTimestampSeconds);

    if (isPredictionExpired(predictionTimestampSeconds)) {
      _log.warn(String.format("Prediction %s for %s has already expired",
          prediction, tripIdAndStopId));
      return;
    }

    synchronized (_predictions) {
      removeFromExpiryBucket(tripIdAndStopId);
      _predictions.put(tripIdAndStopId, prediction);
      putInExpiryBucket(tripIdAndStopId, predictionTimestampSeconds);
    }
  }

  @Override
  public void setMaximumPredictionAgeSeconds(int maximumPredictionAgeSeconds) {
    _maximumPredictionAgeSeconds = maximumPredictionAgeSeconds;
  }

  @Override
  public void setBucketSizeSeconds(int bucketSizeSeconds) {
    _bucketSizeSeconds = bucketSizeSeconds;
    constructExpiredPredictionCleanUpTimer();
  }

  /**
   * Simple class for storing prediciton data. All times are expressed in
   * <em>seconds</em> since the epoch. Arrival and departure times may be
   * <code>null</code>, such as for the departure time at the last stop of a
   * trip.
   */
  private class Prediction {

    Prediction(Long arrivalTimeSeconds, Long departureTimeSeconds,
        long predictionTimestampSeconds) {
      this.arrivalTimeSeconds = arrivalTimeSeconds;
      this.departureTimeSeconds = departureTimeSeconds;
      this.predictionTimestampSeconds = predictionTimestampSeconds;
    }

    /** The arrival time. */
    Long arrivalTimeSeconds;

    /** The departure time. */
    Long departureTimeSeconds;

    /** The time that the prediction was made. */
    long predictionTimestampSeconds;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result
          + ((arrivalTimeSeconds == null) ? 0 : arrivalTimeSeconds.hashCode());
      result = prime
          * result
          + ((departureTimeSeconds == null) ? 0
              : departureTimeSeconds.hashCode());
      result = prime * result
          + (int) (predictionTimestampSeconds
          ^ (predictionTimestampSeconds >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Prediction other = (Prediction) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (arrivalTimeSeconds == null) {
        if (other.arrivalTimeSeconds != null)
          return false;
      } else if (!arrivalTimeSeconds.equals(other.arrivalTimeSeconds))
        return false;
      if (departureTimeSeconds == null) {
        if (other.departureTimeSeconds != null)
          return false;
      } else if (!departureTimeSeconds.equals(other.departureTimeSeconds))
        return false;
      if (predictionTimestampSeconds != other.predictionTimestampSeconds)
        return false;
      return true;
    }

    private ShortTermStopTimePredictionStorageServiceImpl getOuterType() {
      return ShortTermStopTimePredictionStorageServiceImpl.this;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Prediction{ arrivalTimeSeconds=");
      builder.append(debugTimeFormat(arrivalTimeSeconds));
      builder.append("; departureTimeSeconds=");
      builder.append(debugTimeFormat(departureTimeSeconds));
      builder.append("; predictionTimestampSeconds=");
      builder.append(debugTimeFormat(predictionTimestampSeconds));
      builder.append(" }");
      return builder.toString();
    }
  }

  /**
   * Yep, a TimerTask that cleans up expired predictions.
   */
  private class ExpiredPredictionCleanUpTask extends TimerTask {
    @Override
    public void run() {
      removeExpiredPredictions();
    }
  }

  private void constructExpiredPredictionCleanUpTimer() {
    if (_expiredPredictionCleanUpTimer != null) {
      _expiredPredictionCleanUpTimer.cancel();
    }
    _expiredPredictionCleanUpTimer
        = new Timer("Expired Prediction Clean-Up Task", true);
    _expiredPredictionCleanUpTimer.schedule(new ExpiredPredictionCleanUpTask(),
        _bucketSizeSeconds * 1000, _bucketSizeSeconds * 1000);
  }

  /**
   * Calculates the bucket timestamp for a prediction timestamp. The bucket
   * timestamp is the most recent timestamp that any prediction it contains
   * could have. Both timestamps are expressed in seconds since the epoch.
   *
   * @param predictionTimestampSeconds the prediction timestamp
   * @return the bucket timestamp
   */
  private long getBucketTimestamp(long predictionTimestampSeconds) {
    long key = predictionTimestampSeconds / _bucketSizeSeconds;
    key *= _bucketSizeSeconds;
    key += _bucketSizeSeconds;
    key -= 1;
    return key;
  }

  /**
   * Adds a prediction to a correctly timestamped expiry bucket, creating it if
   * necessary. The calling method must synchronise on {@link #_predictions}
   * while calling this method.
   */
  private void putInExpiryBucket(Pair<String> tripIdAndStopId,
      long predictionTimestampSeconds) {
    long bucketTimestamp = getBucketTimestamp(predictionTimestampSeconds);
    Set<Pair<String>> bucket = _expiryBuckets.get(bucketTimestamp);
    if (bucket == null) {
      _log.debug(String.format("Created bucket %s",
          debugTimeFormat(bucketTimestamp)));
      bucket = new HashSet<Pair<String>>();
      _expiryBuckets.put(bucketTimestamp, bucket);
    }
    bucket.add(tripIdAndStopId);
  }

  /**
   * Removes a prediction from its expiry bucket, if such a prediction exists.
   * The calling method must synchronise on {@link #_predictions} while calling
   * this method.
   */
  private void removeFromExpiryBucket(Pair<String> tripIdAndStopId) {
    Prediction oldPrediction = _predictions.get(tripIdAndStopId);
    if (oldPrediction == null) {
      return;
    }
    long bucketTimestamp = getBucketTimestamp(
        oldPrediction.predictionTimestampSeconds);
    Set<Pair<String>> bucket = _expiryBuckets.get(bucketTimestamp);
    if (bucket == null) {
      _log.error(String.format(
        "No bucket %s found for old prediction %s for %s",
        debugTimeFormat(bucketTimestamp), oldPrediction, tripIdAndStopId));
      return;
    }
    if (!bucket.remove(tripIdAndStopId)) {
      _log.error(String.format(
        "Bucket %s did not contain old prediction %s for %s",
        debugTimeFormat(bucketTimestamp), oldPrediction, tripIdAndStopId));
      return;
    }
    if (bucket.isEmpty()) {
      _log.debug(String.format("Removing empty bucket %s.",
          debugTimeFormat(bucketTimestamp)));
      _expiryBuckets.remove(bucketTimestamp);
    }
  }

  private void removeExpiredPredictions() {
    synchronized (_predictions) {
      for (Iterator<Map.Entry<Long, Set<Pair<String>>>> bucketIterator
          = _expiryBuckets.entrySet().iterator(); bucketIterator.hasNext(); ) {

        Map.Entry<Long, Set<Pair<String>>> bucketEntry = bucketIterator.next();

        Long bucketTimestamp = bucketEntry.getKey();
        if (!isPredictionExpired(bucketTimestamp)) {
          _log.debug(String.format(
              "All expired predictions have been removed. Next "
              + "bucketTimestamp is %s. Bucket count is %d.",
              debugTimeFormat(bucketTimestamp), _expiryBuckets.size()));
          // We're up to date!
          return;
        }

        Set<Pair<String>> bucket = bucketEntry.getValue();

        _log.debug(String.format("Removing %d expired predictions for %s:",
            bucket.size(), debugTimeFormat(bucketTimestamp)));

        for (Iterator<Pair<String>> bucketContentIterator = bucket.iterator();
            bucketContentIterator.hasNext();) {

          Pair<String> tripIdAndStopId = bucketContentIterator.next();
          Prediction prediction = _predictions.get(tripIdAndStopId);

          if (prediction == null) {
            _log.error(String.format(
              "Expected prediction for %s, found in bucket %s, was not found "
              + "in the predictions map", tripIdAndStopId,
              debugTimeFormat(bucketTimestamp)));
            continue;
          }
          if (!isPredictionExpired(prediction.predictionTimestampSeconds)) {
            _log.error(String.format(
              "Prediction %s for %s was in bucket %s but has not expired",
              prediction, tripIdAndStopId, debugTimeFormat(bucketTimestamp)));
            continue;
          }

          _predictions.remove(tripIdAndStopId);
          bucketContentIterator.remove();
        }

        if (!bucket.isEmpty()) {
          _log.error(String.format("Bucket %s isn't empty!",
              debugTimeFormat(bucketTimestamp)));
        }
        bucketIterator.remove();
      }
    }
  }

  private boolean isPredictionExpired(long predictionTimestampSeconds) {
    return System.currentTimeMillis() - predictionTimestampSeconds * 1000
        > _maximumPredictionAgeSeconds * 1000;
  }

  private static String debugTimeFormat(Long timeSeconds) {
    return String.format("%d (%tc)", timeSeconds, timeSeconds == null ? null
        : timeSeconds * 1000);
  }
}
