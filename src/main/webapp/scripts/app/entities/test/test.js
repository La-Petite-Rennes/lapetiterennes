'use strict';

angular.module('membershipApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('test', {
                parent: 'entity',
                url: '/test',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.test.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/test/tests.html',
                        controller: 'TestController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('test');
                        return $translate.refresh();
                    }]
                }
            })
            .state('testDetail', {
                parent: 'entity',
                url: '/test/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'membershipApp.test.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/test/test-detail.html',
                        controller: 'TestDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('test');
                        return $translate.refresh();
                    }]
                }
            });
    });
