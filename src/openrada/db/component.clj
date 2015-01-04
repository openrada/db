(ns openrada.db.component
  (:require [com.stuartsierra.component :as component]
            [openrada.db.core :as db]))


(defrecord Database [host connection]
  component/Lifecycle

  (start [component]
    (println ";; Starting database")
    (let [conn (db/make-connection host)]
      ;; Return an updated version of the component with
      ;; the run-time state assoc'd in.
      (assoc component :connection conn)))

  (stop [component]
    (println ";; Stopping database")
    ((db/close-connection connection)
    ;; Return the component, optionally modified. Remember that if you
    ;; dissoc one of a record's base fields, you get a plain map.
    (assoc component :connection nil)))


(defn new-database
  [host]
  (map->Database {:host host}))
