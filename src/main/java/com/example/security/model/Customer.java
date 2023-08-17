package com.example.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "customer_id")
    private int id;

    private String name;

    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //trường này chỉ từ UI->BackEnd chứ k có chiều ngc lại, k gửi pwd từ BackEnd->UI
    private String pwd;

    @Column(name = "create_dt")
    private Date createDt;

    @JsonIgnore //trường này k đc trả về trong json response
    @OneToMany(mappedBy="customer",fetch=FetchType.EAGER) //load các trường 1 user thì load cả các trường của các authority liên quan
    private Set<Authority> authorities;
}
