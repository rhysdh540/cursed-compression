package dev.rdh.idk.packed;

public class TestMain {
	public static void main(String[] args) {
		System.out.println("Hello from TestMain!");
		InnerClass.wow();
	}

	private static class InnerClass {
		public static void wow() {
			System.out.println("Hello from InnerClass!");
		}
	}
}
