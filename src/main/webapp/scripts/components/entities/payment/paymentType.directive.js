'use strict';

angular.module('membershipApp')
	.directive('paymentType', function() {
		return {
			restrict: 'E',
		    scope: {
		    	labelSize: '=labelSize',
		    	payment: '=model',
		    	waiting: '=waiting'
		    },
		    controller: function($scope){
		    	$scope.payment = angular.isDefined($scope.waiting) ? 'Waiting' : $scope.payment;
		    },
            templateUrl: 'scripts/components/entities/payment/paymentType.html'
        };
	});