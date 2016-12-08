'use strict'

// TODO Créer un objet NewSaleItem et Basket et y ajouter les fonctions Javascripts qui vont bien.
angular.module('membershipApp')
	.controller('NewSaleController', function ($scope, Sale, Article, Adherent) {
		$scope.basket = {
			date : new Date(),
			items: []
		};
		$scope.articles = [];
		$scope.searchAdherentCriteria;
		$scope.adherent = null;
		$scope.newItem = {
			id: 0,
			quantity: 1
		};
		
		$scope.loadAll = function() {
			Article.query(function(result) {
				$scope.articles = result;
			})
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
			$scope.basket.adherentId = $scope.adherent.id;
			
			$('#searchAdherentModal').modal('hide');
			$scope.clearSearchAdherent();
		};
		
		$scope.clearSearchAdherent = function() {
			$scope.searchAdherentCriteria = '';
			$scope.searchedAdherents = null;
		};
		
		$scope.addItem = function() {
			var basketItem = getBasketItem($scope.newItem.article.id);
			if (basketItem === null) {
				$scope.basket.items.push({
					id: $scope.newItem.article.id,
					quantity: $scope.newItem.quantity,
					price: newItemPrice($scope.newItem)
				});
			} else {
				basketItem.price = newItemPrice($scope.newItem);
				basketItem.quantity += $scope.newItem.quantity;
			}
			
			$scope.newItem = {
				quantity: 1
			};
		};
		
		var getBasketItem = function(articleId) {
			for (var index in $scope.basket.items) {
				var basketItem = $scope.basket.items[index];
				if (basketItem.id === articleId) {
					return basketItem;
				}
			}
			return null;
		}
		
		var newItemPrice = function(newItem) {
			var price;
			if ($scope.newItem.article.salePrice) {
				price = $scope.newItem.article.salePrice;
			} else { 
				price = parseInt($scope.newItem.freePrice * 100);
			}
			return price;
		}
		
		$scope.removeItem = function(item) {
			var index = $scope.basket.items.indexOf(item);
			if (index > -1) {
				$scope.basket.items.splice(index, 1);
			}
		}
		
		$scope.incrementQuantity = function(item) {
			item.quantity += 1;
		}
		
		$scope.decrementQuantity = function(item) {
			if (item.quantity === 1) {
				$scope.removeItem(item);
			} else {
				item.quantity -= 1;
			}
		}
		
		$scope.saleCost = function() {
			var totalCost = 0;
			
			for (var index in $scope.basket.items) {
				var item = $scope.basket.items[index];
				totalCost += item.price * item.quantity;
			};
			
			return $scope.toEuros(totalCost);
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
		
		$scope.saveSale = function() {
			Sale.save($scope.basket, function(result) {
				// TODO Clear all
			});
		}
		
		$scope.loadAll();
	});
