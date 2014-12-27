(ns openrada.db.core
  (:require [camel-snake-kebab.core :refer :all]
            [rethinkdb.core :refer [connect close]]
            [rethinkdb.query :as r]))



(def conn (connect :host "127.0.0.1" :port 28015))


(defn inserted-id [resp]
  (-> (:response resp)
      (first)
      :generated_keys
      (first)))

(defn get-first [resp]
  (->
   (:response resp)
   (first)))

(defn my-db-table [tablename]
  (-> (r/db "rada")
      (r/table tablename)))




(def memberst (my-db-table "members"))

;(-> (r/db-create "rada") (run conn))

;(-> (r/db "rada") (r/table-create-db "members") (run conn))

;(-> (r/db "rada") (r/table-db "members")
;    (r/index-create :short_name
;                    (r/lambda [deputy]
;                      (r/get-field deputy :short_name))))

; (-> (r/db "rada") (r/table-create-db "votes") (run conn))
; (-> (r/db "rada") (r/table-create-db "bills") (run conn))



;(-> (r/db "rada")
;    (r/table-db "members")
;    (r/index-create :rada
;                    (r/lambda [deputy]
;                      (r/get-field deputy :rada))))

(defn save-members [members]
  (-> memberst
      (r/insert (vec members))
      (r/run conn)))



(defn update-member [id new-data]
  (-> memberst
      (r/get id)
      (r/update new-data)
      (r/run conn)))

;(update-deputy "027fa5df-cbd4-4138-8b7b-77078d2e7f28" {:rada 7})



(defn get-members-from-convocation [convocation]
  (-> memberst
      (r/get-all [convocation] {:index "convocation"})
      (r/run conn)))


;(get-members-from-convocation 8)


(defn get-member [id]
  (->  memberst
      (r/get id)
      (r/run conn)))


;(get-member "c04517b8-39b4-4814-ab2b-89bb1b85e39f")


(defn get-member-by-short-name [short-name]
  (-> memberst
      (r/filter (r/fn [row]
                (r/eq (r/get-field row :short_name) short-name)))
      (r/run conn)))

