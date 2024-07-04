package store.buzzbook.front.dto.point;

public record PointPolicyResponse(
	long id,
	String name,
	int point,
	double rate,
	boolean deleted
) {
}
