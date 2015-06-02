'use strict';

angular.module('membershipApp')
    .controller('AdherentDetailController', function ($scope, $stateParams, Adherent, Coordonnees, Adhesion) {
    	// View Model
        $scope.adherent = {};
        
        // View functions
        $scope.load = function (id) {
            Adherent.get({id: id}, function(result) {
              $scope.adherent = result;
              loadAdhesions(id);
            });
        };
        
        $scope.load($stateParams.id);
        
        // Functions
        function loadAdhesions(adherentId) {
        	Adhesion.adherent({adherentId: adherentId}, function(result) {
        		$scope.adherent.adhesions = result;
        	});
        }
    });
