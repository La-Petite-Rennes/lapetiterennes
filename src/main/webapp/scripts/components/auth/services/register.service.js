'use strict';

angular.module('membershipApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


