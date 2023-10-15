# 2023 Project 49: Beyond Simple Tasks: Evaluating LLMs on Complex Programming Assignments
Benjamin Goh and Cameron Nathan's Part IV Engineering Project for the University of Auckland.
Supervised by Nasser Giacaman and Valerio Terragni.

This project tests GPT's ability to complete code based on the instructions of the 2023 
SOFTENG 281 Assignment 1. The project tests at what stages of student code development does
GPT most successfully complete the test cases of the assignment. It also is able to test which
temperatures provide the best completion.

## Layout
All the code is in the *LLM Testing/Code-Testing/src/main* directory. It may be easier to open
the project from the Code-Testing folder if you are trying to develop more code for the existing
codebase.

## Running the Project
Extract the repos_output.zip from here
https://drive.google.com/file/d/1vbdINgK7G7VVmpIi5q7AEvzkasjOAouF/view?usp=sharing into the LLM
testing folder (email must be part of the University of Auckland).

Run the following Maven command:

```
mvn compile exec:java "-Dexec.args= OPENAI_KEY TEMPERATURE false"
```
## Known Improvements
- The current implementation needs minor tweaks for the natural language explanations of
compilation errors to work.

## Future Work
- Test GPT on different types of assignments
- Test whether there are certain temperatures that work better for different kinds of assignments
- Test GPT's ability to generate natural language explanations of compilation errors
- Test whether those natural language explanations assist GPT in fixing compilation errors