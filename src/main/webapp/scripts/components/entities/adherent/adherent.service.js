'use strict';

angular.module('membershipApp')
    .factory('Adherent', function ($resource) {
        return $resource('api/adherents/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'search': {
            	method: 'GET',
            	url: 'api/adherents/search',
            	isArray: true
            },
            'get': {
                method: 'GET'
            },
            'update': { 
            	method:'PUT',
            	transformRequest: function (adherent) {
            		var data = angular.copy(adherent);
            	
            		if (data.adhesions !== undefined) {
	            		data.adhesions.forEach(function(ad) {
	            			if (ad.dateAdhesion instanceof Date) {
	            				ad.dateAdhesion = ad.dateAdhesion.getDate() + '/' + (ad.dateAdhesion.getMonth() + 1) + '/' + ad.dateAdhesion.getFullYear();
	            			}
	            			ad.price = ad.price * 100;
	            		});
            		}
            		return angular.toJson(data);
            	}
            }
        });
    });
