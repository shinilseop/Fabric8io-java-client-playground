import io.fabric8.kubernetes.api.model.ContainerStateWaiting;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import java.util.Optional;

public class JobSearch {

  public static void main(String[] args) throws InterruptedException {
    KubernetesClient client = new DefaultKubernetesClient();
    String namespace = "default";

    while (true) {
      List<Job> jobs = client.batch().v1().jobs().inNamespace(namespace).list().getItems();

      System.out.print("<");
      for (Job job : jobs) {
        System.out.print(job.getMetadata().getName() + ", ");
        System.out.print("");
      }
      System.out.println(">");

      for (Job job : jobs) {
        System.out.print(job.getMetadata().getName() + " >> ");
        if (job.getStatus().getConditions().size() > 0) {
          if (job.getStatus().getConditions().get(0).getType().equals("Complete")) {
            System.out.println("Succeeded!");
          } else if (job.getStatus().getConditions().get(0).getType().equals("Failed")) {
            System.out.println("Failed!");
          }
        } else {
          Pod pod = client.pods().inNamespace(namespace)
              .withLabel("job-name", job.getMetadata().getName()).list().getItems().get(0);

          Optional<ContainerStateWaiting> waiting = Optional.ofNullable(
              pod.getStatus().getContainerStatuses().get(0).getState().getWaiting());

          if (waiting.isPresent()) {
            if (waiting.get().getReason().equals("ImagePullBackOff")) {
              System.out.println("ImagePullBackOff!");
            }
          } else {
            System.out.println("Running..");
          }
        }
      }

      Thread.sleep(1000);
    }
  }
}
