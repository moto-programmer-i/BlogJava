package moto_pg_i;

public class TestAutoCloseable implements AutoCloseable {
	private static int nextId = 0;
	private int id;

	public TestAutoCloseable() {
		id = nextId;
		System.out.println("TestAutoCloseable " + id + " constructor------------");
		++nextId;
	}

	@Override
	public void close() throws Exception {
		System.out.println("TestAutoCloseable " + id + " close------------------");
	}

	/**
	 * AutoCloseableをフィールドにいれた場合
	 */
	public static class AutoCloseableHolder implements AutoCloseable {
		private final TestAutoCloseable autoCloseable1;
		private final TestAutoCloseable autoCloseable2;

		public AutoCloseableHolder() {
			autoCloseable1 = new TestAutoCloseable();
			autoCloseable2 = new TestAutoCloseable();
		}

		@Override
		public void close() throws Exception {
			// フィールドにいれたAutoCloseableを解放（逆の順番になってしまう）
//			try (autoCloseable1; autoCloseable0) {
//			}
			// TestAutoCloseable 0 close------------------
			// TestAutoCloseable 1 close------------------
			
			// この順番でよい
			// （ローカルで生成した場合と同じようにJava側で解放順を変えてくれる）
			try (autoCloseable1; autoCloseable2) {
			}
//			TestAutoCloseable 1 close------------------
//			TestAutoCloseable 0 close------------------
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("最も標準的な使い方");
		try(var local = new TestAutoCloseable()) {
		}
		
		System.out.println();
		System.out.println("AutoCloseableをフィールドにいれた場合");
		try (var holder = new AutoCloseableHolder()) {
		}
		
		// 実行結果
//		最も標準的な使い方
//		TestAutoCloseable 0 constructor------------
//		TestAutoCloseable 0 close------------------
//
//		AutoCloseableをフィールドにいれた場合
//		TestAutoCloseable 1 constructor------------
//		TestAutoCloseable 2 constructor------------
//		TestAutoCloseable 2 close------------------
//		TestAutoCloseable 1 close------------------
	}

}
