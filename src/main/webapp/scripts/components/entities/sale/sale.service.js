'use strict';

angular.module('membershipApp')
	.factory('Sale', function ($resource) {
		return $resource('api/sales/:id', {}, {
			'save' : {
				method: 'POST'
			},
			'update': {
				method: 'PUT'
			},
			'statistics': {
				method: 'GET',
				url: 'api/sales/statistics/:year'
			},
			'get': {
				method: 'GET'
			},
			'history': {
				method: 'GET',
				url: 'api/sales/history',
				isArray: true
			},
			'temporary': {
				method: 'GET',
				url: 'api/sales/temporary',
				isArray: true
			}
		});
	});
