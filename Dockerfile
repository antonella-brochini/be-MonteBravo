FROM eclipse-temurin:21.0.3_9-jdk

# Copiamos todo el proyecto
WORKDIR /app
COPY . /app

# Instalamos Maven si no está incluido
RUN apt-get update && apt-get install -y maven

# Build del proyecto
RUN mvn clean package -DskipTests

# Ejecutamos el jar
ENTRYPOINT ["java", "-jar", "target/be-0.0.1-SNAPSHOT.jar"]
