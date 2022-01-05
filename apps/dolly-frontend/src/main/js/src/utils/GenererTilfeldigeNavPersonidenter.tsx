const genererPersonident = () => {
	return `Z${Math.floor(Math.random() * 899999) + 100000}`
}

export const genererTilfeldigeNavPersonidenter = () => {
	let personidenter = []
	let numIterations = 0
	while (personidenter.length < 10 && numIterations < 100) {
		const personident = genererPersonident()
		personidenter.push({ value: personident, label: personident })
		numIterations++
	}
	return personidenter
}
