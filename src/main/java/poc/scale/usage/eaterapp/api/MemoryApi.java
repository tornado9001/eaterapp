package poc.scale.usage.eaterapp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import poc.scale.usage.eaterapp.model.EaterResponse;
import poc.scale.usage.eaterapp.model.Version;

@Slf4j
@RestController
@RequestMapping("/memory")
public class MemoryApi {

  @Autowired
  Version version;

  Runtime runtime = Runtime.getRuntime();

  List<String> memoryChunk = new ArrayList<>();

  /**
   * @param blockSize
   * @param blockLoop
   * @param loopDelay
   * @return
   */
  @PostMapping("/eater/volatile")
  public ResponseEntity<EaterResponse> memoryEaterTemporarly(
      @RequestParam(defaultValue = "1024") int blockSize,
      @RequestParam(defaultValue = "1") int blockLoop,
      @RequestParam(defaultValue = "0") int loopDelay) {
    log.info(
        "Initial Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");
    log.info("Starting memory eater...");

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
    Map<String, String> data = Map.of("list size", String.valueOf(memoryChunk.size()));

    memoryChunk.clear();
    runtime.gc();

    log.info("After Cleared Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024
        + " kB");

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @PostMapping("/eater/permanent")
  public ResponseEntity<EaterResponse> memoryEaterPermanent(
      @RequestParam(defaultValue = "1024") int blockSize,
      @RequestParam(defaultValue = "1") int blockLoop,
      @RequestParam(defaultValue = "0") int loopDelay) {
    log.info(
        "Initial Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");
    log.info("Starting memory eater...");

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
    Map<String, String> data = Map.of("list size", String.valueOf(memoryChunk.size()));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @PostMapping("/free")
  public ResponseEntity<EaterResponse> memoryEaterFreeMemory(
      @RequestParam(defaultValue = "1024") int blockSize,
      @RequestParam(defaultValue = "1") int blockLoop,
      @RequestParam(defaultValue = "0") int loopDelay) {
    log.info(
        "Initial Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");

    int originalSize = memoryChunk.size();

    memoryChunk.clear();
    runtime.gc();

    Map<String, String> data = Map.of("original list size", String.valueOf(originalSize),
        "after free up memory",
        String.valueOf(memoryChunk.size()));

    log.info("After Cleared Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024
        + " kB");

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @GetMapping("/eater/size")
  public ResponseEntity<EaterResponse> memorySize() {
    log.info(
        "Current Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1024 + " kB");

    Map<String, String> data = Map.of("current list size",
        String.valueOf(memoryChunk.size()));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

}
