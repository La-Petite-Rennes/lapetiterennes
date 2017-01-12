'use strict'

angular.module('membershipApp')
	.controller('LastSalesController', function ($scope, Sale, Basket, ParseLinks) {
		
		// View Model
		$scope.sales = [];
		$scope.page = 1;
		
		// View Functions
		
		$scope.saleItemsLabel = function(sale) {
			var label = sale.items.map(function(item) {
				return item.quantity + " * " + item.name;
			}).join(", ");
			
			if (label.length < 100) {
				return label;
			} else {
				return label.substring(0, 100) + "...";
			}
		};
		
		$scope.clear = function() {
			$scope.sales = [];
			$scope.page = 1;
		};
		
		$scope.loadPage = function(page) {
			$scope.page = page;
			
			var query = {
					page: $scope.page,
					per_page: 20
			};
			
			Sale.history(query, function(result, headers) {
				$scope.links = ParseLinks.parse(headers('link'));
				for (var i = 0; i < result.length; i++) {
                    $scope.sales.push(Basket.fromJson(result[i]));
                }
			});
		};
		
		$scope.loadAll = function() {
			$scope.clear();
			$scope.loadPage($scope.page);
		}
		
		$scope.loadAll();
		
	});
