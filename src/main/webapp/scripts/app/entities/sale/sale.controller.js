'use strict'

angular.module('membershipApp')
	.controller('SaleController', function ($scope, $state, $stateParams, Sale, Article, Adherent, Basket) {
		$scope.articles = [];
		$scope.alerts = [];
		
		$scope.clearSale = function() {
			$scope.basket = new Basket();
			$scope.searchAdherentCriteria = null;
			$scope.adherent = null;
			$scope.newItem = {
				id: 0,
				quantity: 1
			};
		}
		
		$scope.load = function(saleId, alert) {
			Article.query(function(result) {
				$scope.articles = result;
			})
			
			if (saleId !== null && saleId !== "") {
				Sale.get({id: saleId}, function(result) {
					$scope.basket = Basket.fromJson(result);
					Adherent.get({id: $scope.basket.adherentId}, function(adherent) {
						$scope.adherent = adherent;
					})
				});
			}

			if (alert != null) {
				$scope.alerts.push(alert);
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
			$scope.adherent.fullName = $scope.adherent.prenom + ' ' + $scope.adherent.nom;
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
		
		$scope.getArticle = function(articleId) {
			for (var index in $scope.articles) {
				var article = $scope.articles[index];
				if (article.id === articleId) {
					return article;
				}
			}
			return null;
		}
		
		$scope.saveSale = function(finished) {
			$scope.basket.finished = finished;
			
			// If finished, create a new sale
			if ($scope.basket.id == null) {
				Sale.save($scope.basket, function(result) {
					// TODO Gérer les cas d'erreurs
					var alert = {
						type: 'success',
						message: 'La vente a été enregistrée avec succès'
					};
					$state.go('newSale', {id: null, alert: alert}, {reload: true});
				});
			} 
			// Otherwise, update the sale
			else {
				Sale.update($scope.basket, function(result) {
					// TODO Gérer les cas d'erreurs
					var alert = {
						type: 'success',
						message: 'La vente a été mise à jour avec succès'
					};
					$state.go('newSale', {id: null, alert: alert}, {reload: true});
				});
			}
		}
		
		$scope.canSaveSale = function() {
			return $scope.adherent !== null &&
				$scope.basket.paymentType !== null &&
				$scope.basket.items.length !== 0;
		}
		
		$scope.deleteSale = function() {
			if ($scope.basket.id === null) {
				return;
			}
			
			Sale.remove({id: $scope.basket.id}, function () {
				// TODO Gérer les cas d'erreurs
				var alert = {
					type: 'success',
					message: 'La vente a été supprimée avec succès'
				};
				$state.go('newSale', {id: null, alert: alert}, {reload: true});
			});
		}
		
		$scope.closeAlert = function() {
			$scope.alerts = [];
		}
		
		$scope.clearSale();
		$scope.load($stateParams.id, $stateParams.alert);
	});
