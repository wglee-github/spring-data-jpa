package study.datajpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import study.datajpa.entity.Item;

@SpringBootTest
@Rollback(false)
class ItemRepositoryTest {

	@Autowired ItemRepository itemRepository;
	
	/**
	 * 
	 	* 참고
	 	· JPA 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다. 
	 	· 그런데 JPA 식별자 생성 전략이 @Id 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 save() 를 호출한다. 
	 	· 따라서 이 경우 merge() 가 호출된다. merge() 는 우선 DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다. 
	 	· 따라서 Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하는게 효과적이다.
		· 참고로 등록시간( @CreatedDate )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다. 
		  (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)
	 * 
	 */
	@Test
	void save() {
		
		Item item = new Item("A");
		itemRepository.save(item);
	}

}
