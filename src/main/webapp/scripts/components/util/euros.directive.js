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
	})	
	.directive('euros', function() {
		return {
			restrict: 'A',
			require: 'ngModel',
			link: function (scope, element, attrs, ctrl) {
				ctrl.$formatters.unshift(function(model) {
					return model / 100;
				});
				ctrl.$parsers.push(function(view) {
					return parseInt(view * 100);
				});
		    }
		};
	});