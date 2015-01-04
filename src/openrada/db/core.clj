(ns openrada.db.core
  (:require [rethinkdb.core :refer [connect close]]
            [rethinkdb.query :as r]))

(defn make-connection [host]
  (connect :host host))

(defn close-connection [db]
  (close (:connection db)))


(defn my-db-table [tablename]
  (-> (r/db "rada")
      (r/table tablename)))


(def memberst (my-db-table "members"))


(defn save-members [db members]
  (-> memberst
      (r/insert (vec members))
      (r/run (:connection db))))



(defn update-member [db id new-data]
  (-> memberst
      (r/get id)
      (r/update new-data)
      (r/run (:connection db))))


(defn get-members-from-convocation [db convocation]
  (-> memberst
      (r/get-all [convocation] {:index "convocation"})
      (r/without [:image])
      (r/run (:connection db))))


(defn get-member [db id]
  (->  memberst
      (r/get id)
      (r/run (:connection db))))




(defn get-member-by-short-name [db short-name]
  (-> memberst
      (r/filter (r/fn [row]
                (r/eq (r/get-field row :short_name) short-name)))
      (r/run (:connection db))))

