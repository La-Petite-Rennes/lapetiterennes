package fr.lpr.membership.web.rest;

import static com.google.common.collect.ImmutableList.of;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.Provider;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.domain.sale.PaymentType;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.repository.ProviderRepository;
import fr.lpr.membership.repository.sale.SaleRepository;

// FIXME A Supprimer
@Component
public class DataTest {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private AdherentRepository adherentRepository;

	@PostConstruct
	@Transactional
	public void init() {
		if (!adherentRepository.findAll().isEmpty()) {
			return;
		}

		// Create an adherent and 2 Adhesions
		Adherent adherent = new Adherent().prenom("Goulven").nom("Le Breton");
		Adhesion firstAdhesion = new Adhesion().typeAdhesion(TypeAdhesion.Simple).paymentType(PaymentType.Cash)
				.adherent(adherent).dateAdhesion(LocalDate.now().minusMonths(10));
		Adhesion secondAdhesion = new Adhesion().typeAdhesion(TypeAdhesion.Simple).paymentType(PaymentType.Check)
				.adherent(adherent).dateAdhesion(LocalDate.now().minusDays(5));

		adherent.addAdhesion(firstAdhesion);
		adherent.addAdhesion(secondAdhesion);
		adherentRepository.save(adherent);

		// Create providers and articles
		if (!providerRepository.findAll().isEmpty()) {
			return;
		}

		Provider p1 = new Provider().name("Chain Reaction Cycles");
		Provider p2 = new Provider().name("Probike Shop");
		providerRepository.save(of(p1, p2));

		List<Article> articles = new ArrayList<>(5);
		articles.add(new Article().name("Dérailleur arrière Shimano Tiagra 4700 10v")
				.salePrice(3199).quantity(15).provider(p1).unitPrice(2599));
		articles.add(new Article().name("Cassette Route Shimano Ultegra 6800 11 vitesses")
				.salePrice(5399).quantity(8).provider(p1).unitPrice(4599));
		articles.add(new Article().name("Frein Shimano Dura-Ace 9000").salePrice(11799).quantity(3).provider(p2).unitPrice(11000));
		articles.add(new Article().name("Pneu Route Continental Grand Prix 4000S II - 23c PAIR").salePrice(6999).provider(p2).unitPrice(5150).quantity(25));
		articles.add(new Article().name("Cable de frein").quantity(47));
		articleRepository.save(articles);

		// Enregistrement des ventes
		Random random = new Random();
		int nbSales = 100;
		for (int i = 0; i != nbSales; ++i) {
			Sale sale = new Sale().adherent(adherent).paymentType(PaymentType.Cash)
					.createdAt(DateTime.now().minusMonths(random.nextInt(12))).finished(true);
			Article article = articles.get(random.nextInt(articles.size() - 1));
			sale.addSoldItem(article, 1, article.getSalePrice());
			saleRepository.save(sale);
		}
	}
}
