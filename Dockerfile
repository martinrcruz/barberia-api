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

# Crear usuario no-root para seguridad (requerido por Digital Ocean)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto (Digital Ocean usa variable PORT dinámica)
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

# Comando de inicio con soporte para PORT de Digital Ocean
# Digital Ocean App Platform proporciona la variable PORT
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-$SERVER_PORT} -jar /app/app.jar"]

