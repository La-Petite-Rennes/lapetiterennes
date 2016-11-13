'use strict'

angular.module('membershipApp')
	.controller('SaleHistoryController', function ($scope) {
		$scope.weekSales = [{
			"label": "Dérailleur arrière Shimano Tiagra 4700 10v",
			"unitPrice" : 3199,
			"nbSales": 4,
			"totalPrice": 12796
		},{
			"label": "Cassette Route Shimano Ultegra 6800 11 vitesses",
			"unitPrice" : 5399,
			"nbSales": 2,
			"totalPrice": 10798
		}];
		$scope.monthSales = [{
			"label": "Dérailleur arrière Shimano Tiagra 4700 10v",
			"unitPrice" : 3199,
			"nbSales": 4,
			"totalPrice": 12796
		},{
			"label": "Cassette Route Shimano Ultegra 6800 11 vitesses",
			"unitPrice" : 5399,
			"nbSales": 2,
			"totalPrice": 10798
		},{
			"label": "Frein Shimano Dura-Ace 9000",
			"unitPrice" : 11799,
			"nbSales": 7,
			"totalPrice": 82593
		},{
			"label": "Pneu Route Continental Grand Prix 4000S II - 23c PAIR",
			"unitPrice" : 6999,
			"nbSales": 15,
			"totalPrice": 104995
		}];
		
		$scope.history = [];
		$scope.reverse = false;
		$scope.propertyName= 'name';
	
		$scope.loadAll = function() {
			$scope.weekHistory();
		};
		
		$scope.weekHistory = function() {
			$('#weekHistory').addClass('btn-primary');
			$('#monthHistory').removeClass('btn-primary');
			
			selectHistory($scope.weekSales);
		};
		
		$scope.monthHistory = function() {
			$('#monthHistory').addClass('btn-primary');
			$('#weekHistory').removeClass('btn-primary');
			
			selectHistory($scope.monthSales);
		};
		
		var selectHistory = function(history) {
			$scope.history = history;
		};
		
		$scope.totalPrice = function() {
			var total = 0;
			for (var index in $scope.history) {
				total += $scope.history[index].totalPrice;
			}
			return total;
		};
		
		$scope.sortBy = function(propertyName) {
			$scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
			$scope.propertyName = propertyName;
		};
		
		$scope.toEuros = function(priceInCent) {
			return parseInt(priceInCent / 100) + "," + (priceInCent % 100);
		}
		
		$scope.loadAll();
		
	});
