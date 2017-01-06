'use strict';

angular.module('membershipApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal, TemporarySales) {
        
    	$scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        
        $scope.baskets = TemporarySales.baskets;
        
        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };
        
        $('[data-toggle="popover"]').popover()
        
    });
