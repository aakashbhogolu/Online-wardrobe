@echo off
echo Compiling Java files...
if not exist "target\classes" mkdir target\classes
javac -d target/classes src/main/java/com/wardrobemanager/model/*.java
javac -d target/classes -cp target/classes src/main/java/com/wardrobemanager/components/*.java
javac -d target/classes -cp target/classes src/main/java/com/wardrobemanager/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Running application...
java -cp target/classes com.wardrobemanager.WardrobeManagerApp
pause 