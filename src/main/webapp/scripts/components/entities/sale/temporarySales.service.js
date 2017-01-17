'use strict';

angular.module('membershipApp')
	.factory('TemporarySales', function($q, $timeout, Basket, Sale) {
		
		// Constants
		var RECONNECT_TIMEOUT = 30000;
		var SOCKET_URL = "/lpr-websocket";
		var TEMPORARY_SALES_TOPIC = "/topic/temporarySales";
		var DELETE_SALES_TOPIC = "/topic/deletedSale";
		
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
		
		var getMessage = function(data) {
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
		};
		
		var deletedSale = function(saleId) {
			var index = TemporarySales.baskets.findIndex(function(basket) {
				return basket.id === parseInt(saleId);
			});
			
			if (index !== -1) {
				TemporarySales.baskets.splice(index, 1);
			}
		}
		
		var reconnect = function() {
			$timeout(function() {
				initialize();
			}, this.RECONNECT_TIMEOUT);
		};
		
		var startListener = function() {
			socket.stomp.subscribe(TEMPORARY_SALES_TOPIC, function(data) {
				listener.notify(getMessage(data.body));
			});
			socket.stomp.subscribe(DELETE_SALES_TOPIC, function(data) {
				listener.notify(deletedSale(data.body));
			});
		};
		
		var initialize = function() {
			Sale.temporary(function(result) {
				for (var sale of result) {
					TemporarySales.baskets.push(Basket.fromJson(sale));
				}
			});
			
			socket.client = new SockJS(SOCKET_URL);
			socket.stomp = Stomp.over(socket.client);
			socket.stomp.connect({}, startListener);
			socket.stomp.onclose = reconnect;
	    };
	    
	    initialize();
	    
	    return TemporarySales;
		
	});