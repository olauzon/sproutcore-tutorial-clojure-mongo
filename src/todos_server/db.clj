(ns todos-server.db
 (:use somnium.congomongo))

(mongo! :db "clojure-test-todos")

(defn find-all-tasks []
  (fetch :tasks))

(defn find-task [id]
  (fetch-one :tasks :where {:_id id}))

(defn- uuid []
  (str (java.util.UUID/randomUUID)))

(defn add-task [task]
  (insert! :tasks (assoc task :_id (uuid))))

(defn keywordify-keys
  "Returns a map otherwise same as the argument but
   with all keys turned to keywords"
  [m]
  (zipmap
    (map keyword (keys m))
    (vals m)))

(defn merge-with-kw-keys
  "Merges maps converting all keys to keywords"
  [& maps]
  (reduce
    merge
    (map keywordify-keys maps)))

(defn update-task [id task]
  (let [task-in-db (find-task id)]
    (update! :tasks
      task-in-db
      (merge-with-kw-keys task-in-db task))))

(defn destroy-task [id]
  (destroy! :tasks
    (find-task id)))
