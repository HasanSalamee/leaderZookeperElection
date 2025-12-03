# Java Zookeeper Leader Election 👑

## 📋 Overview

A robust leader election implementation using Apache ZooKeeper, demonstrating distributed coordination patterns. This system simulates multiple nodes competing for leadership through ephemeral sequential znodes, providing fault-tolerant distributed system coordination.

## 🎯 Features

- ✅ **Distributed Leader Election** using ZooKeeper ensemble
- ✅ **Ephemeral Sequential Znodes** for automatic node cleanup
- ✅ **Watch Mechanism** for real-time leadership change detection
- ✅ **Fault Tolerance** with automatic re-election on leader failure
- ✅ **Multiple Instance Support** for simulating distributed nodes
- ✅ **Thread-safe ZooKeeper Client** with connection recovery
- ✅ **Comprehensive Logging** for debugging distributed scenarios
- ✅ **Graceful Shutdown** with proper resource cleanup

## 🏗️ Project Structure

```
zookeeper-leader-election/
├── src/main/java/
│   └── LeaderElection.java          # Main leader election implementation
├── src/main/resources/              # Configuration files (optional)
├── pom.xml                          # Maven build configuration
├── README.md                        # Project documentation
└── LICENSE                          # Project license file
```

## 📁 Class Description

### `LeaderElection.java`
- **Main Class**: Handles ZooKeeper connection and leader election logic
- **Core Responsibilities**:
  - Establishes connection to ZooKeeper ensemble
  - Creates ephemeral sequential znodes for election
  - Implements watch callback for leadership changes
  - Manages session expiration and reconnection
  - Provides election status monitoring

## 🚀 Getting Started

### Prerequisites
- **Java JDK 11+**
- **Apache Maven 3.6+** or Maven Wrapper
- **Apache ZooKeeper 3.5+** server running

### Installation & Execution

1. **Clone or download the project**:
```bash
git clone https://github.com/HasanSalamee/Java-Zookeeper-Leader-Election.git
cd Java-Zookeeper-Leader-Election
```

2. **Configure ZooKeeper connection**:
   - Edit `src/main/java/LeaderElection.java`
   - Update the `ZOOKEEPER_ADDRESS` constant:
```java
private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
```

3. **Build the project**:
```bash
mvn clean package
```

4. **Run a single instance**:
```bash
mvn compile exec:java -Dexec.mainClass="LeaderElection"
```

5. **Run multiple instances (simulate distributed nodes)**:
   - Open multiple terminal windows
   - Run the command in step 4 in each terminal
   - Observe leadership election in real-time

### Direct JAR Execution
```bash
# Using classpath approach
java -cp target/zookeeper-leader-election-1.0-SNAPSHOT.jar:target/dependency/* LeaderElection

# Or if you have an executable JAR
java -jar target/zookeeper-leader-election-1.0-SNAPSHOT.jar
```

## ⚙️ Configuration

| Parameter | Description | Default Value |
|-----------|-------------|---------------|
| `ZOOKEEPER_ADDRESS` | ZooKeeper server host:port | `localhost:2181` |
| `SESSION_TIMEOUT` | ZooKeeper session timeout (ms) | `3000` |
| `NAMESPACE` | Parent znode for election | `/leader-election` |

## 🎮 Usage Example

### Running Multiple Nodes
```
[Terminal 1]
$ mvn compile exec:java -Dexec.mainClass="LeaderElection"
Node created: /leader-election/_c_54b5f0e6e000000
I'm a leader! 🎉

[Terminal 2]
$ mvn compile exec:java -Dexec.mainClass="LeaderElection"
Node created: /leader-election/_c_54b5f0e6e000001
I'm not a leader. Leader is: /leader-election/_c_54b5f0e6e000000

[Terminal 3]
$ mvn compile exec:java -Dexec.mainClass="LeaderElection"
Node created: /leader-election/_c_54b5f0e6e000002
I'm not a leader. Leader is: /leader-election/_c_54b5f0e6e000000
```

### Expected Behavior
- Each instance creates an ephemeral sequential znode
- Node with smallest sequence number becomes leader
- Non-leader nodes watch their immediate predecessor
- When leader disconnects, next node in sequence becomes leader
- Automatic reconnection on session expiration

## 🎨 System Architecture

