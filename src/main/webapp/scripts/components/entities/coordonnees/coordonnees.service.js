'use strict';

angular.module('membershipApp')
    .factory('Coordonnees', function ($resource) {
        return $resource('api/coordonneess/:id', {}, {
            'query': { method: 'GET', isArray: true},
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
