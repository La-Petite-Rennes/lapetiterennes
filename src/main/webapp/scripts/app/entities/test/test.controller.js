'use strict';

angular.module('membershipApp')
    .controller('TestController', function ($scope, Test, Adhesion) {
        $scope.tests = [];
        $scope.adhesions = Adhesion.query();
        $scope.loadAll = function() {
            Test.query(function(result) {
               $scope.tests = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Test.update($scope.test,
                function () {
                    $scope.loadAll();
                    $('#saveTestModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Test.get({id: id}, function(result) {
                $scope.test = result;
                $('#saveTestModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Test.get({id: id}, function(result) {
                $scope.test = result;
                $('#deleteTestConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Test.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTestConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.test = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
