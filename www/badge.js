var Badge = function () {
};

Badge.prototype.errorCallback = function (msg) {
    console.log('JPush Callback Error: ' + msg)
}

Badge.prototype.callNative = function (name, args, successCallback, errorCallback) {
    if (errorCallback) {
        cordova.exec(successCallback, errorCallback, 'Badge', name, args)
    } else {
        cordova.exec(successCallback, this.errorCallback, 'Badge', name, args)
    }
};

Badge.prototype.setAndroidIconBadgeNumber = function (badge, callback) {
    var args = [parseInt(badge) || 0];
    this.callNative('set', args, callback)
};

Badge.prototype.getAndroidIconBadgeNumber = function (callback) {
    this.callNative('get', [], callback)
};

Badge.prototype.clearAndroidIconBadgeNumber = function (callback) {
    this.callNative('clear', [], callback)
};

if (!window.plugins) {
    window.plugins = {}
}

if (!window.plugins.Badge) {
    window.plugins.Badge = new Badge()
}

module.exports = new Badge();
