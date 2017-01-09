## Hibernate Postgres JSONB

A working implementation of JSONB with Hibernate and Jackson ObjectNode.
<br />
The library address the problem of using Hibernate with Postgres JSONB 

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

##### Usage example with JsonEntity
Look at JsonEntityTest for more info
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

        // Query and get object node back
        JsonEntity queryEntity = entityManager.find(JsonEntity.class, id);
        ObjectNode queryNode = queryEntity.getJson();
    }
    
    /**
     * How to store custom pojo object?
     */
    @Test
    void persistObject() throws Exception {
        MyCustomObject object = new MyCustomObject();
        
        // Persist entity
        JsonEntity entity = new JsonEntity();
        entity.setJson(mapper.valueToTree(object));
        entityManager.persist(entity);
        
        // Query entity
        JsonEntity queryEntity = entityManager.find(JsonEntity.class, entity.getId());
        MyCustomObject queryObject = mapper.treeToValue(queryEntity.getJson(), MyCustomObject.class);
        assertEquals(queryObject, object);
    }
}
```

See https://github.com/pires/hibernate-postgres-jsonb for more