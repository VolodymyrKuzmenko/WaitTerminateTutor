import org.junit.Test;

//key solution: use isAlive()
public class WaitTerminateTutorVer1 {
	Thread t1, t2;
	Object monitor = new Object();
	volatile int runningThreadNumber = 1;

	class TestThread implements Runnable {
		String threadName;
		public boolean shouldTerminate;
		
		public TestThread(String threadName) {
			this.threadName = threadName;
		}
		
		@Override
		public void run() {
			for (int i=0;i<100;i++) {
				System.out.println(threadName+":"+i);
				synchronized(monitor) {
					try {
						
						while (t2.isAlive() && !threadName.equals("t"+runningThreadNumber)) {
							System.out.println("wait for thread "+"t"+runningThreadNumber);
					
							monitor.wait();;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runningThreadNumber++;
					if (runningThreadNumber>2) runningThreadNumber=1;
					monitor.notifyAll();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (shouldTerminate){
						System.out.println("terminate "+ threadName);
						
						return;
					}
				}
			}
			System.out.println(threadName+" finished");
		}
	}
	
	@Test
	public void testThread() {
		TestThread testThread1 = new TestThread("t1");
		t1 = new Thread(testThread1);
		final TestThread testThread2 = new TestThread("t2");
		t2 = new Thread(testThread2);
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
				testThread2.shouldTerminate=true;
			}
		});
		terminator.start();

		System.out.println("Waiting threads to join...");
	    try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}