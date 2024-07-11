package store.buzzbook.front.dto.product;

public enum OrderBy {
	NAME, SCORE, REVIEWS;


	public static OrderBy getByName(String name) {
		try {
			return OrderBy.valueOf(name.toUpperCase()); // 모두 대문자로 변환하여 Enum 상수와 비교
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("정렬값이 올바르지 않습니다: " + name);
		}
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
