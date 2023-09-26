package poc.scale.usage.eaterapp.api;

import java.util.ArrayList;
import java.util.HashMap;
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
@RequestMapping("/cpu")
public class CpuApi {

  @Autowired
  Version version;
  boolean timerFlag = false;

  int durationMin;

  Thread timerThread;

  List<Thread> loadThreads = new ArrayList<>();

  @PostMapping("/threads/start")
  public ResponseEntity<EaterResponse> processorEaterStart(
      @RequestParam(defaultValue = "10") int threadSize,
      @RequestParam(defaultValue = "5") int duration) {

    durationMin = duration;

    timerFlag = true;

    // Set the thread and timer status into this data
    Map<String, String> data = new HashMap<>();

    for (int i = 0; i < threadSize; i++) {
      Thread myThread;
      loadThreads.add(myThread = new Thread(new ThreadLoad()));
      data.put("thread status " + myThread.getId(), String.valueOf(myThread.getState()));
    }
    timerThread = new Thread(new Timer());
    timerThread.start();
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    data.put("timer status", String.valueOf(timerThread.getState()));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @PostMapping("/threads/stop")
  public ResponseEntity<EaterResponse> processorEaterStop() {

    // Set the thread and timer status into this data
    Map<String, String> data = new HashMap<>();

    timerThread.interrupt();
    timerFlag = false;

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // Interrupt all thread, and record the status
    loadThreads.forEach(thread -> {
      thread.interrupt();
      data.put("thread status " + thread.getId(), String.valueOf(thread.getState()));
    });

    // Remove terminated thread
    loadThreads.removeIf(th -> th.getState() == Thread.State.TERMINATED);

    data.put("timer status", String.valueOf(timerThread.getState()));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  @GetMapping("/threads/status")
  public ResponseEntity<EaterResponse> getThreadsStatus() {

    Map<String, String> data = new HashMap<>();

    loadThreads.forEach(th -> {
      data.put("thread status " + th.getId(), String.valueOf(th.getState()));

    });

    data.put("timer status",
        String.valueOf(timerThread == null ? timerThread : timerThread.getState()));

    return ResponseEntity.ok(new EaterResponse(version, data));
  }

  private class Timer implements Runnable {

    @Override
    public void run() {
      log.info("Timer started!!");

      loadThreads.forEach(Thread::start);

      // Thread sleep
      try {
        Thread.sleep((long) durationMin * 60 * 1000);
      } catch (InterruptedException e) {
        log.info("The timer sleep is interrupted!");
        Thread.currentThread().interrupt();
      }

      timerFlag = false;
      log.info("Timer flag is: {}", timerFlag);

      log.info("Timer stopped!!");
    }
  }

  private class ThreadLoad implements Runnable {

    @Override
    public void run() {

      // skip whenever the timer is still running and the thread is not interrupted.
      while (timerFlag && !Thread.currentThread().isInterrupted()) {

        // Do some calculation
        long square = (System.currentTimeMillis() % 13) ^ 2;
        // useless if to create slightly more cpu usage.
        if (square < 0)
          break;
      }
      // thread gonna terminated.
    }
  }
}
