package com.soft;

import com.mongodb.DBCollection;
import com.soft.mongo.MongodbTestModel;
import com.soft.mongo.MongodbUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {

    @Test
    public void testSave1() {
        MongodbTestModel mo = new MongodbTestModel();
        mo.setMid("123");
        mo.setName("MongodbTestModel");
        mo.setAge("22");
        MongodbUtils.save(mo);
    }

    @Test
    public void testSave2() {
        MongodbTestModel mo = new MongodbTestModel();
        mo.setMid(UUID.randomUUID().toString());
        mo.setName("MongodbTestModel");
        mo.setAge("22");
        MongodbUtils.save(mo, "test23");

    }

    @Test
    public void testRemove1() {
        MongodbTestModel mo = new MongodbTestModel();
        mo.setMid("5eb1091b-b2a8-4a92-b1a3-4d515e837823");
        mo.setName("MongodbTestModel");
        mo.setAge("22");
        MongodbUtils.remove(mo);
    }

    @Test
    public void testRemove2() {
        MongodbTestModel mo = new MongodbTestModel();
        mo.setMid("a27a0652-a0ed-4d7d-a100-d5f11fe1b844");
        mo.setName("MongodbTestModel");
        mo.setAge("22");
        MongodbUtils.remove(mo, "test23");
    }

    @Test
    public void testRemove3() {
        MongodbUtils.removeById("_id", "123", "mongodbTestModel");
    }

    @Test
    public void testUpdate1() {
        String accordingKey = "_id";
        String accordingValue = "e933f48a-cc68-4993-9069-820ff3adab34";
        String[] updateKeys = { "name", "age" };
        Object[] updateValues = { "tat", "22222" };
        String collectionName = "test23";
        MongodbUtils.updateFirst(accordingKey, accordingValue, updateKeys, updateValues, collectionName);
    }

    @Test
    public void testUpdate2() {
        String accordingKey = "name";
        String accordingValue = "tat";
        String[] updateKeys = { "age" };
        Object[] updateValues = { "000000" };
        String collectionName = "test23";
        MongodbUtils.updateMulti(accordingKey, accordingValue, updateKeys, updateValues, collectionName);
    }

    @Test
    public void testFind1() {
        MongodbTestModel obj = new MongodbTestModel();
        String[] findKeys = { "age", "name" };
        String[] findValues = { "22", "MongodbTestModel" };
        List<? extends Object> find = MongodbUtils.find(obj, findKeys, findValues);
        System.out.println(find);
    }

    @Test
    public void testFind2() {
        MongodbTestModel obj = new MongodbTestModel();
        String[] findKeys = { "name" };
        String[] findValues = { "tat" };
        String collectionName = "test23";
        List<? extends Object> find = MongodbUtils.find(obj, findKeys, findValues, collectionName);
        System.out.println(find);
    }

    @Test
    public void testFind3() {
        MongodbTestModel obj = new MongodbTestModel();
        String[] findKeys = { "name" };
        String[] findValues = { "tat" };
        String collectionName = "test23";
        Object findOne = MongodbUtils.findOne(obj, findKeys, findValues, collectionName);
        System.out.println(findOne);
    }

    @Test
    public void testFind4() {
        List<? extends Object> findAll = MongodbUtils.findAll(new MongodbTestModel());
        System.out.println(findAll);
    }

    @Test
    public void testFind5() {
        List<? extends Object> findAll = MongodbUtils.findAll(new MongodbTestModel(),"test23");
        System.out.println(findAll);
    }
}
