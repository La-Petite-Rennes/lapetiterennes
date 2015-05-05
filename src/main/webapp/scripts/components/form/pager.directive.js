/* globals $ */
'use strict';

angular.module('membershipApp')
    .directive('membershipAppPager', function() {
        return {
            templateUrl: 'scripts/components/form/pager.html'
        };
    });
