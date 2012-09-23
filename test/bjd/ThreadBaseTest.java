package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.util.TestUtil;

public class ThreadBaseTest {

	class MyThread extends ThreadBase {

		protected MyThread() {
			super(new Kernel(), "TEST");
		}

		@Override
		protected boolean onStartThread() {
			System.out.println("onStartThread");
			return true;
		}

		@Override
		protected void onLoopThread() {
			while (life) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("onLoopThread");
			}
		}

		@Override
		protected void onStopThread() {
			System.out.println("onStopThread");
		}
		
	}
	
	@Test
	public final void test() {
		
		TestUtil.dispHeader("start() stop()　してisRunnig()の状態を確認する"); //TESTヘッダ
		
		MyThread myThread = new MyThread();
		
		myThread.start();
		TestUtil.dispPrompt(this, "myThread.start()");
		assertThat(myThread.isRunnig(), is(true));
		TestUtil.dispPrompt(this, "isRunnig()=true start()から返った時点で、isRunnig()はTrueになっている");

		myThread.stop();
		TestUtil.dispPrompt(this, "myThread.stop()");
		assertThat(myThread.isRunnig(), is(false));
		TestUtil.dispPrompt(this, "isRunnig()=false end()から返った時点で、isRunnig()はfalseになっている");
		
		TestUtil.dispPrompt(this, "myThread.stop() stop()が重複しても問題ない");
		
		//start()から返った時点で、isRunnig()はTrueになっている
		myThread.start();
		TestUtil.dispPrompt(this, "myThread.start()");
		assertThat(myThread.isRunnig(), is(true));
		TestUtil.dispPrompt(this, "isRunnig()=true");

		myThread.start(); //start()が重複しても問題ない
		TestUtil.dispPrompt(this, "myThread.start() start()が重複しても問題ない");

		myThread.stop();
		TestUtil.dispPrompt(this, "myThread.stop()");
		assertThat(myThread.isRunnig(), is(false));
		TestUtil.dispPrompt(this, "isRunnig()=false");
		
		myThread.dispose();
		TestUtil.dispPrompt(this, "myThread.dispose()");
	}

}
