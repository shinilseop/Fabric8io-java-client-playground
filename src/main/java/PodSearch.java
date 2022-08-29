import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;

public class PodSearch {
  public static void main(String[] args) throws InterruptedException {
    KubernetesClient client = new DefaultKubernetesClient();

    String namespace = "default";
    String jobName = "pi2";

    boolean isFinish = false;
    int interval = 1;

    while (!isFinish) {
      PodList podList = client.pods().inNamespace(namespace)
          .withLabel("job-name", jobName).list();

      List<Pod> pods = podList.getItems();


      System.out.print("<");
      for (Pod pod : pods) {
        System.out.print(pod.getMetadata().getName()+", ");
      }
      System.out.println("> "+ pods.size());


      for (Pod pod : pods) {
        System.out.println("POD NAME : "+pod.getMetadata().getName());
        System.out.println("POD STATUS : "+pod.getStatus().getPhase());

        if (pod.getMetadata().getName().equals(jobName)) {
          if (pod.getStatus().getPhase().equals("Succeeded")) {
            System.out.println("modelMetadata.changeStatus(Status.SUCCEEDED)");
            isFinish = true;
            break;
          } else if (pod.getStatus().getPhase().equals("Pending")) {

          } else if (pod.getStatus().getPhase().equals("Failed")) {
            System.out.println("modelMetadata.changeStatus(Status.FAILED)");
            isFinish = true;
            break;
          }
        }
      }

      Thread.sleep(interval * 1000);
    }
  }
}
