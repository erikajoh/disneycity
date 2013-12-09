package restaurant_pizza;
import java.util.Vector;
 
public class ProducerConsumerMonitor extends Object {
    private final int N = 5;
    private int count = 0;
    private Vector theData;
    
    synchronized public void insert(StandOrder data) {
        while (count == N) {
            try{ 
                System.out.println("\tFull, waiting");
                wait(5000);                         // Full, wait to add
            } catch (InterruptedException ex) {};
        }
            
        insert_item(data);
        count++;
        if(count == 1) {
            System.out.println("\tNot Empty, notify");
            notify();                               // Not empty, notify a 
                                                    // waiting consumer
        }
    }
    
    synchronized public StandOrder remove() {
    	StandOrder data;
        if (count == 0) { return null;}
        data = remove_item();
        count--;
        if(count == N-1){ 
            System.out.println("\tNot full, notify");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return data;
    }
    
    private void insert_item(StandOrder data){
        theData.addElement(data);
    }
    
    private StandOrder remove_item(){
    	StandOrder data = (StandOrder) theData.firstElement();
        theData.removeElementAt(0);
        return data;
    }
    
    public ProducerConsumerMonitor(){
        theData = new Vector();
    }
}