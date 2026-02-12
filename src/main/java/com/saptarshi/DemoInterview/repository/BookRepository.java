package com.saptarshi.DemoInterview.repository;

import com.saptarshi.DemoInterview.entity.Author;
import com.saptarshi.DemoInterview.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, QueryByExampleExecutor<Book> {

    @Query("select b from Book b left join fetch b.author")
    List<Book> findAllWithEagerRelationships();

}
