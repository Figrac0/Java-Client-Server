@echo off
echo Запуск клиента...
D:\JDK.java\jdk-17.0.12\bin\java.exe ^
--module-path D:\javafx-sdk-21.0.6\lib ^
--add-modules javafx.controls,javafx.fxml ^
-cp target\classes ^
org.example.tictactoe.ClientApplication
pause
