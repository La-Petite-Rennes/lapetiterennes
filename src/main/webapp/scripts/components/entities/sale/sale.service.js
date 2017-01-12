'use strict';

angular.module('membershipApp')
	.factory('Sale', function ($resource) {
		return $resource('api/sales/:id', {}, {
			'save' : { 
				method: 'POST' 
			},
			'statistics': {
				method: 'GET',
				url: 'api/sales/statistics'
			},
			'get': {
				method: 'GET'
			},
			'history': {
				method: 'GET',
				url: 'api/sales/history',
				isArray: true
			}
		});
	});