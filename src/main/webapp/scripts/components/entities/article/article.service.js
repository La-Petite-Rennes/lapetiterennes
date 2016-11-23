'use strict';

angular.module('membershipApp')
    .factory('Article', function ($resource) {
        return $resource('api/articles/:id', {}, {
            'query': {
            	method: 'GET',
            	isArray: true
            },
            'get': {
                method: 'GET'
            },
            'update': { 
            	method: 'PUT'
            },
            'reassort': {
            	method: 'POST',
            	url: 'api/articles/reassort',
            	isArray: true
            }
        });
    });
