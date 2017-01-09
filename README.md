## Hibernate Postgres JSONB

A working implementation of JSON with Hibernate and Jackson ObjectNode.

Using Postgres JSONB in HibernateJPA
- Hibernate + JPA
- Postgres + JSONB
- Jackson + (ObjectNode or CustomPOJO)

Only 2 class file!
- JsonPostgreSQLDialect
- JsonUserType

### Setup & Test
Setup JSONB
```xml
<!-- Set dialect to org.hibernate.dialect.JsonPostgreSQLDialect -->
<property name="hibernate.dialect" value="org.hibernate.dialect.JsonPostgreSQLDialect"/>
```

Setup Entity Class
```java
@TypeDef(name = "jsonb", typeClass = JsonUserType.class)
@Entity
public class JsonEntity {
    
    @Type(type = "jsonb")
    private ObjectNode json;
}
```

Run test with Postgres Docker container and gradlew test
```bash
docker run -d -p 32978:5432 -e POSTGRES_USER=jsonb-user -e POSTGRES_PASSWORD=6w51SG476dfd --name jsonb-database postgres
gradlew test
```

### Some Examples

#### JsonEntity
```java
@TypeDef(name = "jsonb", typeClass = JsonUserType.class)
@Entity
public class JsonEntity {

    private String id;
    private String name; // normal field
    private ObjectNode json; // jsonb

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    @Id
    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    @Column(nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "jsonb")
    @Column(nullable = true)
    public ObjectNode getJson() {
        return json;
    }

    public void setJson(ObjectNode json) {
        this.json = json;
    }
}
```

##### Usage example
```java
class TestExample{
    static ObjectMapper mapper = new ObjectMapper();
    EntityManager entityManager; // Provide your own EntityManager instance
    
    /**
     * How to store jackson ObjectNode
     */
    @Test
    void persistJson() throws Exception {
        JsonEntity entity = new JsonEntity();
        entity.setName("my name");

        // Create object node and populate
        ObjectNode node = mapper.createObjectNode();
        node.put("parser", "jackson");
        entity.setJson(node);

        // Persist and get generated unique id
        entityManager.persist(entity);
        final String id = entity.getId();

        // Query and assert test
        JsonEntity queryEntity = entityManager.find(JsonEntity.class, id);
        assertEquals(queryEntity.getId(), id);
        assertEquals(queryEntity.getJson().path("parser").asText(), "jackson");

        // Update Entity
        JsonEntity updateEntity = entityManager.find(JsonEntity.class, id);
        updateEntity.getJson().put("name", "Fuxing");
        entityManager.persist(updateEntity);

        // Query and assert updated entity
        queryEntity = entityManager.find(JsonEntity.class, id);
        assertEquals(queryEntity .getJson().path("name").asText(), "Fuxing");
    }
    
    /**
     * How to store custom pojo object?
     */
    @Test
    void persistObject() throws Exception {
        MyCustomObject object = new MyCustomObject();
        
        JsonEntity entity = new JsonEntity();
        entity.setJson(mapper.valueToTree(object));
        
        // Persist
        entityManager.persist(entity);
        
        // Query
        JsonEntity queryEntity = entityManager.find(JsonEntity.class, entity.getId());
        
        MyCustomObject queryObject = mapper.treeToValue(queryEntity.getJson(), MyCustomObject.class);
        assertEquals(queryObject, object);
    }
}
```

See https://github.com/pires/hibernate-postgres-jsonb for more