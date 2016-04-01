#Steps to build and run the Server Code:

1. git clone https://github.com/Arjun-Code-Knight/csc510-se-project.git
2. use folder simpl-shot-server 
3. Run mvn clean compile
4. mvn package
5. Go into the target folder and pick the "simpl-shot-server-jar-with-dependencies.jar"
6. The server can be started using the command - `java -cp  simpl-shot-server-jar-with-dependencies.jar com.simplshot.server.AppStart`
7. Make Sure to place Tessdata in the directory specificed in the simp-shot-properties
8. To get this working in linux change directories to linux variants
