'use strict';

angular.module('membershipApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('article' , {
				parent: 'entity',
				url: '/article',
				data: {
					roles: ['ROLE_ADMIN', 'ROLE_WORKSHOP_MANAGER'],
					pageTitle: 'membershipApp.article.home.title'
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/entities/article/articles.html',
						controller: 'ArticleController'
					}
				},
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('article');
                        return $translate.refresh();
                    }]
                }
			})
			.state('articleHistory', {
				parent: 'entity',
				url: '/article/:id/history',
				data: {
					roles: ['ROLE_ADMIN', 'ROLE_WORKSHOP_MANAGER'],
					pageTitle: 'membershipApp.article.history.title'
				},
				views: {
					'content@': {
						templateUrl: 'scripts/app/entities/article/article-history.html',
						controller: 'ArticleHistoryController'
					}
				},
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('article');
                        return $translate.refresh();
                    }]
                }
			});
	});
