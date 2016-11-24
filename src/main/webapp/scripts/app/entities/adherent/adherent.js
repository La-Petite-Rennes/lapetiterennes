'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('adherent', {
                parent: 'entity',
                url: '/adherent',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.adherent.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/adherent/adherents.html',
                        controller: 'AdherentController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adherent');
                        $translatePartialLoader.addPart('adhesion');
                        $translatePartialLoader.addPart('coordonnees');
                        $translatePartialLoader.addPart('payment');
                        return $translate.refresh();
                    }]
                }
            })
            .state('adherentDetail', {
                parent: 'entity',
                url: '/adherent/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.adherent.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/adherent/adherent-detail.html',
                        controller: 'AdherentDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adherent');
                        $translatePartialLoader.addPart('adhesion');
                        return $translate.refresh();
                    }]
                }
            })
            .state('adherentExport', {
            	parent: 'entity',
            	url: '/adherents/export',
            	data: {
            		roles: ['ROLE_ADMIN'],
            		pageTitle: 'membershipApp.adherent.export.title'
            	},
            	views: {
            		'content@': {
            			 templateUrl: 'scripts/app/entities/adherent/adherent-export.html',
                         controller: 'AdherentExportController'
            		}
            	},
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adherent');
                        return $translate.refresh();
                    }]
                }
            });
    });
