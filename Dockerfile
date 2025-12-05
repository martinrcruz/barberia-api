# Etapa de construcción
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Configurar Maven para mejor manejo de red, timeouts y reintentos
ENV MAVEN_OPTS="-Dmaven.wagon.http.retryHandler.count=5 \
                -Dmaven.wagon.httpconnectionManager.ttlSeconds=25 \
                -Dmaven.wagon.http.readTimeout=120000 \
                -Dmaven.wagon.http.connectTimeout=60000"

# Copiar archivos de configuración de Maven
COPY pom.xml .
# Intentar descargar dependencias con reintentos
RUN mvn dependency:go-offline || \
    (sleep 5 && mvn dependency:resolve) || \
    (sleep 10 && mvn dependency:resolve -U)

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -U

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

