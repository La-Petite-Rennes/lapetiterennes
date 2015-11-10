package fr.lpr.membership.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.google.common.base.Splitter;

import fr.lpr.membership.domain.Adherent;

@Repository
public class SearchAdherentRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Transactional
	public Page<Adherent> findAdherentByName(String name, Pageable pageable) {
		final FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

		// Create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		final QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Adherent.class).get();
		final BooleanJunction<?> query = qb.bool();
		Splitter.on(' ').omitEmptyStrings().split(name)
				.forEach(term -> query.must(qb.keyword().wildcard().onField("fullName").matching('*' + term.toLowerCase() + '*').createQuery()));

		// wrap Lucene query in a javax.persistence.Query
		final FullTextQuery persistenceQuery = fullTextEntityManager.createFullTextQuery(query.createQuery(), Adherent.class);
		persistenceQuery.setFirstResult(pageable.getOffset());
		persistenceQuery.setMaxResults(pageable.getPageSize());

		// Apply a sort if needed
		if (pageable.getSort() != null) {
			final List<SortField> sortFields = new ArrayList<>();
			for (final Order sortOrder : pageable.getSort()) {
				sortFields.add(new SortField(sortOrder.getProperty(), SortField.Type.STRING, !sortOrder.isAscending()));
			}
			persistenceQuery.setSort(new Sort(sortFields.toArray(new SortField[sortFields.size()])));
		}

		// execute search
		final List<Adherent> result = persistenceQuery.getResultList();
		return new PageImpl<>(result, pageable, pageable.getOffset() + result.size() + 1);
	}
}
