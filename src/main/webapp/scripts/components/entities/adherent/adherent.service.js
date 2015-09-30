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
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    var lastAdhesionFrom = data.lastAdhesion.split("-");
                    data.lastAdhesion = new Date(lastAdhesionFrom[2], lastAdhesionFrom[1] - 1, lastAdhesionFrom[0]);
                    return data;
                }
            },
            'update': { 
            	method:'PUT',
            	transformRequest: function (data) {
            		if (data.adhesions !== undefined) {
	            		data.adhesions.forEach(function(ad) {
	            			ad.dateAdhesion = ad.dateAdhesion.getDate() + '/' + (ad.dateAdhesion.getMonth() + 1) + '/' + ad.dateAdhesion.getFullYear();
	            		});
            		}
            		return angular.toJson(data);
            	}
            }
        });
    });
