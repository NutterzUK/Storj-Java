package storj.io.restclient.model;

import java.util.Calendar;
import java.util.List;

/**
 * Represents a file staging frame. Created by Stephen Nutbrown on 06/07/2016.
 */
public class Frame {

	private String id;
	private String user;
	private List<Shard> shards;
	private Long size;
	private boolean locked;
	private Calendar created;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Shard> getShards() {
		return shards;
	}

	public void setShards(List<Shard> shards) {
		this.shards = shards;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "Frame [user=" + user + ", id=" + id + ", shards=" + shards + ", size=" + size + ", locked=" + locked
				+ ", created=" + created + "]";
	}

}
