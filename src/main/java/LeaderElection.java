    import org.apache.zookeeper.*;
    import org.apache.zookeeper.data.Stat;

    import java.io.IOException;
    import java.util.Collections;
    import java.util.List;

    public class LeaderElection implements Watcher {
        private static final String address = "192.168.43.10:2181";
        private static final int SESSION_TIMEOUT = 3000; //dead client
        private static final String ELECTION_NAMESPACE = "/election";

        private String currentZnodeName;
        private ZooKeeper zooKeeper;


        public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
            LeaderElection leaderElection = new LeaderElection();
            leaderElection.connectToZookeeper();
            leaderElection.volunteerForLeadership();
            leaderElection.electLeader();
            leaderElection.run();
            leaderElection.close();
            System.out.println("Successfully Closed");
        }

        public void volunteerForLeadership() throws InterruptedException, KeeperException {
            String znodePrefix = ELECTION_NAMESPACE + "/c_";
            String znodeFullPath = zooKeeper.create(znodePrefix, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            System.out.println(znodeFullPath);
            this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
        }

        public void electLeader() throws InterruptedException, KeeperException {
            List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, this);


            Collections.sort(children);

            String smallestChild = children.get(0); //the first element

            if (smallestChild.equals(currentZnodeName)) {
                System.out.println("I'm a leader");
            } else {
                System.out.println("I'm not a leader" + smallestChild + " is a leader.");
            }
            int myIndex = children.indexOf(currentZnodeName);
            if (myIndex > 0) {
                String previousNode = children.get(myIndex - 1);
                watchPreviousNode(previousNode);
            }

        }

        private void watchPreviousNode(String previousNodeName) throws KeeperException, InterruptedException {
            Stat stat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + previousNodeName, this);
            String previousNodePath = ELECTION_NAMESPACE + "/" + previousNodeName;
            zooKeeper.exists(previousNodePath, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                        try {
                            electLeader();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (KeeperException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            if (stat == null) {
                electLeader();
            }
        }

        private void close() throws InterruptedException {
            this.zooKeeper.close();
        }

        public void connectToZookeeper() throws IOException {
            this.zooKeeper = new ZooKeeper(address, SESSION_TIMEOUT, this);
        }

        public void run() throws InterruptedException {
            synchronized (zooKeeper) {
                zooKeeper.wait();
            }
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            switch (watchedEvent.getType()) {
                case None:
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        System.out.println("Successfully connected to Zookeeper");
                    } else if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
                        synchronized (zooKeeper) {
                            System.out.println("Disconnected from Zookeeper");
                            zooKeeper.notifyAll();
                        }
                    } else if (watchedEvent.getState() == Event.KeeperState.Closed) {
                        System.out.println("Closed Successfully");
                    }
            }
        }
    }
