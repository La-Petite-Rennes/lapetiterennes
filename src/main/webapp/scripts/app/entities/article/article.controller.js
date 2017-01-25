'use strict'

angular.module('membershipApp')
	.controller('ArticleController', function ($scope, Article, Provider) {
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
			});
			
			Provider.query(function(result) {
				$scope.providers = result;
			});
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
			
			// TODO ES6 for (var article of $scope.articles) {
			for (var index in $scope.articles) {
				var article = $scope.articles[index];
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
		
		$scope.forRepairing = function(article) {
			Article.forRepairing({articleId: article.id}, function(updatedArticle) {
				article.quantity = updatedArticle.quantity;
			});
		};
		
		$scope.update = function(article) {
			$scope.newArticle = angular.copy(article);
			$('#articleModal').modal('show');
		}
		
		$scope.createArticle = function() {
			Article.save($scope.newArticle, function(result) {
				$('#articleModal').modal('hide');
				$scope.loadAll();
			});
		};
		
		$scope.clearArticleModal = function() {
			$scope.newArticle = {};
		};
		
		$scope.loadAll();
		
	});
