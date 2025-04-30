@echo off
echo Compiling Java files...

if not exist "target\classes" mkdir target\classes

echo Compiling model classes...
javac -d target\classes src\main\java\com\wardrobemanager\model\*.java

echo Compiling component classes...
javac -d target\classes -cp target\classes src\main\java\com\wardrobemanager\components\*.java

echo Compiling main classes...
javac -d target\classes -cp target\classes src\main\java\com\wardrobemanager\*.java

echo Running application...
java -cp target\classes com.wardrobemanager.WardrobeManagerGUI

pause 