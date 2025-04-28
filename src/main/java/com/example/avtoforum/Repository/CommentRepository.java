package com.example.avtoforum.Repository;

import com.example.avtoforum.Model.Entity.Comment;
import com.example.avtoforum.Model.Entity.Topic;
import com.example.avtoforum.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTopic(Topic topic);

    List<Comment> findByUser(User user);
}
