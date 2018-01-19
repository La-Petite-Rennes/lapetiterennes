'use strict';

angular.module('membershipApp')
	.factory('TemporarySales', function($q, $rootScope, $timeout, Basket, Sale, Principal) {

		// Constants
		var RECONNECT_TIMEOUT = 30000;
		var SOCKET_URL = "/lpr-websocket";
		var TEMPORARY_SALES_TOPIC = "/topic/temporarySales";
		var DELETE_SALES_TOPIC = "/topic/deletedSale";
		var FINISHED_SALE_TOPIC = "/topic/finishedSale";

		// Fonctions WebSocket
		var socket = {
			client: null,
			stomp: null
		};
		var listener = $q.defer();

		// Constructeur
		function TemporarySales() {
		}

		// List of temporary sales
		TemporarySales.baskets = [];

		var saleReceived = function(data) {
			var temporarySale = Basket.fromJson(JSON.parse(data));

			var alreadyExists = false;
			for (var index in TemporarySales.baskets) {
				if (TemporarySales.baskets[index].id === temporarySale.id) {
					TemporarySales.baskets[index] = temporarySale;
					alreadyExists = true;
				}
			}

			if (!alreadyExists) {
				TemporarySales.baskets.push(temporarySale);
			}

			// Refresh the scope to apply the modiciation in the view
			$rootScope.$apply();
		};

		var deletedSale = function(saleId) {
			var index = TemporarySales.baskets.findIndex(function(basket) {
				return basket.id === parseInt(saleId);
			});

			if (index !== -1) {
				TemporarySales.baskets.splice(index, 1);
			}

			// Refresh the scope to apply the modiciation in the view
			$rootScope.$apply();
		}

		var reconnect = function() {
			$timeout(function() {
				initialize();
			}, RECONNECT_TIMEOUT);
		};

		var startListener = function() {
			socket.stomp.subscribe(TEMPORARY_SALES_TOPIC, function(data) {
				listener.notify(saleReceived(data.body));
			});
			socket.stomp.subscribe(DELETE_SALES_TOPIC, function(data) {
				listener.notify(deletedSale(data.body));
			});
			socket.stomp.subscribe(FINISHED_SALE_TOPIC, function(data) {
				listener.notify(deletedSale(data.body));
			});
		};

		var initialize = function() {
			$rootScope.$watch(Principal.isAuthenticated, function(newValue, oldValue) {
				if (newValue) {
					loadSavedTemporaryBaskets();
				} else {
					TemporarySales.baskets = [];
				}
			});

			socket.client = new SockJS(SOCKET_URL);
			socket.stomp = Stomp.over(socket.client);
			socket.stomp.connect({}, startListener);
			socket.stomp.onclose = reconnect;
	    };

	    var loadSavedTemporaryBaskets = function() {
	    	Sale.temporary(function(results) {
				angular.forEach(results, function (sale) {
					TemporarySales.baskets.push(Basket.fromJson(sale));
			    });
			});
	    }

	    initialize();

	    return TemporarySales;

	});
