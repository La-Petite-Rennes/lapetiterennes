'use strict';

angular.module('membershipApp')
	.config(function ($stateProvider) {
		$stateProvider
			.state('article' , {
				parent: 'entity',
				url: '/article',
				data: {
					roles: ['ROLE_ADMIN'],
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
			});
	});
