import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class WatchersAndTriggers implements Watcher {

    private static final String address = "192.168.43.10:2181";

    private static final int SESSION_TIMEOUT = 3000; //dead client
    private static final String TargetZnode = "/target_znode";

    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        WatchersAndTriggers watchersAndTriggers = new WatchersAndTriggers();

        watchersAndTriggers.connectToZookeeper();
        watchersAndTriggers.testWatcher();

        watchersAndTriggers.run();
        watchersAndTriggers.close();

    }

    private void close() throws InterruptedException {
        this.zooKeeper.close();
    }

    private void run() throws InterruptedException {
        synchronized (zooKeeper) {
            this.zooKeeper.wait();
        }
    }

    private void connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(address, SESSION_TIMEOUT, this);
    }

    public void testWatcher() throws InterruptedException, KeeperException {
        Stat stat = zooKeeper.exists(TargetZnode, this);
        if (stat == null) {
            System.out.println(TargetZnode + " not exist");
            return;
        }
        byte[] data = zooKeeper.getData(TargetZnode, this, stat);
        List<String> children = zooKeeper.getChildren(TargetZnode, this);

        System.out.println("Data " + new String(data) + "children " + children);
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
            case NodeCreated:
                System.out.println(TargetZnode + " Created");
                break;
            case NodeDeleted:
                System.out.println(TargetZnode + " Deleted");
                break;
            case NodeDataChanged:
                System.out.println(TargetZnode + " DataChanged");
                break;
            case NodeChildrenChanged:
                System.out.println(TargetZnode + " ChildrenChanged");
                break;
        }
        try {
            testWatcher();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }
}
