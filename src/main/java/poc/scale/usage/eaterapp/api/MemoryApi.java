package poc.scale.usage.eaterapp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import poc.scale.usage.eaterapp.model.EaterResponse;
import poc.scale.usage.eaterapp.model.Version;

@Slf4j
@RestController
@RequestMapping("/memory")
public class MemoryApi {

  @Autowired
  Version version;

  Runtime runtime = Runtime.getRuntime();

  List<String> memoryChunk;

  @PostMapping("/eater/heap")
  public ResponseEntity<EaterResponse> memoryEaterPermanent(
      @RequestParam(defaultValue = "1024") int blockSize,
      @RequestParam(defaultValue = "1") int blockLoop,
      @RequestParam(defaultValue = "0") int loopDelay) {
    log.info(
        "Initial Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");
    log.info("Starting memory eater consuming heap...");

    if (memoryChunk == null) {
      memoryChunk = new ArrayList<>();
    }

    for (int i = 0; i < blockLoop; i++) {
      // Generate random string with size of blockSize
      String randomString = String.valueOf(Math.random() * 26 + 'a').repeat(blockSize);

      // Add to memory chunk
      memoryChunk.add(randomString);

      // Thread sleep
      try {
        Thread.sleep(loopDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    log.info("After Eater Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024
        + " kB");

    double heapUsage = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) runtime.totalMemory() * 100;

    log.info(String.format("Heap usage %.2f %%", heapUsage));

    Map<String, String> data = Map.of("list size", String.valueOf(memoryChunk.size()), "heap usage",
        String.format("%.2f%%", heapUsage));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @PostMapping("/free")
  public ResponseEntity<EaterResponse> memoryEaterFreeMemory(
      @RequestParam(defaultValue = "1024") int blockSize,
      @RequestParam(defaultValue = "1") int blockLoop,
      @RequestParam(defaultValue = "0") int loopDelay) {
    log.info(
        "Initial Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");

    String originalSize = String.valueOf(memoryChunk == null ? 0 : memoryChunk.size());

    memoryChunk = null;
    runtime.gc();

    double heapUsage = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) runtime.totalMemory() * 100;

    log.info(String.format("Heap usage %.2f %%", heapUsage));

    Map<String, String> data = Map.of("original list size", String.valueOf(originalSize),
        "after free up memory",
        String.valueOf(String.valueOf(memoryChunk == null ? 0 : memoryChunk.size())), "heap usage",
        String.format("%.2f%%", heapUsage));

    log.info("After Cleared Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024
        + " kB");

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @GetMapping("/eater/size")
  public ResponseEntity<EaterResponse> memorySize() {

    log.info(
        "Current Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");

    double heapUsage = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) runtime.totalMemory() * 100;

    log.info(String.format("Heap usage %.2f %%", heapUsage));

    Map<String, String> data = Map.of("current list size",
        String.valueOf(memoryChunk == null ? 0 : memoryChunk.size()), "heap usage",
        String.format("%.2f%%", heapUsage));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

}
