@echo off
echo Creating directories...
mkdir target\classes 2>nul

echo Compiling Java files...
javac -d target/classes -cp "lib/*" src/main/java/com/wardrobemanager/model/*.java
javac -d target/classes -cp "target/classes;lib/*" src/main/java/com/wardrobemanager/*.java

echo Starting Wardrobe Manager...
java -cp "target/classes;lib/*" com.wardrobemanager.WardrobeManagerGUI 