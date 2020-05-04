package den0minat0r.bettersiegetactics.common.util;

import java.util.HashSet;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.math.BlockPos;

public class SupportedBlockCache {
	
	private static final Logger LOGGER = LogManager.getLogger();

	private final Cache<BlockPos, HashSet<BlockPos>> supportToSupported;
	private final Cache<BlockPos, HashSet<BlockPos>> supportedToSupport;

	public SupportedBlockCache(int size) {
		this.supportedToSupport = new Cache<BlockPos, HashSet<BlockPos>>(size);
		this.supportToSupported = new Cache<BlockPos, HashSet<BlockPos>>(size);
	}
	
	public void put(BlockPos support, BlockPos supported) {
		LOGGER.debug("Block Cached");
		supportToSupported.putIfAbsent(support, new HashSet<BlockPos>(4));
		supportToSupported.get(support).add(supported);
		
		supportedToSupport.putIfAbsent(supported, new HashSet<BlockPos>(4));
		supportedToSupport.get(supported).add(support);
	}
	
	/**
	 * Returns whether {@code supported} is present in the cache as being supported by {@code support}.
	 * 
	 * NOTE: A {@code false} return value does not guarantee that {@code supported} is not supported by {@code support}, 
	 * only that it is not present in the cache.
	 * 
	 * @param support
	 * @param supported
	 * @return {@code true} if the cache contains an entry for {@code supported} being supported by {@code support}.
	 */
	public boolean isCachedSupportFor(BlockPos support, BlockPos supported) {
		HashSet<BlockPos> supported_blocks = supportToSupported.get(support);
		return supported_blocks == null ? false : supported_blocks.contains(supported);
	}
	
	public boolean isCachedSupport(BlockPos support) {
//		LOGGER.debug("Block Queried Support");
		HashSet<BlockPos> supported = supportToSupported.get(support);
		return supported == null ? false : supported.size() > 0;
	}
	
	public boolean isCachedSupported(BlockPos supported) {
//		LOGGER.debug("Block Queried Supported");
		HashSet<BlockPos> support = supportToSupported.get(supported);
		return support == null ? false : support.size() > 0;
	}
	
	public void remove(BlockPos pos) {
		LOGGER.debug("Block Evicted");
		HashSet<BlockPos> supported_blocks = supportToSupported.get(pos);
		HashSet<BlockPos> support_blocks = supportedToSupport.get(pos);

		if (supported_blocks != null) {
			for (BlockPos supported : supported_blocks)
			{
				supportedToSupport.remove(supported);
			}
		}

		if (support_blocks != null) {
			for (BlockPos support : support_blocks)
			{
				supportToSupported.remove(support);
			}
		}

		supportToSupported.remove(pos);
		supportedToSupport.remove(pos);
	}
	
	public Pair<Double, Double> getHitRate() {
		return Pair.of(supportedToSupport.getHitRate(), supportToSupported.getHitRate());
	}
	
}
