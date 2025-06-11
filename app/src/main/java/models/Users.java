package models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Users implements Serializable {
    private String userid;
    private String name;
    private String email;
    private String username;
    private String password;
    private String gender;
    private String phonenumber;

    private String profilepicture;
    private Map<String, Object> defaultaddress;
    private List<Map<String, Object>> useractivity;

    public Users() {
    }

    public Users(String userid, String name, String email, String username, String password, String gender, String phonenumber, String profilepicture, Map<String, Object> defaultaddress, List<Map<String, Object>> useractivity) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.phonenumber = phonenumber;
        this.profilepicture = profilepicture;
        this.defaultaddress = defaultaddress;
        this.useractivity = useractivity;
    }

    public List<Map<String, Object>> getUseractivity() {
        return useractivity;
    }

    public void setUseractivity(List<Map<String, Object>> useractivity) {
        this.useractivity = useractivity;
    }

    public Map<String, Object> getDefaultaddress() {
        return defaultaddress;
    }

    public void setDefaultaddress(Map<String, Object> defaultaddress) {
        this.defaultaddress = defaultaddress;
    }



    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }



    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }



    @NonNull
    @Override
    public String toString() {
        return "Users{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
