'use strict';

angular.module('membershipApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
