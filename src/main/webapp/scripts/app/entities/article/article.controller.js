'use strict'

angular.module('membershipApp')
	.controller('ArticleController', function ($scope, Article) {
		$scope.articles = [];
		$scope.reverse = false;
		$scope.propertyName= 'name';
		
		$scope.reassort = {
			waitingConfiration: false,
			items: []
		};
		
		$scope.loadAll = function() {
			Article.query(function(result) {
				$scope.articles = result;
				$scope.reverse = false;
				$scope.propertyName= 'name';
				$scope.clearReassort();
			})
		};
		
		$scope.stockLevel = function(article) {
			if (article.quantity <= 0) {
				return 'article-outOfStock';
			} else if (article.quantity <= 5) {
				return 'article-insufficientStock';
			} else {
				return 'article-sufficientStock';
			}
		};
		
		$scope.clearReassort = function() {
			$scope.reassort = {
				waitingConfiration: false,
				items: []
			};
			
			for (var article of $scope.articles) {
				$scope.reassort.items.push({
					id: article.id,
					name: article.name,
					quantity: 0
				});
			}
		}
		
		$scope.saveReassort = function() {
			if (!$scope.reassort.waitingConfirmation) {
				$scope.reassort.waitingConfirmation = true;
			} else {
				Article.reassort($scope.reassort.items, function() {
					$('#reassortModal').modal('hide');
					$scope.loadAll();
				});
			}
		}
		
		$scope.sortBy = function(propertyName) {
			$scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
			$scope.propertyName = propertyName;
		};
		
		// TODO A extraire dans une directive
		$scope.toEuros = function(price) {
			return parseInt(price / 100) + "," + (price % 100);
		};
		
		$scope.forRepairing = function(article) {
			Article.forRepairing({articleId: article.id}, function(updatedArticle) {
				article.quantity = updatedArticle.quantity;
			});
		}
		
		$scope.loadAll();
		
	});
