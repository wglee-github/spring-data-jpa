package study.datajpa.dto;

public class UsernameOnlyDto {
	
	private final String username;
	private final int age;
	
	public UsernameOnlyDto(String username, int age) {
		this.username = username;
		this.age = age;
	}
	
	public String getUsername() {
		return username;
	}

	public int getAge() {
		return age;
	}
	
}
