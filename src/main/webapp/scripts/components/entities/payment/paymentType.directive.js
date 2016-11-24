'use strict';

angular.module('membershipApp')
	.directive('paymentType', function() {
		return {
			restrict: 'E',
		    scope: {
		    	labelSize: '=labelSize'
		    },
            templateUrl: 'scripts/components/entities/payment/paymentType.html'
        };
	});