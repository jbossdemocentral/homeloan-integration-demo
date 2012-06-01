import com.jboss.bu.soa.demo.riftsaw.listener.RiftsawBusinessEventListener;

RiftsawBusinessEventListener busEventListener = new RiftsawBusinessEventListener();
 
// Start the listener (passing the config)...
busEventListener.startEsbListener(config);
 
// Wait until the Groovy Gateway is signaled to stop...
def stopped = false;
while(!stopped) {
    stopped = gateway.waitUntilStopping(1000);
}
 
// Now stop the listener...
busEventListener.stopEsbListener();
