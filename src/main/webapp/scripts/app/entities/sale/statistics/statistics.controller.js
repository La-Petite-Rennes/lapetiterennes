'use strict'

angular.module('membershipApp')
	.controller('SaleStatisticsController', function ($scope, Sale) {
		$scope.salesByMonth = {};
		$scope.salesAmountByMonth = [];
		
		$scope.loadAll = function() {
			Sale.statistics(function(result) {
				$scope.salesByMonth = result.itemsByPeriod;
				computeSalesAmountByMonth();
			});
		};
		
		var computeSalesAmountByMonth = function() {
			$scope.salesAmountByMonth = [];
			for (var month in $scope.salesByMonth) {
				$scope.salesAmountByMonth.push({
					month: month,
					amount: $scope.salesByMonth[month].reduce(function(total, sale) { return total + sale.totalPrice }, 0)
				});
			}
		};
		
		$scope.loadAll();
	});
