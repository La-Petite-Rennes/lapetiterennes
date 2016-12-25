'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
    	$stateProvider
    		.state('saleStatistics', {
    			parent: 'entity',
    			url: '/saleStatistics',
    			data: {
    				roles: ['ROLE_ADMIN', 'ROLE_WORKSHOP_MANAGER'],
    				pageTitle: 'membershipApp.statistics.home.title'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'scripts/app/entities/sale/statistics/statistics.html',
    					controller: 'SaleStatisticsController'
    				}
    			},
    			resolve: {
    				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sale');
                        return $translate.refresh();
                    }]
    			}
    		});
    });