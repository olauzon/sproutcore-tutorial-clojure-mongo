(ns todos-server.api
  (:use clojure.contrib.json
        clojure.contrib.duck-streams
        compojure.core
        todos-server.db))

(defn- emit-json
  "Turn the object to JSON"
  [x]
  {:headers {"Content-Type" "application/json"}
   :body    (json-str {:content x})})

(defn- parse-json
  "Parse the request body into a Clojure data structure"
  [body]
  (read-json (slurp* body)))

(defn task-path
  "Returns the relative URL for task"
  [task]
  (str "/tasks/" (:_id task)))

(defn with-guid
  "Associates task with :guid pointing to its relative URL"
  [task]
  (assoc task :guid (task-path task)))

(defroutes main-routes

  (GET    "/tasks" []
    (emit-json
      (map with-guid (find-all-tasks))))

  (GET    "/tasks/:id" [id]
    (emit-json
      (with-guid (find-task id))))

  (POST   "/tasks" {body :body}
    (let [saved-task (add-task (parse-json body))]
      {:status 201
       :headers {"Location" (task-path saved-task)}}))

  (PUT    "/tasks/:id" {body :body {id "id"} :route-params}
    (update-task id (parse-json body)))

  (DELETE "/tasks/:id" [id]
    (destroy-task id)
    {:status 200}))
