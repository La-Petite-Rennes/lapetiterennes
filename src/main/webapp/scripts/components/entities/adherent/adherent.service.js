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
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
