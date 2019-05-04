package rafradek.minecraft2source;

public class BlockRange {
	public final int minX;
	public final int minY;
	public final int minZ;
	public final int maxX;
	public final int maxY;
	public final int maxZ;
	public BlockRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super();
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public boolean intersects(BlockRange range)
    {
        return this.minX <= range.maxX && this.maxX >= range.minX && this.minY <= range.maxY && this.maxY >= range.minY && this.minZ <= range.maxZ && this.maxZ >= range.minZ;
    }
	
	public boolean contains(BlockRange range)
    {
        return this.minX <= range.minX && this.maxX >= range.maxX && this.minY <= range.minY && this.maxY >= range.maxY && this.minZ <= range.minZ && this.maxZ >= range.maxZ;
    }
}
