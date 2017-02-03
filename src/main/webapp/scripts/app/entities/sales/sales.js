'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
    	$stateProvider
    		.state('sales', {
    			parent: 'entity',
    			url: '/sales',
    			data: {
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
    					templateUrl: 'scripts/app/entities/sales/statistics.html',
    					controller: 'SaleStatisticsController'
    				}
    			}
    		})
    });