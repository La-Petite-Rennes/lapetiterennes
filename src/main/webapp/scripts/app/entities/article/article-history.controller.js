'use strict'

angular.module('membershipApp')
	.controller('ArticleHistoryController', function ($scope, $stateParams, Article, ParseLinks) {
		
		// View Model
		$scope.article = {};
		$scope.history = [];
		
		$scope.page = 1;
		
		// View Functions
		
		$scope.load = function(id) {
			$scope.articleId = id;
			Article.get({id: id}, function(result) {
				$scope.article = result;
			});
			
			$scope.loadPage($scope.page);
		}
		
		$scope.loadPage = function(page) {
            $scope.page = page;
			
			var historyQuery = {
					id: $scope.articleId,
					page: $scope.page,
					per_page: 20
			};
			
			Article.history(historyQuery, function(result, headers) {
				$scope.links = ParseLinks.parse(headers('link'));
				for (var i = 0; i < result.length; i++) {
                    $scope.history.push(result[i]);
                }
			});
        };
		
		$scope.load($stateParams.id);
		
	});