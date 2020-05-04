package den0minat0r.bettersiegetactics.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	private final int cacheSize;
	private long queries;
	private long hits;
	
	public Cache(int size) {
		super(size, 0.75f, true);
		cacheSize = size;
		queries = 0;
		hits = 0;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() >= this.cacheSize; 
	}
	
	@Override
	public V get(Object key) {
		V obj = super.get(key);
		++queries;
		if (obj != null) 
		{
			++hits;
		}
		return obj;
	}
	
	public double getHitRate()
	{
		return ((float)hits) / (queries != 0 ? queries : 1);
	}

}
