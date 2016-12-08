'use strict';

angular.module('membershipApp')
	.factory('Sale', function ($resource) {
		return $resource('api/sales/:id', {}, {
			'save' : { 
				method: 'POST' 
			}
		});
	});