'use strict';

angular.module('membershipApp')
    .factory('Provider', function ($resource) {
        return $resource('api/providers/:id', {}, {});
    });
