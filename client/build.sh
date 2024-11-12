# remove class
rm -f *.class
# remove jar
rm -f *.jar
# java compiler class
javac Metric.java
# build jar
jar cvf metric.jar *.class
# run
java -cp ".:.jar:libs/*:.jar:*" Metric