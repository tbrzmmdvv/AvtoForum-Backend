package com.example.avtoforum.Repository;


import com.example.avtoforum.Model.Entity.Category;
import com.example.avtoforum.Model.Entity.Topic;
import com.example.avtoforum.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByCategory(Category category);

    List<Topic> findByUser(User user);

    List<Topic> findTop5ByOrderByCreatedAtDesc();

    List<Topic> findByCategoryOrderByCreatedAtDesc(Category category);
}
