(ns openrada.db.core
  (:require [rethinkdb.core :refer [connect close]]
            [rethinkdb.query :as r]))


; conf should have :host and :port keys
(defn make-connection [conf]
  (connect :host (:host conf)))


(defn my-db-table [tablename]
  (-> (r/db "rada")
      (r/table tablename)))


(def memberst (my-db-table "members"))


(defn save-members [conn members]
  (-> memberst
      (r/insert (vec members))
      (r/run conn)))



(defn update-member [conn id new-data]
  (-> memberst
      (r/get id)
      (r/update new-data)
      (r/run conn)))

;(update-member "048a84fc-1a58-45d6-9077-78e04af24447" {:convocation  7})



(defn get-members-from-convocation [conn convocation]
  (-> memberst
      (r/get-all [convocation] {:index "convocation"})
      (r/without [:image])
      (r/run conn)))


;(get-members-from-convocation 8)


(defn get-member [conn id]
  (->  memberst
      (r/get id)
      (r/run conn)))


;(get-member "c04517b8-39b4-4814-ab2b-89bb1b85e39f")


(defn get-member-by-short-name [conn short-name]
  (-> memberst
      (r/filter (r/fn [row]
                (r/eq (r/get-field row :short_name) short-name)))
      (r/run conn)))

