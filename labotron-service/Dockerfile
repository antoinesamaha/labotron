FROM tomcat:9.0-jdk17

# Remove default Tomcat apps (optional)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your WAR file to Tomcat
COPY target/labotronservice-*.war /usr/local/tomcat/webapps/ROOT.war

# Optional: Configure Tomcat
ENV CATALINA_OPTS="-Xms512m -Xmx4096m -Dspring.profiles.active=prod"

EXPOSE 8080

CMD ["catalina.sh", "run"]
