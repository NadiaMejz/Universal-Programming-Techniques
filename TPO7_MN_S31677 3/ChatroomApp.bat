@echo off
REM ============================================================
REM  demo_start.bat  –  Local chat demo script (Windows)
REM  Requirements covered:
REM    • start broker locally
REM    • create necessary exchanges/topics
REM    • launch clients
REM ============================================================

REM 1) Start (or reuse) the RabbitMQ container -----------------
docker inspect -f "{{.State.Running}}" rabbit 2>NUL | find "true" >NUL
if errorlevel 1 (
    echo [INFO] Launching RabbitMQ ...
    docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
) else (
    echo [INFO] RabbitMQ container already running.
)

REM Give the broker a few seconds to become healthy
echo [INFO] Waiting for broker to accept connections ...
for /L %%i in (1,1,10) do (
    docker exec rabbit rabbitmq-diagnostics -q check_port_connectivity 2>NUL && goto :ready
    timeout /t 1 >NUL
)
echo [ERROR] Broker did not start in time. Aborting.
goto :eof
:ready

REM 2) Declare exchanges ---------------------------------------
echo [INFO] Declaring exchanges ...
docker exec rabbit rabbitmqadmin --username=guest --password=guest declare exchange name=room.mainroom type=fanout durable=true
docker exec rabbit rabbitmqadmin --username=guest --password=guest declare exchange name=dm type=direct durable=true

REM 3) Build client distribution (only first run) --------------
echo [INFO] Building client ...
call gradlew installDist -q

REM 4) Launch two sample clients -------------------------------
echo [INFO] Starting demo clients ...
start "" "%~dp0build\install\chatroom\bin\chatroom.bat" Alice mainroom
start "" "%~dp0build\install\chatroom\bin\chatroom.bat" Bob   mainroom

echo [OK] Demo environment is up.  Close the chat windows to exit.
