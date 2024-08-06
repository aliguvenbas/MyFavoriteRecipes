- The application will run in two different containers. One of is for the api application the other one is for MySql database.
- The api is scalable ans stateless
- I used docker-compose for run these containers. Check the command below. You should use it in order to run the application
      * docker-compose down && docker-compose build --no-cache && docker-compose up
      * it would be nice to check is containers are up and running
      -> docker ps
                  CONTAINER ID   IMAGE                                     COMMAND                  CREATED         STATUS         PORTS                               NAMES
                  ***            myfavoriterecipes-myfavoriterecipes-api   "java -jar myfavorit…"   2 minutes ago   Up 2 minutes   0.0.0.0:8080->8080/tcp              myfavoriterecipes-api
                  ***            mysql                                     "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:3306->3306/tcp, 33060/tcp   mysqldb
- You may enter to the database container by using this command
      -> docker exec -it mysqldb mysql -u root -p
      -> password equals to MYSQL_ROOT_PASSWORD
- After you run the application, you can check the api documentation by usign command below
      -> http://localhost:8080/swagger-ui/index.html#
- getAll and search endpoints will return paginated results
- By default, recipe name and the instructions are mandatory for a recipe
- I use filter object during the search function instead of queery parameters. This approach provides clean URLs and is easy to extend with additional parameters in the future.
  Additionally, it avoids potential issues with URL length limits and makes the API design cleaner
- All the test queries added to the project.myfavoriterecipes.postman_collection.json. It can be directly imported to Postman
- Drawbacks
      * Amount of test can be increased. Currently the code coverage is for classes 100%, for methods 96% and for 89% for lines
      * during build of the containers, I skipped the tests, there was an error about the integration TestContainers
      * Security layer should be added
      * more logs can be added

