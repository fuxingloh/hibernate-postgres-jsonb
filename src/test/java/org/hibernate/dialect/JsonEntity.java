package org.hibernate.dialect;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.usertype.JsonUserType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created By: Fuxing Loh
 * Date: 5/1/2017
 * Time: 4:30 PM
 * Project: hibernate-postgres-jsonb
 */
@TypeDef(name = "jsonb", typeClass = JsonUserType.class)
@Entity
class JsonEntity {

    private String id;
    private String name;
    private Long longValue;

    private ObjectNode json;

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

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = true)
    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    @Column(nullable = true)
    @Type(type = "jsonb")
    public ObjectNode getJson() {
        return json;
    }

    public void setJson(ObjectNode json) {
        this.json = json;
    }
}
