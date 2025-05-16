
FROM maven:3.8.5-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Tomcat (avec port 80)
FROM tomcat:9.0

# Changer le port HTTP de Tomcat à 80
RUN sed -i 's/Connector port="8080"/Connector port="80"/' /usr/local/tomcat/conf/server.xml

# Déploiement du WAR
COPY --from=builder /app/target/CINEPROJ.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 80

CMD ["catalina.sh", "run"]