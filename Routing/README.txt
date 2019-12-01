===========================
INSTRUCTIONS & INFORMATION
===========================

1. After extracting the solution zip file, a folder named "Routing" would be created. Now this folder can be imported into eclipse IDE by going to File -> Open Projects from File System... -> Browse this "Rounting" folder in the prompt and hit finish. The program can then be run using the "Main.java" file inside the "main" package.

2. Alternatively I have included a jar file named "run.jar" which can be run from the command prompt as well. To do that go to the routing directory and run "java -jar run.jar".

3. On start, the program asks for a user input so as to decide if the graphs needs to be stored or not. If we wish to store them the program will take additional 7-8 minutes. Otherwise we can opt to choose "Generate only" which runs faster as it does not stores the generated graphs.

3. If chosen to save the graphs as well, the program would first generate the 5 pairs of graphs and store them in a folder called "Graphs". This roughly takes 7-8 minutes to complete. Graphs can be stored so that the adjcency list can be be referred if we need to verify the results. Also it makes the program execution repeatable.

4. Then the program executes the algorithms. Totally there are 10 graphs and for each graph we execute 3 algorithms on 5 (s, t) pairs. Therefore we have 150 algorithm runs. That roughly takes another 7 minutes to complete. Together with the generation and execution step it would take 15 minutes for the program to finish. If we don't save the graphs it just takes 7 minutes.

Note: No additional libraries have been used. And routing algorithm implementations have been referred from class notes.

