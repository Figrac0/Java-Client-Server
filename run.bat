@echo off
title Запуск TicTacToe клиента и сервера

REM Запуск сервера
start "Сервер" cmd /k "cd /d %~dp0 && mvn exec:java -Dexec.mainClass=org.server.tictactoe.Server"

REM Пауза для запуска сервера
timeout /t 3 >nul

REM Запуск клиента
echo Запуск клиента...
"D:\JDK.java\jdk-17.0.12\bin\java.exe" ^
--module-path "D:\javafx-sdk-21.0.6\lib" ^
--add-modules javafx.controls,javafx.fxml ^
-jar target\tictactoe-1.0-SNAPSHOT.jar

pause
