package ru.yandex.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.UserJpaRepository;
import ru.yandex.practicum.shareit.utils.TestUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    @Autowired private TestEntityManager em;

    @Autowired
    ItemJpaRepository itemRepository;

    @Autowired
    UserJpaRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(em).isNotNull();
    }

    @Test
    void shouldReturn() {
        EntityManager entityManager = em.getEntityManager();
        TypedQuery<Item> query = entityManager
                .createQuery("select i " +
                        "from Item i " +
                        "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
                        "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
                        "and i.available is true", Item.class);
        User user = TestUtils.makeUser(1);
        userRepository.save(user);

        Item item1 = new Item(1L, "abc name", "description 1", true, user, null, null, null, null);
        Item item2 = new Item(2L, "name 2", "abc description", true, user, null, null, null, null);
        Item item3 = new Item(3L, "bla bla bla", "bla bla bla", true, user, null, null, null, null);

        assertThat(query.setParameter(1, "aBc").getResultList()).isEmpty();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        assertThat(itemRepository.findAllByText("aBc", null)).hasSize(2);
    }
}
