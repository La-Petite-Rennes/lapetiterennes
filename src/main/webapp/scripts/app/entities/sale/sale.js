'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
    	$stateProvider
    		.state('sale', {
    			parent: 'entity',
    			url: '/sale/:id',
    			params: {
    				alert: null
    			},
    			data: {
    				roles: ['ROLE_ADMIN', 'ROLE_WORKSHOP_MANAGER'],
    				pageTitle: 'membershipApp.sale.update.title'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'scripts/app/entities/sale/sale.html',
    					controller: 'SaleController'
    				}
    			},
    			resolve: {
    				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adherent');
                        $translatePartialLoader.addPart('sale');
                        $translatePartialLoader.addPart('payment');
                        return $translate.refresh();
                    }]
    			}
    		})
    		.state('newSale', {
    			parent: 'sale',
    			url: '',
    			data: {
    				roles: ['ROLE_USER'],
    				pageTitle: 'membershipApp.sale.new.title'
    			},
    		});
    });