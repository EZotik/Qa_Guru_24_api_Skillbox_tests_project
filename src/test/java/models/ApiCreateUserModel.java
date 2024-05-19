package models;

import lombok.Data;
import lombok.Getter;

@Data
public class ApiCreateUserModel {
    @Getter String username;
    @Getter String firstName;
    String lastName;
    @Getter String email;
    String password;
    String phone;
    int userStatus;
    @Getter int id;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
