'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('adhesion', {
                parent: 'entity',
                url: '/adhesion',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.adhesion.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/adhesion/adhesions.html',
                        controller: 'AdhesionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adhesion');
                        return $translate.refresh();
                    }]
                }
            })
            .state('adhesionDetail', {
                parent: 'entity',
                url: '/adhesion/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.adhesion.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/adhesion/adhesion-detail.html',
                        controller: 'AdhesionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adhesion');
                        return $translate.refresh();
                    }]
                }
            });
    });
