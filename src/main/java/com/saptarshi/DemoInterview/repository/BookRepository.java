package com.saptarshi.DemoInterview.repository;

import com.saptarshi.DemoInterview.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, QueryByExampleExecutor<Book> {

}
