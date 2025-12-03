# Java Zookeeper Leader Election

Description
- Simple Java/Maven example demonstrating leader election using Apache ZooKeeper.
- Main source file: `src/main/java/LeaderElection.java`

Requirements
- Java 11+ installed.
- Maven installed (or use the Maven Wrapper if included).
- Apache ZooKeeper server running and reachable.

Setup
1. Update the ZooKeeper server address in `src/main/java/LeaderElection.java`:
   - Change the `address` constant to match your `host:port` (e.g. `192.168.43.10:2181`).

Build
- From PowerShell or Command Prompt in the project directory:
  - `mvn clean package`

Run
- Run via Maven (convenient during development):
  - `mvn compile exec:java -Dexec.mainClass="LeaderElection"`
- Or after building the JAR:
  - If using classpath: `java -cp target\your-artifact-id-1.0-SNAPSHOT.jar;target\dependency\* LeaderElection`
  - If the JAR is executable: `java -jar target\your-artifact-id-1.0-SNAPSHOT.jar`

Run multiple instances
- Open multiple terminal windows and run the application in each to simulate multiple nodes competing for leadership. Each instance creates an ephemeral sequential znode and competes to be leader.

Expected behavior
- Each instance prints the full path of the znode it created.
- The instance with the smallest znode name becomes leader and prints: `I'm a leader`.
- Other instances print: `I'm not a leader <leader-node> is a leader.` and watch their predecessor node so they can try again when it is deleted.

Troubleshooting
- Ensure ZooKeeper is running on the configured host and port.
- If connection exceptions occur, check firewall and network access.
- ZooKeeper server logs are useful to diagnose connection or session issues.

License
- Add a `LICENSE` file if you need to declare a license for this project.

Main source file
- `src/main/java/LeaderElection.java`
