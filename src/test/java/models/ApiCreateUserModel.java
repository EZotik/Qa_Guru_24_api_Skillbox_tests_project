package models;

import lombok.Data;

@Data
public class ApiCreateUserModel {
    String id, username, firstName, lastName, email, password, phone, userStatus;

}
