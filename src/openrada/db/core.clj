(ns openrada.db.core
  (:require [rethinkdb.core :refer [connect close]]
            [rethinkdb.query :as r]))

(def base-url "https://api.openrada.com/v1/parliament/")

(defn add-url [ent-name ent convocation]
  (assoc ent :url (str base-url convocation "/" ent-name "/" (:id ent))))

(defn add-urls-to-all [ents ent-name convocation]
  (map #(add-url ent-name % convocation) ents))

(defn find-first
  [f coll]
  (first (filter f coll)))


(defn make-connection [host]
  (connect :host host))

(defn close-connection [db]
  (close (:connection db)))


(defn my-db-table [tablename]
  (-> (r/db "rada")
      (r/table tablename)))


(def memberst (my-db-table "members"))

(def committeest (my-db-table "committees"))

(def factionst (my-db-table "factions"))

(def registrations (my-db-table "registrations"))

(defn remove-field [db tablename field]
  (-> (my-db-table tablename)
      (r/replace (r/fn [row]
                       (r/without row [field])))
      (r/run (:connection db))))

(defn get-by-id [db tablename id]
  (-> (my-db-table tablename)
      (r/get id)
      (r/without [:image])
      (r/run (:connection db))))

; members

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
      (r/run (:connection db))
      (add-urls-to-all "members" convocation)))


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
      (r/run (:connection db))
      (add-urls-to-all "committees" convocation)))


; factions

(defn save-factions [db factions]
  (-> factionst
      (r/insert (vec factions))
      (r/run (:connection db))))


(defn get-factions-from-convocation [db convocation]
  (-> factionst
      (r/get-all [convocation] {:index "convocation"})
      (r/run (:connection db))
      (add-urls-to-all "factions" convocation)))


;; compount queries


(defn pure-member [member]
  (dissoc member :faction_id
                 :faction_role
                 :committee_id
                 :committee_role
                 :image))


(defn enhance-member [member factions committees]
  (let [faction (find-first #(= (:id %) (:faction_id member)) factions)
        committee (find-first #(= (:id %) (:committee_id member)) committees)
        m (pure-member member)
        f (assoc faction :role (:faction_role member))
        c (assoc committee :role (:committee_role member))
        f-clean (dissoc f :convocation)
        c-clean (dissoc c :convocation)]
    (assoc m :faction (if (:id faction) f-clean nil)
             :committee (if (:id committee) c-clean nil))))

(defn get-members-full
  ([db convocation]
     (let [members (get-members-from-convocation db convocation)
           factions (get-factions-from-convocation db convocation)
           committees (get-committees-from-convocation db convocation)]
       (get-members-full db convocation members factions committees)))
  ([db convocation members factions committees]
      (map #(enhance-member % factions committees) members)))


(defn get-member-full
  ([db convocation id]
     (let [members (get-members-from-convocation db convocation)
           factions (get-factions-from-convocation db convocation)
           committees (get-committees-from-convocation db convocation)]
       (get-member-full db convocation id factions committees)))
  ([db convocation id factions committees]
    (let [member (get-by-id db "members" id)]
      (enhance-member member factions committees))))

(defn enhance-faction [faction members]
  (let [members (filter #(= (:id faction) (:faction_id %)) members)
        ms (map #(assoc % :role (:faction_role %)) members)]
    (assoc faction :members (map pure-member ms))))


(defn get-factions-full
  ([db convocation]
     (let [members (get-members-from-convocation db convocation)
           factions (get-factions-from-convocation db convocation)]
       (get-factions-full db convocation members factions)))
  ([db convocation members factions]
      (map #(enhance-faction % members) factions)))


(defn get-faction-full
  ([db convocation id]
     (let [members (get-members-from-convocation db convocation)
           factions (get-factions-from-convocation db convocation)]
       (get-faction-full db convocation id members)))
  ([db convocation id members]
    (let [faction (get-by-id db "factions" id)]
      (enhance-faction faction members))))


(defn enhance-committee [committee members]
  (let [members (filter #(= (:id committee) (:committee_id %)) members)
        ms (map #(assoc % :role (:committee_role %)) members)]
    (assoc committee :members (map pure-member ms))))


(defn get-committees-full
  ([db convocation]
     (let [members (get-members-from-convocation db convocation)
           committees (get-committees-from-convocation db convocation)]
       (get-committees-full db convocation members committees)))
  ([db convocation members committees]
      (map #(enhance-committee % members) committees)))


(defn get-committee-full
  ([db convocation id]
     (let [members (get-members-from-convocation db convocation)
           committees (get-committees-from-convocation db convocation)]
       (get-committees-full db convocation id members)))
  ([db convocation id members]
     (let [committee (get-by-id db "committees" id)]
       (enhance-committee committee members))))

;(def db {:connection (make-connection "127.0.01")})
;(get-factions-from-convocation db 8)
;(get-members-full db 8)
;(get-factions-full db 8)
;(get-committees-full db 8)
