JAR_FILE=target/apiapi-1.0-SNAPSHOT-jar-with-dependencies.jar
GCC=/bin/gcc
TMP=/tmp
JAVA_HOME=/usr/lib/jvm/default
MAVEN=/usr/lib/netbeans/java/maven/bin/mvn

build:
	JAVA_HOME=$(JAVA_HOME) $(MAVEN) clean install
generate:
	java -jar $(JAR_FILE) $(SPEC) $(COMPILE_OUTPUT) $(GENERATE_OUTPUT)
