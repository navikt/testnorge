// For bruk i IE 11

;(function(arr) {
	arr.forEach(function(item) {
		if (item.hasOwnProperty('prepend')) {
			return
		}
		Object.defineProperty(item, 'prepend', {
			configurable: true,
			enumerable: true,
			writable: true,
			value: function prepend() {
				const argArr = Array.prototype.slice.call(arguments),
					docFrag = document.createDocumentFragment()
				argArr.forEach(function(argItem) {
					const isNode = argItem instanceof Node
					docFrag.appendChild(isNode ? argItem : document.createTextNode(String(argItem)))
				})
				this.insertBefore(docFrag, this.firstChild)
			}
		})
	})
})([Element.prototype, Document.prototype, DocumentFragment.prototype])
