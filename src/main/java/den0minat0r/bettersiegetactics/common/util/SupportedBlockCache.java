package den0minat0r.bettersiegetactics.common.util;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.math.BlockPos;

public class SupportedBlockCache {
	
	private static final Logger LOGGER = LogManager.getLogger();

	private final Cache<BlockPos, HashSet<BlockPos>> supports;

	public SupportedBlockCache(int size) {
		this.supports = new Cache<BlockPos, HashSet<BlockPos>>(size);
	}
	
	public void put(BlockPos support, BlockPos supported) {
		LOGGER.debug("Block Cached");
		supports.putIfAbsent(supported, new HashSet<BlockPos>(4));
		supports.get(supported).add(support);
	}
	
	public boolean isSupported(BlockPos supported) {
		HashSet<BlockPos> support = supports.get(supported);
		return support == null ? false : support.size() > 0;
	}
	
	public void remove(BlockPos pos) {
		LOGGER.debug("Block Evicted");
		for (Iterator<HashSet<BlockPos>> supported = supports.values().iterator(); supported.hasNext(); )
		{
			supported.next().remove(pos);
		}
		supports.remove(pos);
	}
	
}
