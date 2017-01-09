package org.hibernate.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fxloh.hibernate.utils.HibernateUtils;
import com.fxloh.hibernate.utils.TransactionProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created By: Fuxing Loh
 * Date: 5/1/2017
 * Time: 4:28 PM
 * Project: hibernate-postgres-jsonb
 */
class JsonPostgreSQLDialectTest {

    static ObjectMapper mapper = new ObjectMapper();
    static TransactionProvider provider;

    @BeforeAll
    static void beforeAll() throws Exception {
        // If you have docker, use this command to create a test db
        // docker run -d -p 32978:5432 -e POSTGRES_USER=jsonb-user -e POSTGRES_PASSWORD=6w51SG476dfd --name jsonb-database postgres
        HibernateUtils.shutdown();

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hikari.dataSource.url", "jdbc:postgresql://localhost:32978/jsonb-user");
        properties.put("hibernate.hikari.dataSource.user", "jsonb-user");
        properties.put("hibernate.hikari.dataSource.password", "6w51SG476dfd");

        HibernateUtils.setupFactory(properties);
        provider = HibernateUtils.get();
    }

    @Test
    void persist() throws Exception {
        JsonEntity entity = new JsonEntity();
        entity.setLongValue(500L);
        entity.setName("Awesome");

        // Persist and get Id
        final String id = provider.reduce(em -> {
            em.persist(entity);
            return entity.getId();
        });

        // Assert non json entity
        JsonEntity queryEntity = provider.reduce(em -> em.find(JsonEntity.class, id));
        assertEquals(queryEntity.getId(), id);
        assertEquals(queryEntity.getLongValue().longValue(), 500L);
    }

    @Test
    void persistJson() throws Exception {
        JsonEntity entity = new JsonEntity();
        entity.setLongValue(500L);
        entity.setName("Awesome");

        // Create object node and populate
        ObjectNode node = mapper.createObjectNode();
        node.put("parser", "jackson");
        entity.setJson(node);

        // Persist & get Id
        final String id = provider.reduce(em -> {
            em.persist(entity);
            return entity.getId();
        });

        // Query and assert values
        JsonEntity queryEntity = provider.reduce(em -> em.find(JsonEntity.class, id));
        assertEquals(queryEntity.getId(), id);
        assertEquals(queryEntity.getJson().path("parser").asText(), "jackson");

        // Update entity
        provider.with(em -> {
            JsonEntity e = em.find(JsonEntity.class, id);
            e.getJson().put("name", "Fuxing");
            em.persist(e);
        });

        // Query updated entity and assert
        JsonEntity entity2 = provider.reduce(em -> em.find(JsonEntity.class, id));
        assertEquals(entity2.getJson().path("name").asText(), "Fuxing");
    }

    @Test
    void persistObject() throws Exception {
        MyCustomObject object = new MyCustomObject();
        object.setValue("Foo");

        JsonEntity entity = new JsonEntity();
        entity.setName("Awesome");
        entity.setJson(mapper.valueToTree(object));

        // Persist
        final String id = provider.reduce(em -> {
            em.persist(entity);
            return entity.getId();
        });

        // Query & assert
        JsonEntity queryEntity = provider.reduce(em -> em.find(JsonEntity.class, id));
        MyCustomObject queryObject = mapper.treeToValue(queryEntity.getJson(), MyCustomObject.class);
        assertEquals(queryObject, object);
    }

    public static class MyCustomObject {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyCustomObject that = (MyCustomObject) o;

            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}