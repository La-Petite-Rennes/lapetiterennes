/* globals $ */
'use strict';

angular.module('membershipApp')
    .directive('membershipAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
