import java.util.Random;

class StopAndWaitProtocol {
    public static void main(String[] args) {
        System.out.println("=== Stop-and-Wait Protocol Simulation ===");
        Receiver receiver = new Receiver();
        Sender sender = new Sender(receiver);
        
        String[] packetsToSend = {"Hello", "World", "How", "Are", "You"};
        sender.run(packetsToSend);
    }
}

class Sender {
    private Receiver receiver;
    private int seqNum = 0;
    private final double lossProb;
    private final int timeout = 2000; // milliseconds
    private long lastSentTime;
    private String lastPacket;
    private Random random = new Random();
    
    public Sender(Receiver receiver) {
        this(receiver, 0.1);
    }
    
    public Sender(Receiver receiver, double lossProb) {
        this.receiver = receiver;
        this.lossProb = lossProb;
    }
    
    public boolean send(String packet) {
        // Simulate packet loss
        if (random.nextDouble() < lossProb) {
            System.out.println("Sender: Packet " + seqNum + " lost!");
            return false;
        }
        
        lastPacket = packet;
        lastSentTime = System.currentTimeMillis();
        System.out.println("Sender: Sending packet " + seqNum);
        receiver.receive(packet, seqNum);
        return true;
    }
    
    public boolean waitForAck() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            if (receiver.getAckReceived() == seqNum) {
                System.out.println("Sender: Received ACK for packet " + seqNum);
                seqNum = 1 - seqNum; // Toggle between 0 and 1
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Sender: Timeout waiting for ACK " + seqNum);
        return false;
    }
    
    public void run(String[] packets) {
        for (String packet : packets) {
            boolean success = false;
            while (!success) {
                boolean sent = send(packet);
                if (sent) {
                    boolean ackReceived = waitForAck();
                    if (ackReceived) {
                        success = true;
                    } else {
                        System.out.println("Sender: Resending packet due to timeout");
                    }
                } else {
                    System.out.println("Sender: Resending packet due to loss");
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}

class Receiver {
    private int expectedSeqNum = 0;
    private Integer ackReceived = null;
    private Random random = new Random();
    
    public void receive(String packet, int seqNum) {
        if (seqNum == expectedSeqNum) {
            System.out.println("Receiver: Received packet " + seqNum + " - '" + packet + "'");
            expectedSeqNum = 1 - expectedSeqNum;
            // Simulate ACK loss
            if (random.nextDouble() < 0.1) { // 10% ACK loss probability
                System.out.println("Receiver: ACK " + seqNum + " lost!");
                ackReceived = null;
            } else {
                System.out.println("Receiver: Sending ACK " + seqNum);
                ackReceived = seqNum;
            }
        } else {
            System.out.println("Receiver: Discarding out-of-order packet " + seqNum + 
                             ", expecting " + expectedSeqNum);
            // Resend last ACK
            if (ackReceived != null) {
                System.out.println("Receiver: Resending ACK " + ackReceived);
            }
        }
    }
    
    public Integer getAckReceived() {
        return ackReceived;
    }
}