package fr.lpr.membership.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.lpr.membership.domain.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select a from Article a where a.disabled = false")
    List<Article> findAll(Sort sort);

    @Modifying
    @Query("update Article a set a.disabled = true where a.id = ?1")
    void delete(Long id);
    
	/**
	 * Change the quantity of an article by adding the given quantity
	 *
	 * @param id the identifier of the article
	 * @param quantity the quantity to add, can be negative
	 */
	@Modifying
	@Query("update Article a set a.quantity = a.quantity + ?2 where a.id = ?1")
	void updateQuantity(Long id, int quantity);
}
