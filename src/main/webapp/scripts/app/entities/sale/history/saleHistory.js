'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
    	$stateProvider
    		.state('saleHistory', {
    			parent: 'entity',
    			url: '/saleHistory',
    			data: {
    				roles: ['ROLE_USER'],
    				pageTitle: 'membershipApp.saleHistory.home.title'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'scripts/app/entities/sale/history/saleHistory.html',
    					controller: 'SaleHistoryController'
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