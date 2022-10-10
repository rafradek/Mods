package rafradek.TF2weapons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalMap<A, B> {

	public ConcurrentHashMap<A, B> client = new ConcurrentHashMap<>();
	public ConcurrentHashMap<A, B> server = new ConcurrentHashMap<>();

	public Map<A, B> get(boolean client) {
		return client ? this.client : this.server;
	}

}
