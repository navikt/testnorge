import AttributtManager from '../AttributtManager'

const AttributtManagerInstance = new AttributtManager()

describe('listAllSelected', () => {
	it('should return attributes based on selectedIds', () => {
		const list = AttributtManagerInstance.listAllSelected(['kjonn', 'statsborgerskap'])
		expect(list.length).toEqual(2)
	})

	it('should return boaddresse + all children', () => {
		const list = AttributtManagerInstance.listAllSelected(['boadresse'])
		expect(list.length).toBeGreaterThan(1)
	})
})

describe('listSelectedExcludingChildren', () => {
	it('should return attribute excluding children', () => {
		const list = AttributtManagerInstance.listSelectedExcludingChildren(['boadresse'])
		expect(list.length).toEqual(1)
	})
})
