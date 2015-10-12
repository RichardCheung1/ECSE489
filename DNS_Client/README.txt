To compile: 
javac -d . Packet.java
javac -d . Request.java
javac -d . Client.java

To run:

java DNS.Client -t 1 -r 2 -p 3 @132.206.85.18 www.mcgill.ca
java DNS.Client @132.206.85.18 www.mcgill.ca
