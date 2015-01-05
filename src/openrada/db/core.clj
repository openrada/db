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

(def committeest (my-db-table "committees"))

(defn remove-field [db tablename field]
  (-> (my-db-table tablename)
      (r/replace (r/fn [row]
                       (r/without row [field])))
      (r/run (:connection db))))


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

(defn update-member-by-full-name [db full-name new-data]
  (-> memberst
      (r/filter (r/fn [row]
                (r/eq (r/get-field row :full_name) full-name)))
      (r/update new-data)
      (r/run (:connection db))))


; committees


(defn save-committees [db committees]
  (-> committeest
      (r/insert (vec committees))
      (r/run (:connection db))))


(defn get-committees-from-convocation [db convocation]
  (-> committeest
      (r/get-all [convocation] {:index "convocation"})
      (r/run (:connection db))))


