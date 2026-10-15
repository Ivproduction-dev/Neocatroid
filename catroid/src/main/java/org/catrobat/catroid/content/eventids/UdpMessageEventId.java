package org.catrobat.catroid.content.eventids;

public class UdpMessageEventId extends org.catrobat.catroid.content.eventids.EventId {
	private static final long serialVersionUID = 1L;

	private final String uniqueId = java.util.UUID.randomUUID().toString();

	public UdpMessageEventId() {
		super(EventId.USER_CONCAT);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UdpMessageEventId)) return false;
		return uniqueId.equals(((UdpMessageEventId) o).uniqueId);
	}

	@Override
	public int hashCode() {
		return uniqueId.hashCode();
	}
}