```
[Node 1]      [Node 2]      [Node 3]
    |             |             |
    └─────────────┴─────────────┘
            ZooKeeper Ensemble
                |
         [Election Znode]
                |
    ┌───────────┴───────────┐
[Leader]    [Followers]    [Followers]
    |           |              |
 Election    Watch on       Watch on
  Winner     Predecessor    Predecessor
```

## 📊 Election Process Flow

1. **Connection Phase**: Each node connects to ZooKeeper
2. **Znode Creation**: Creates ephemeral sequential znode under `/leader-election`
3. **Sequence Check**: Lists all children and sorts by sequence number
4. **Leadership Decision**:
   - If smallest sequence → Become leader
   - Otherwise → Watch previous node
5. **Watch Trigger**: When a leader fails, leadership is transferred to the next node in the sequence

## 🔧 Troubleshooting

### Common Issues and Solutions:

1. **Connection Refused**:
```bash
# Verify ZooKeeper is running
zkServer.sh status

# Check if port 2181 is listening
netstat -an | grep 2181
```

2. **Session Expired**:
   - Increase `SESSION_TIMEOUT` in code
   - Check network stability between client and ZooKeeper

3. **No Leader Elected**:
   - Verify all nodes use same parent znode path
   - Check ZooKeeper server logs for errors
   - Ensure znodes are being created successfully

4. **Watch Not Triggering**:
   - Verify predecessor znode exists
   - Check if watch is being set correctly
   - Monitor ZooKeeper events with `zkCli.sh`

### ZooKeeper Server Setup
```bash
# Download ZooKeeper
wget https://downloads.apache.org/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz

# Extract and configure
tar -xzf apache-zookeeper-3.7.0-bin.tar.gz
cd apache-zookeeper-3.7.0-bin
cp conf/zoo_sample.cfg conf/zoo.cfg

# Start ZooKeeper
bin/zkServer.sh start
```

## 🛠️ Development

### Building from Source
```bash
# Clean build
mvn clean compile

# Create executable JAR
mvn package

# Run tests (if any)
mvn test
```

### Adding Dependencies
The project uses Maven. Dependencies are in `pom.xml`:
```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.7.0</version>
</dependency>
```

## 🤝 Contributing

We welcome contributions to enhance this leader election example:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/enhancement`)
3. **Commit your changes** (`git commit -m 'Add some enhancement'`)
4. **Push to the branch** (`git push origin feature/enhancement`)
5. **Open a Pull Request**

### Suggested Enhancements
- Add configuration file support
- Implement graceful shutdown handling
- Add metrics and monitoring
- Create Docker containerization
- Add unit tests with embedded ZooKeeper

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

- **Hasan Salamee** - [GitHub Profile](https://github.com/HasanSalamee)

## 🙏 Acknowledgments

- **Apache ZooKeeper** for providing robust distributed coordination
- **ZooKeeper Java Client** for comprehensive API
- **Distributed Systems Community** for patterns and best practices

## 📞 Support

For issues and questions:

1. Check the [Troubleshooting](#-troubleshooting) section
2. Review ZooKeeper server logs
3. Enable debug logging in Java client:
```java
System.setProperty("zookeeper.request.timeout", "5000");
```

## 💡 Best Practices

1. **Production Considerations**:
   - Use ZooKeeper ensemble (3-5 nodes) for high availability
   - Implement connection state listeners
   - Add retry logic for transient failures
   - Monitor ZooKeeper metrics and health

2. **Performance Tips**:
   - Keep znode data small (metadata only)
   - Minimize watch counts per znode
   - Use async APIs for high-throughput scenarios
   - Batch operations when possible

3. **Security**:
   - Enable ZooKeeper authentication (SASL)
   - Use ACLs to restrict znode access
   - Encrypt network traffic (SSL/TLS)

## 🔄 Extension Ideas

1. **Leader-based Task Distribution**:
   - Extend to coordinate distributed task execution
   - Implement work queue with leader assignment

2. **Configuration Management**:
   - Use leader to push configuration updates
   - Implement configuration versioning

3. **Service Registry**:
   - Build service discovery on top of leader election
   - Implement health checking and failover

---

**Note**: This example demonstrates fundamental ZooKeeper patterns for distributed coordination. In production systems, consider using higher-level frameworks like Apache Curator for additional abstractions and robustness.

⭐ **Star this repository** if you find it helpful for understanding distributed leader election!
