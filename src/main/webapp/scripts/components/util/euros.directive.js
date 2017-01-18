'use strict';

angular.module('membershipApp')
	.directive('euros', function() {
		return {
			restrict: 'E',
			link: function (scope, element, attrs, ctrl) {
				scope.$watch(attrs.cents, function(newVal, oldVal) {
			        element.text((newVal / 100).toFixed(2) + ' €');
			    });
		    },
		};
	});