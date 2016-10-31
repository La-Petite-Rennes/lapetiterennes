'use strict'

angular.module('membershipApp')
	.controller('NewSaleController', function ($scope, Article) {
		$scope.basket = {
			items: []
		};
		$scope.articles = [];
		$scope.newItem = {
			id: 0,
			quantity: 1
		};
		
		$scope.loadAll = function() {
			Article.query(function(result) {
				$scope.articles = result;
			})
		};
		
		$scope.addItem = function() {
			var basketItem = getBasketItem($scope.newItem.id);
			if (basketItem === null) {
				$scope.basket.items.push($scope.newItem);
			} else {
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
				totalCost += $scope.getArticle(item.id).price * item.quantity;
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
		
		$scope.loadAll();
	});
