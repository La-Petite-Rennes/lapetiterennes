'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('adresse', {
                parent: 'entity',
                url: '/adresse',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.adresse.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/adresse/adresses.html',
                        controller: 'AdresseController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adresse');
                        return $translate.refresh();
                    }]
                }
            })
            .state('adresseDetail', {
                parent: 'entity',
                url: '/adresse/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.adresse.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/adresse/adresse-detail.html',
                        controller: 'AdresseDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('adresse');
                        return $translate.refresh();
                    }]
                }
            });
    });
