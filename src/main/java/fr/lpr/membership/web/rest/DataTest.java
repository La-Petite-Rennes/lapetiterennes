package fr.lpr.membership.web.rest;

import fr.lpr.membership.config.Constants;
import fr.lpr.membership.domain.*;
import fr.lpr.membership.domain.sale.PaymentType;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.repository.ProviderRepository;
import fr.lpr.membership.repository.sale.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.ImmutableList.of;

// FIXME A Supprimer
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
@RequiredArgsConstructor
public class DataTest {

	private final ArticleRepository articleRepository;

	private final ProviderRepository providerRepository;

	private final SaleRepository saleRepository;

	private final AdherentRepository adherentRepository;

	@PostConstruct
	@Transactional
	public void init() {
		if (!adherentRepository.findAll().isEmpty()) {
			return;
		}

		// Create an adherent and 2 Adhesions
		Adherent adherent = Adherent.builder().prenom("Goulven").nom("Le Breton").build();
		Adhesion firstAdhesion = Adhesion.builder().typeAdhesion(TypeAdhesion.Simple).paymentType(PaymentType.Cash)
				.adherent(adherent).dateAdhesion(LocalDate.now().minusMonths(10)).build();
		Adhesion secondAdhesion = Adhesion.builder().typeAdhesion(TypeAdhesion.Simple).paymentType(PaymentType.Check)
				.adherent(adherent).dateAdhesion(LocalDate.now().minusDays(5)).build();

		adherent.addAdhesion(firstAdhesion);
		adherent.addAdhesion(secondAdhesion);
		adherentRepository.save(adherent);

		// Create providers and articles
		if (!providerRepository.findAll().isEmpty()) {
			return;
		}

		Provider p1 = Provider.builder().name("Chain Reaction Cycles").build();
		Provider p2 = Provider.builder().name("Probike Shop").build();
		providerRepository.saveAll(of(p1, p2));

		List<Article> articles = new ArrayList<>(5);
		articles.add(Article.builder().name("Dérailleur arrière Shimano Tiagra 4700 10v").salePrice(3199).quantity(15)
				.provider(p1).unitPrice(2599).reference("11111").stockWarningLevel(5).build());
		articles.add(Article.builder().name("Cassette Route Shimano Ultegra 6800 11 vitesses").salePrice(5399).quantity(8)
				.provider(p1).unitPrice(4599).reference("22222").stockWarningLevel(5).build());
		articles.add(Article.builder().name("Frein Shimano Dura-Ace 9000").salePrice(11799).quantity(3).provider(p2)
				.unitPrice(11000).reference("3333").stockWarningLevel(5).build());
		articles.add(Article.builder().name("Pneu Route Continental Grand Prix 4000S II - 23c PAIR").salePrice(6999)
				.provider(p2).unitPrice(5150).quantity(25).reference("44444").stockWarningLevel(5).build());
		articles.add(Article.builder().name("Cable de frein").quantity(47).stockWarningLevel(5).build());
		articleRepository.saveAll(articles);

		// Enregistrement des ventes
		Random random = new Random();
		int nbSales = 200;
		for (int i = 0; i != nbSales; ++i) {
			Sale sale = Sale.builder().adherent(adherent).paymentType(PaymentType.Cash)
					.createdAt(DateTime.now().minusMonths(random.nextInt(15))).finished(true).build();
			Article article = articles.get(random.nextInt(articles.size() - 1));
			sale.addSoldItem(article, 1, article.getSalePrice());
			saleRepository.save(sale);
		}
	}
}
