package rafradek.TF2weapons.client.particle;

public enum EnumTF2Particles {
	BULLET_TRACER(3, 256),
	EXPLOSION(0, 256);
	
	private int paramNum;
	private float range;
	
	EnumTF2Particles(int paramNum, float range){
		this.paramNum = paramNum;
		this.range = range;
	}

	public int getParamNum() {
		return paramNum;
	}
	
	public float getRange() {
		return range;
	}
}
