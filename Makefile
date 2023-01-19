public:
	javac libs/*/*.java
	javac main.java && java main
clean:
	rm libs/*/*.class
