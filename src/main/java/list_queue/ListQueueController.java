/* 
 * AFTER RUNNING PROJECT WITH COMMAND: 
 * `gradle build && java -Dserver.port=0080 -jar build/libs/gs-spring-boot-0.1.0.jar`
 * RUN CURL COMMAND:
 * `curl {baseUrl}/queues`
 * EXPECT JSON TO BE RETURNED:
 * [{"uri":"/Accounts/{accountId}/Queues/{queueId}",
 *  "dateCreated":"{dateCreated}",
 *  "dateUpdated":"{dateUpdated}",
 *  "revision":1,
 *  "queueId":"{queueId}",
 *  "alias":"Tutorial Queue",
 *  "currentSize":0,
 *  "maxSize":25,
 *  "averageWaitTime":0,
 *  "subresourceUris":{"members":"/Accounts/{accountId}/Queues/{queueId}/Members"}}]
*/

package main.java.list_queue;

import com.vailsys.persephony.api.PersyClient;
import com.vailsys.persephony.api.PersyException;
import com.vailsys.persephony.api.queue.Queue;
import com.vailsys.persephony.api.queue.QueueList;
import com.vailsys.persephony.api.queue.QueuesSearchFilters;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@RestController
public class ListQueueController {
  // Get accountID and authToken from environment variables
  private String accountId = System.getenv("ACCOUNT_ID");
  private String authToken = System.getenv("AUTH_TOKEN");

  @RequestMapping("/queues")
  public ArrayList<Queue> listQueues() {
    QueuesSearchFilters filters = new QueuesSearchFilters();
    filters.setAlias("Tutorial Queue");

    try {
      PersyClient client = new PersyClient(accountId, authToken); // Create PersyClient object

      // Invoke get method to retrieve the first page of queues with a matching alias
      QueueList queueList = client.queues.get(filters);

      // Check if the list is empty by checking its total size
      if (queueList.getTotalSize() > 0) {
        // Retrieve all pages of results
        while (queueList.getLocalSize() < queueList.getTotalSize()) {
          queueList.loadNextPage();
        }

        // Retrieve the inner ArrayList of queues to process
        ArrayList<Queue> queues = queueList.export();
        for (Queue queue : queues) {
          // Process queue element in some way
          System.out.println(queue);
        }
        return queues;
      }
    } catch (PersyException pe) {
      System.out.println(pe.getMessage());
    }

    return null;
  }
}