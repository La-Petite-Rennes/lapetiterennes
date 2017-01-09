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
			var total = 0;

			for (var month in $scope.salesByMonth) {
				var amount = $scope.salesByMonth[month].reduce(function(total, sale) { return total + sale.totalPrice }, 0);
				$scope.salesAmountByMonth.push({
					month: new Date(month),
					amount: toEuros(amount)
				});
				total += amount;
			}
			
			$scope.total = toEuros(total);
		};
		
		var toEuros = function(price) {
			return parseInt(price / 100) + "," + (price % 100);
		};
		
		$scope.loadAll();
	});
