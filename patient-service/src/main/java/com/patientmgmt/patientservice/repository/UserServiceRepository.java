package com.patientmgmt.patientservice.repository;

import com.patientmgmt.patientservice.model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserServiceRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private DynamoDbTable<User> userTable;

    public UserServiceRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.userTable = dynamoDbEnhancedClient.table("users", TableSchema.fromBean(User.class));;
    }

    public User save(User user) {
        userTable.putItem(user);
        return user;
    }

    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        userTable.scan().items().forEach(userList::add);
        return userList;
    }

    public User findById(String id) {
        return userTable.getItem(Key.builder().partitionValue(id).build());
    }

    public void deleteById(String id) {
        userTable.deleteItem(Key.builder().partitionValue(id).build());
    }
}
