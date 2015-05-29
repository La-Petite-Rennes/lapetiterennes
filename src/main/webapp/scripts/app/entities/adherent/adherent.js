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
                        return $translate.refresh();
                    }]
                }
            });
    });
