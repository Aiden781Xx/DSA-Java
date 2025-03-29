import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

class SlidingWindowProtocol {
    public static void main(String[] args) {
        System.out.println("\n=== Sliding Window (Go-Back-N) Protocol Simulation ===");
        Receiver receiver = new Receiver();
        Sender sender = new Sender(receiver, 4); // window size of 4
        
        String[] packetsToSend = {"Packet1", "Packet2", "Packet3", "Packet4", 
                                 "Packet5", "Packet6", "Packet7", "Packet8"};
        sender.run(packetsToSend);
    }
}

class Sender {
    private Receiver receiver;
    private int windowSize;
    private final double lossProb;
    private int base = 0;
    private int nextSeqNum = 0;
    private String[] packets;
    private final int timeout = 2000; // milliseconds
    private Map<Integer, Long> timers = new HashMap<>();
    private Random random = new Random();
    
    public Sender(Receiver receiver, int windowSize) {
        this(receiver, windowSize, 0.1);
    }
    
    public Sender(Receiver receiver, int windowSize, double lossProb) {
        this.receiver = receiver;
        this.windowSize = windowSize;
        this.lossProb = lossProb;
    }
    
    public boolean sendPacket(int seqNum) {
        if (seqNum >= packets.length) {
            return false;
        }
        
        // Simulate packet loss
        if (random.nextDouble() < lossProb) {
            System.out.println("Sender: Packet " + seqNum + " lost!");
            return false;
        }
        
        System.out.println("Sender: Sending packet " + seqNum);
        receiver.receive(packets[seqNum], seqNum);
        timers.put(seqNum, System.currentTimeMillis());
        return true;
    }
    
    public void sendWindow() {
        while (nextSeqNum < base + windowSize && nextSeqNum < packets.length) {
            sendPacket(nextSeqNum);
            nextSeqNum++;
        }
    }
    
    public void checkTimeouts() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<Integer, Long> entry : new HashMap<>(timers).entrySet()) {
            int seqNum = entry.getKey();
            long sentTime = entry.getValue();
            
            if (seqNum < base) {
                timers.remove(seqNum);
            } else if (currentTime - sentTime > timeout) {
                System.out.println("Sender: Timeout for packet " + seqNum + 
                                 ", resending window from " + base);
                nextSeqNum = base;
                sendWindow();
                break;
            }
        }
    }
    
    public void receiveAck(int ackNum) {
        if (ackNum >= base) {
            System.out.println("Sender: Received ACK " + ackNum);
            base = ackNum + 1;
            if (base == nextSeqNum) {
                // Window fully acknowledged, stop timers
                timers.clear();
            } else {
                // Send new packets that now fit in the window
                sendWindow();
            }
        }
    }
    
    public void run(String[] packets) {
        this.packets = packets;
        sendWindow();
        
        while (base < packets.length) {
            checkTimeouts();
            
            // Check for received ACKs
            Integer ackNum = receiver.getNextAck();
            if (ackNum != null) {
                receiveAck(ackNum);
            }
            
            try {
                Thread.sleep(100); // Prevent busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
class Receiver {
    private int expectedSeqNum = 0;
    private Queue<Integer> ackQueue = new ArrayDeque<>();
    private Random random = new Random();
    
    public void receive(String packet, int seqNum) {
        if (seqNum == expectedSeqNum) {
            System.out.println("Receiver: Received packet " + seqNum + " - '" + packet + "'");
            expectedSeqNum++;
            // Simulate ACK loss
            if (random.nextDouble() < 0.1) { // 10% ACK loss probability
                System.out.println("Receiver: ACK " + seqNum + " lost!");
            } else {
                System.out.println("Receiver: Sending ACK " + seqNum);
                ackQueue.add(seqNum);
            }
        } else {
            System.out.println("Receiver: Discarding out-of-order packet " + seqNum + 
                             ", expecting " + expectedSeqNum);
            // Resend last ACK
            if (expectedSeqNum > 0) {
                int lastAck = expectedSeqNum - 1;
                System.out.println("Receiver: Resending ACK " + lastAck);
                ackQueue.add(lastAck);
            }
        }
    }
    
    public Integer getNextAck() {
        return ackQueue.poll();
    }
}