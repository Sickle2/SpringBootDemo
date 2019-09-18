package com.example.demo.repository;

import com.example.demo.model.DemoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: demo
 * @description: oauth config
 * @author: sickle
 * @create: 2019-09-17 16:25
 **/
@Repository
public interface UserRepository extends JpaRepository<DemoUser, Long> {

    @Query(nativeQuery = true, value = "select u.user_type from i_user u where u.username=?1")
    List<Integer> getUserType(String username);

    DemoUser getByUsername(String username);
}
