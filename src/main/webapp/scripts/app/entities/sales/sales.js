'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
    	$stateProvider
    		.state('sales', {
    			parent: 'entity',
    			url: '/sales',
    			data: {
    				roles: ['ROLE_ADMIN', 'ROLE_WORKSHOP_MANAGER'],
    				pageTitle: 'membershipApp.sales.home.title'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'scripts/app/entities/sales/sales.html'
    				}
    			},
    			resolve: {
    				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sale');
                        return $translate.refresh();
                    }]
    			}
    		})
    		.state('lastSales', {
    			parent: 'sales',
    			views: {
    				'sales': {
    					templateUrl: 'scripts/app/entities/sales/lastSales.html',
    					controller: 'LastSalesController'
    				}
    			}
    		})
    		.state('statistics', {
    			parent: 'sales',
    			views: {
    				'sales': {
    					templateUrl: 'scripts/app/entities/sale/statistics/statistics.html',
    					controller: 'SaleStatisticsController'
    				}
    			}
    		})
    });