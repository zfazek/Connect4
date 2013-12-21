package com.ongroa.connect4;

import junit.framework.TestCase;

import org.junit.Test;

public class TestHash extends TestCase {
	
	@Test
	public void testHashEquality() {
		Table table = new Table();
		long hash1 = table.hash.getHash(table.getTable(), Table.HUMAN);
		System.out.println(hash1);
		long hash2 = table.hash.getHash(table.getTable(), Table.HUMAN);
		System.out.println(hash2);
		assertEquals(hash1, hash2);
		long hash3 = table.hash.getHash(table.getTable(), Table.COMPUTER);
		System.out.println(hash3);
		assertFalse(hash1 == hash3);
	}

}
