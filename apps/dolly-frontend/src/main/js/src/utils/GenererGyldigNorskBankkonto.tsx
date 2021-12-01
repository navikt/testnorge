const KONTROLL_SIFFER_1 = [3, 7, 6, 1, 8, 9, 4, 5, 2]
const KONTROLL_SIFFER_2 = [5, 4, 3, 2, 7, 6, 5, 4, 3, 2]

const kontrollsiffer = (kontonummer: string, kontrollsifferliste: number[]) => {
	let siffer = 0

	for (let i = 0; i < kontrollsifferliste.length; i++) {
		// @ts-ignore
		siffer += kontonummer.charAt(i) * kontrollsifferliste[i]
	}
	return (11 - (siffer % 11)) % 11
}

const createValidBankkontoNummer = () => {
	const bankkontoNrWithoutCheckDigits = `00${Math.floor(Math.random() * 8999999) + 1000000}`
	const bankkontoNrWithFirstCheckDigit = bankkontoNrWithoutCheckDigits.concat(
		String(kontrollsiffer(bankkontoNrWithoutCheckDigits, KONTROLL_SIFFER_1))
	)
	return bankkontoNrWithFirstCheckDigit.concat(
		String(kontrollsiffer(bankkontoNrWithFirstCheckDigit, KONTROLL_SIFFER_2))
	)
}

const gyldigKontonummerMod11 = (kontonummer: string) => {
	if (kontonummer.length !== 11) {
		return false
	} else {
		const sjekksiffer = parseInt(kontonummer.charAt(10), 10)
		const kontonummerUtenSjekksiffer = kontonummer.substring(0, 10)
		let sum = 0
		for (let index = 0; index < 10; index++) {
			sum += parseInt(kontonummerUtenSjekksiffer.charAt(index), 10) * KONTROLL_SIFFER_2[index]
		}
		const remainder = sum % 11
		return sjekksiffer === (remainder === 0 ? 0 : 11 - remainder)
	}
}

export const generateValidKontoOptions = () => {
	let kontoArray = []
	let numIterations = 0
	while (kontoArray.length < 10 && numIterations < 100) {
		const kontoNummer = createValidBankkontoNummer()
		if (gyldigKontonummerMod11(kontoNummer)) {
			kontoArray.push({ value: kontoNummer, label: kontoNummer })
		}
		numIterations++
	}
	return kontoArray
}
