window.mediator.subscribe('trackAddToCart', function(data) {
    if (data.productCode && data.quantity) {
        var profileTagElement = document.querySelector("body");
        var notifyProfileTag = new CustomEvent('notifyProfileTagAddToCart', {detail:data});
        profileTagElement.dispatchEvent(notifyProfileTag)
    }
});

window.mediator.subscribe('trackUpdateCart', function(data) {
    if (data.productCode && data.initialCartQuantity && data.newCartQuantity) {
        var profileTagElement = document.querySelector("body");
        var notifyProfileTag = new CustomEvent('notifyProfileTagUpdateCart', {detail:data});
        profileTagElement.dispatchEvent(notifyProfileTag)
    }
});

window.mediator.subscribe('trackRemoveFromCart', function(data) {
    if (data.productCode && data.initialCartQuantity) {
        var profileTagElement = document.querySelector("body");
        var notifyProfileTag = new CustomEvent('notifyProfileTagRemoveFromCart', {detail:data});
        profileTagElement.dispatchEvent(notifyProfileTag)
    }
});