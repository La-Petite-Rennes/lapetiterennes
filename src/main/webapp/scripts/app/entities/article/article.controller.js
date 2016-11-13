'use strict'

angular.module('membershipApp')
	.controller('ArticleController', function ($scope, Article) {
		$scope.articles = [];
		$scope.reverse = false;
		$scope.propertyName= 'name';
		
		$scope.loadAll = function() {
			Article.query(function(result) {
				$scope.articles = result;
			})
		};
		
		$scope.stockLevel = function(article) {
			if (article.quantity === 0) {
				return 'article-outOfStock';
			} else if (article.quantity <= 5) {
				return 'article-insufficientStock';
			} else {
				return 'article-sufficientStock';
			}
		};
		
		$scope.sortBy = function(propertyName) {
			$scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
			$scope.propertyName = propertyName;
		};
		
		$scope.toEuros = function(price) {
			return parseInt(price / 100) + "," + (price % 100);
		};
		
		$scope.loadAll();
		
	});
