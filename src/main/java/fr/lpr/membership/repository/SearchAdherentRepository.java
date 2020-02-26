package fr.lpr.membership.repository;

import fr.lpr.membership.domain.Adherent;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.CacheMode;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.util.AnalyzerUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@Transactional
public class SearchAdherentRepository {

	@PersistenceContext
	private EntityManager entityManager;

    public void reindex() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

            fullTextEntityManager.createIndexer()
                .batchSizeToLoadObjects(25)
                .cacheMode(CacheMode.NORMAL)
                .threadsToLoadObjects(5)
                .startAndWait();
        } catch (InterruptedException ex) {
            log.error("Erreur lors de l'indexation des personnes", ex);
            Thread.currentThread().interrupt();
        }
    }


    @SuppressWarnings("unchecked")
	public Page<Adherent> findAdherentByName(String name, Pageable pageable) {
		final FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

		// Create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		final QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Adherent.class).get();
		final Analyzer analyzer = fullTextEntityManager.getSearchFactory().getAnalyzer("nameAnalyzer");
		final BooleanJunction<?> query = qb.bool();

		tokenized(analyzer, name).forEach(term -> query.must(qb.keyword().wildcard().onField("fullName").matching('*' + term + '*').createQuery()));

		// wrap Lucene query in a javax.persistence.Query
		final FullTextQuery persistenceQuery = fullTextEntityManager.createFullTextQuery(query.createQuery(), Adherent.class);
		persistenceQuery.setFirstResult((int) pageable.getOffset());
		persistenceQuery.setMaxResults(pageable.getPageSize());

		// Apply a sort if needed
        final List<SortField> sortFields = new ArrayList<>();
        for (final Order sortOrder : pageable.getSort()) {
            sortFields.add(new SortField(sortOrder.getProperty(), SortField.Type.STRING, !sortOrder.isAscending()));
        }
        persistenceQuery.setSort(new Sort(sortFields.toArray(new SortField[0])));

        // execute search
		final List<Adherent> result = persistenceQuery.getResultList();
		return new PageImpl<>(result, pageable, pageable.getOffset() + result.size() + 1);
	}

	private List<String> tokenized(Analyzer analyzer, String term) {
		try {
			return AnalyzerUtils.tokenizedTermValues(analyzer, "fullName", term);
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
