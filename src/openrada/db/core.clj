(ns openrada.db.core
  (:require [camel-snake-kebab.core :refer :all]
            [bitemyapp.revise.connection :refer [connect close]]
            [bitemyapp.revise.query :as r]
            [bitemyapp.revise.core :refer [run run-async]]))



(def conn (connect))


(defn inserted-id [resp]
  (-> (:response resp)
      (first)
      :generated_keys
      (first)))


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
  (-> (r/db "rada")
      (r/table-db "members")
      (r/insert (vec members))
      (run conn)))


(defn update-member [id new-data]
  (-> (r/db "rada")
      (r/table-db "members")
      (r/get id)
      (r/update new-data)
      (run conn)))

;(update-deputy "027fa5df-cbd4-4138-8b7b-77078d2e7f28" {:rada 7})


(defn get-members []
  (-> (r/db "rada")
      (r/table-db "members")
      (run conn)
      :response))


(defn get-members-from-convocation [convocation]
  (-> (r/db "rada")
      (r/table-db "members")
      (r/get-all [convocation] :convocation)
      (run conn)
      :response))




(defn get-member [id]
  (-> (r/db "rada")
      (r/table-db "members")
      (r/get id)
      (run conn)
      :response
      (first)))


;(get-deputy "044588fe-db19-470d-a056-65c0eae1220a")


(defn get-member-by-short-name [short-name]
  (-> (r/db "rada")
      (r/table-db "members")
      (r/filter (r/lambda [row]
                (r/= (r/get-field row :short_name) short-name)))
      (run conn)
      :response
      (first)))
