'use strict';

angular.module('membershipApp')
    .controller('AdhesionController', function ($scope, Adhesion, Adherent) {
        $scope.adhesions = [];
        $scope.adherents = Adherent.query();
        $scope.loadAll = function() {
            Adhesion.query(function(result) {
               $scope.adhesions = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Adhesion.update($scope.adhesion,
                function () {
                    $scope.loadAll();
                    $('#saveAdhesionModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Adhesion.get({id: id}, function(result) {
                $scope.adhesion = result;
                $('#saveAdhesionModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Adhesion.get({id: id}, function(result) {
                $scope.adhesion = result;
                $('#deleteAdhesionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Adhesion.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAdhesionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.adhesion = {typeAdhesion: null, dateAdhesion: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
