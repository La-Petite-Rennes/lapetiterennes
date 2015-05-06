'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('coordonnees', {
                parent: 'entity',
                url: '/coordonnees',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.coordonnees.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/coordonnees/coordonneess.html',
                        controller: 'CoordonneesController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('coordonnees');
                        return $translate.refresh();
                    }]
                }
            })
            .state('coordonneesDetail', {
                parent: 'entity',
                url: '/coordonnees/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.coordonnees.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/coordonnees/coordonnees-detail.html',
                        controller: 'CoordonneesDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('coordonnees');
                        return $translate.refresh();
                    }]
                }
            });
    });
