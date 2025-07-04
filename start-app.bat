@echo off
echo Starting Bike Taxi App in PRODUCTION mode...
echo.

REM Set JVM memory parameters for production
set JAVA_OPTS=-Xmx4g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError

echo Production JVM Memory Settings:
echo -Xmx4g (Max heap: 4GB)
echo -Xms2g (Initial heap: 2GB)
echo -XX:+UseG1GC (G1 Garbage Collector)
echo -XX:MaxGCPauseMillis=200 (Max GC pause: 200ms)
echo -XX:+HeapDumpOnOutOfMemoryError (Heap dump on OOM)
echo.

echo 🔒 AUTHENTICATION ENABLED
echo All protected endpoints require valid JWT tokens
echo.

REM Start the application
mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

pause 