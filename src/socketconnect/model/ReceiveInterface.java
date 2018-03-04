package socketconnect.model;

import java.util.concurrent.BlockingQueue;

public interface ReceiveInterface {

    BlockingQueue<byte[]> getReceiveDeque();

    Connecter getReceiver();

}
