'use strict'

angular.module('membershipApp')
	.controller('NewSaleController', function ($scope, $stateParams, Sale, Article, Adherent, Basket) {
		$scope.articles = [];
		
		$scope.clearSale = function() {
			$scope.basket = new Basket();
			$scope.searchAdherentCriteria = null;
			$scope.adherent = null;
			$scope.newItem = {
				id: 0,
				quantity: 1
			};
		}
		
		$scope.loadAll = function(saleId) {
			Article.query(function(result) {
				$scope.articles = result;
			})
			
			if (saleId !== null) {
				Sale.get({id: saleId}, function(result) {
					$scope.basket = Basket.fromJson(result);
					// FIXME setAdherent
					// FIXME Récupérer nom article
				});
			}
		};
		
		$scope.openAdherentModal = function() {
			$('#searchAdherentModal').modal({ show: true, backdrop: 'static' });
		}
		
		$scope.searchAdherent = function() {
			var query = {
	    		page: $scope.page,
	    		per_page: 20,
	    		criteria: $scope.searchAdherentCriteria
	        };
	        	
	        Adherent.search(query, function(result, headers) {
	           	$scope.searchedAdherents = result;
	        });
		};
		
		$scope.selectAdherent = function(adherent) {
			$scope.adherent = adherent;
			$scope.adherent.name = $scope.adherent.prenom + ' ' + $scope.adherent.nom;
			$scope.basket.setAdherent($scope.adherent);
			
			$('#searchAdherentModal').modal('hide');
			$scope.clearSearchAdherent();
		};
		
		$scope.clearSearchAdherent = function() {
			$scope.searchAdherentCriteria = '';
			$scope.searchedAdherents = null;
		};
		
		$scope.addItem = function() {
			$scope.basket.addItem($scope.newItem.article, $scope.newItem.quantity, saleItemPrice($scope.newItem));
			$scope.newItem = {
				quantity: 1
			};
		};
		
		var saleItemPrice = function(newItem) {
			var price;
			if ($scope.newItem.article.salePrice) {
				price = $scope.newItem.article.salePrice;
			} else { 
				price = parseInt($scope.newItem.freePrice * 100);
			}
			return price;
		}
		
		$scope.saleCost = function() {
			return $scope.toEuros($scope.basket.totalCost());
		}
		
		$scope.getArticle = function(articleId) {
			for (var index in $scope.articles) {
				var article = $scope.articles[index];
				if (article.id === articleId) {
					return article;
				}
			}
			return null;
		}
		
		$scope.toEuros = function(priceInCent) {
			return parseInt(priceInCent / 100) + "," + (priceInCent % 100);
		}
		
		$scope.saveSale = function(finished) {
			$scope.basket.finished = finished;
			Sale.save($scope.basket, function(result) {
				// TODO Afficher un message de confirmation
				$scope.clearSale();
			});
		}
		
		$scope.clearSale();
		$scope.loadAll($stateParams.id);
	});
