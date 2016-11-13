'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
    	$stateProvider
    		.state('newSale', {
    			parent: 'entity',
    			url: '/newSale',
    			data: {
    				roles: ['ROLE_USER'],
    				pageTitle: 'membershipApp.newSale.home.title'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'scripts/app/entities/sale/newSale/newSale.html',
    					controller: 'NewSaleController'
    				}
    			},
    			resolve: {
    				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adherent');
                        $translatePartialLoader.addPart('sale');
                        return $translate.refresh();
                    }]
    			}
    		});
    });