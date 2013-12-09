package restaurant_bayou;
import java.util.Vector;
import restaurant_bayou.CookAgent.Order;
 
public class ProducerConsumerMonitor extends Object {
    private final int N = 5;
    private int count = 0;
    private Vector theData;
    
    synchronized public void insert(Order data) {
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
    
    synchronized public Order remove() {
        Order data;
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
    
    private void insert_item(Order data){
        theData.addElement(data);
    }
    
    private Order remove_item(){
        Order data = (Order) theData.firstElement();
        theData.removeElementAt(0);
        return data;
    }
    
    public ProducerConsumerMonitor(){
        theData = new Vector();
    }
}