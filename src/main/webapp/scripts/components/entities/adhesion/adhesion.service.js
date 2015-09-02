'use strict';

angular.module('membershipApp')
    .factory('Adhesion', function ($resource) {
        return $resource('api/adhesions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    var dateAdhesionFrom = data.dateAdhesion.split("/");
                    data.dateAdhesion = new Date(Date.UTC(dateAdhesionFrom[2], dateAdhesionFrom[1] - 1, dateAdhesionFrom[0]));
                    return data;
                }
            },
            'adherent': {
            	method: 'GET',
            	url: 'api/adhesions/adherent/:adherentId',
            	isArray: true
            },
            'update': { method:'PUT' }
        });
    });
