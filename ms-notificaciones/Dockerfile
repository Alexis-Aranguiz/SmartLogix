# =========================
# Etapa 1: COMPILACIÓN
# =========================
# Imagen con Maven 3.9.9 + Java 17 para compilar el proyecto
# 'AS build' le da un nombre a esta etapa para referenciarla después
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Define /app como carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiar SOLO el pom.xml primero
# Truco: si el pom no cambia, Docker reutiliza el cache de las dependencias
# aunque el código fuente haya cambiado → builds mucho más rápidos
COPY pom.xml .

# Descargar TODAS las dependencias por adelantado y guardarlas en cache
RUN mvn dependency:go-offline

# Ahora sí copiar el código fuente
# Al estar después del paso anterior, cambios en .java no reinician la descarga
COPY src ./src

# Compilar y empaquetar → genera el archivo .jar en /app/target/
# -DskipTests: omitir tests para agilizar el build en Docker
RUN mvn clean package -DskipTests

# =========================
# Etapa 2: EJECUCIÓN
# =========================
# Imagen LIVIANA con solo el JRE (Java Runtime Environment)
# Alpine es ultra ligera (~40MB vs 200MB de una imagen completa)
# NO incluye Maven ni compilador → imagen final más segura y pequeña
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# CLAVE del Multi-Stage Build:
# Copia SOLO el .jar desde la etapa 'build'
# La imagen final NO contiene Maven, pom.xml, ni código fuente
COPY --from=build /app/target/*.jar app.jar

# Documenta que la app escucha en el puerto 8080 internamente
# (se mapea al puerto externo en docker-compose)
EXPOSE 8080

# Comando que se ejecuta al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
