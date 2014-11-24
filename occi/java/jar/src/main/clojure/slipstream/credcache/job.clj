(ns slipstream.credcache.job
  "Functions for managing the jobs for credential renewal."
  (:require
    [clojure.tools.logging :as log]
    [clojurewerkz.quartzite.scheduler :as qs]
    [clojurewerkz.quartzite.conversion :as qc]
    [clojurewerkz.quartzite.triggers :as t]
    [clojurewerkz.quartzite.jobs :as j]
    [clojurewerkz.quartzite.schedule.simple :as ss :refer [schedule with-repeat-count with-interval-in-milliseconds]]
    [clojurewerkz.quartzite.jobs :refer [defjob]]
    [clj-time.coerce :as tc]
    [slipstream.credcache.db-utils :as db]
    [slipstream.credcache.renewal :as r]
    [slipstream.credcache.notify :as notify]))

(def renewal-factor 2/3)                                    ;; renew 2/3 of way through validity period
(def renewal-threshold (* 5 60))                            ;; 5 minutes in seconds!
(def clean-interval 2)                                      ;; 2 hours

(defn attempt-renewal
  "Attempts to renew and update the given credential.  Returns nil if the
   credential could not be found.  Returns the original or updated resource,
   depending on whether the renewal was successful or not."
  [id]
  (log/info "renewing credential:" id)
  (if-let [resource (db/retrieve-resource id)]
    (do
      (if-let [updated-resource (r/renew resource)]
        (do
          (db/update-resource updated-resource)
          (log/info "credential renewed:" id)
          updated-resource)
        (do
          (log/warn "credential not renewed:" id)
          (notify/renewal-failure resource)
          resource)))
    (do
      (log/warn "credential information not found:" id)
      nil)))

(defn expired?
  [{:keys [expiry]}]
  (if expiry
    (->> (quot (System/currentTimeMillis) 1000)
         (- expiry)
         (max 0)
         (zero?))))

(defn renewal-datetime
  "Returns the next renewal time for the given expiry date, applying
   the renewal wait time and threshold.  NOTE: The expiry
   time is in seconds, not milliseconds!"
  [expiry]
  (if expiry
    (let [now (quot (System/currentTimeMillis) 1000)
          delta (->> (- expiry now)
                     (max 0)
                     (* renewal-factor)
                     (int))]
      (if (>= delta renewal-threshold)
        (-> delta
            (+ now)
            (* 1000)
            (tc/from-long))))))

(defn delete-expired-credentials
  "Delete expired credentials from cache."
  [resource-type]
  (doall
    (->> (db/all-resource-ids resource-type)
         (map db/retrieve-resource)
         (remove nil?)
         (filter expired?)
         (map :id)
         (map db/delete-resource))))

(declare schedule-renewal)

(defjob renew-cred-job
        [ctx]
        (let [id (-> ctx
                     (qc/from-job-data)
                     (get "id"))]
          (log/info "start credential renewal:" id)
          (->> id
               (attempt-renewal)
               (schedule-renewal))
          (log/info "finished credential renewal: " id)))

(defn schedule-renewal
  "Takes a resource (credential) as input and schedules a renewal job
   for that resource.  If the resource has no :expiry entry, then no
   renewal job is scheduled.  This function returns the original resource
   in all cases."
  [{:keys [id expiry] :as resource}]
  (if-let [start (renewal-datetime expiry)]
    (let [job-key (j/key (str "renewal." id))
          trigger-key (t/key (str "trigger." id))]
      (qs/delete-job job-key)
      (let [job (j/build
                  (j/with-identity job-key)
                  (j/of-type renew-cred-job)
                  (j/using-job-data {"id" id}))
            trigger (t/build
                      (t/with-identity trigger-key)
                      (t/start-at start))]
        (qs/schedule job trigger)
        (log/info "scheduled next renewal:" id start)))
    (log/info "renewal NOT scheduled:" id))
  resource)

(defjob cleanup-cache
        [ctx]
        (let [resource-type (-> ctx
                                (qc/from-job-data)
                                (get "resource-type"))]
          (log/info "start cleanup:" resource-type)
          (delete-expired-credentials resource-type)
          (log/info "finished cleanup: " resource-type)))

(defn schedule-cache-cleanup
  "Defines the job for cleaning up the credential cache."
  [resource-type]
  (let [job-key (j/key (str "cleanup." resource-type))
        trigger-key (t/key (str "trigger." resource-type))]
    (qs/delete-job job-key)
    (let [job (j/build
                (j/with-identity job-key)
                (j/of-type cleanup-cache)
                (j/using-job-data {"resource-type" resource-type}))
          trigger (t/build
                    (t/with-identity trigger-key)
                    (t/start-now)
                    (t/with-schedule (schedule
                                       (ss/repeat-forever)
                                       (ss/with-interval-in-hours clean-interval)
                                       (ss/with-misfire-handling-instruction-now-with-remaining-count))))]
      (qs/schedule job trigger)
      (log/info "scheduled cache cleanup job"))))
