import org.junit.Test;
/**
 * 
 * solution use Deamon thread. It started, when second thread has finished work.
 *
 */
public class WaitTerminateTutorVer2 {
	Thread t1, t2 ,t2Emulate;
	Object monitor = new Object();
	int runningThreadNumber = 1;

	class TestThread implements Runnable {
		String threadName;
		public boolean shouldTerminate;

		public TestThread(String threadName) {
			this.threadName = threadName;
		}

		@Override
		public void run() {
			for (int i = 0; i < 100; i++) {
				System.out.println(threadName + ":" + i);
				synchronized (monitor) {
					try {
						while (!threadName.equals("t" + runningThreadNumber)) {
							System.out.println("wait for thread " + "t"
									+ runningThreadNumber);
							monitor.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runningThreadNumber++;
					if (runningThreadNumber > 2)
						runningThreadNumber = 1;
					monitor.notifyAll();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (shouldTerminate)
						
						return;
				}
			}
		}
	}

	@Test
	public void testThread() {
		TestThread testThread1 = new TestThread("t1");
		t1 = new Thread(testThread1);
		final TestThread testThread2 = new TestThread("t2");
		t2 = new Thread(testThread2);
		
		final TestThread therad2Emulate = new TestThread("t2");
		t2Emulate = new Thread(therad2Emulate);
		t2Emulate.setDaemon(true);
		
		System.out.println("Starting threads...");
		t1.start();
		t2.start();
		
		
	

		Thread terminator = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				testThread2.shouldTerminate = true;
			}
		});
		terminator.start();

		System.out.println("Waiting threads to join...");
		try {
			t2.join();
			System.out.println("Deamon started");
			t2Emulate.start();
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}