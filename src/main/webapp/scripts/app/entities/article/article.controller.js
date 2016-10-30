'use strict'

angular.module('membershipApp')
	.controller('ArticleController', function ($scope, Article) {
		$scope.articles = [];
		
		$scope.loadAll = function() {
			Article.query(function(result) {
				$scope.articles = result;
			})
		}
		
		$scope.loadAll();
		
	});
