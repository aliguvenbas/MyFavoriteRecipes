# Stage 1: Build the application
FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Stage 2: final image
FROM openjdk:17
MAINTAINER aliguvenbas@gmail.com
WORKDIR /app
COPY --from=build /app/build/libs/MyFavoriteRecipes-*.jar myfavoriterecipes.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "myfavoriterecipes.jar"]
