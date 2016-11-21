package fr.lpr.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
