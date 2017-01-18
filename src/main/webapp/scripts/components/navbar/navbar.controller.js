'use strict'

angular.module('membershipApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal, TemporarySales) {
        
    	$scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        
        $scope.baskets = TemporarySales.baskets;
        
        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };
        
        $('[data-toggle="popover"]').popover({
        	container: 'body',
            html: true,
            content: function () {
                var clone = $($(this).data('popover-content')).clone(true).removeClass('hide');
                return clone;
            }
        }).click(function(e) {
            e.preventDefault();
        });
        
    });
