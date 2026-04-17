import { codeToNorskLabel } from '@/utils/DataFormatter'

export const manualOptions = {
	DNummer: 'D-nummer',
	DNummerOgFoedselsnr: 'D-nummer og fødselsnummer',
	EndringIFoedselINorge: 'Endring i fødsel i Norge',
	endringIFoedselINorge: 'Endring i fødsel i Norge',
	EndringIOppholdPaaSvalbard: 'Endring i opphold på Svalbard',
	endringIOppholdPaaSvalbard: 'Endring i opphold på Svalbard',
}

export const createOptions = (
	options: Array<string> | Map<string, string>,
	hasManualOptions = false,
) => {
	if (!options || (Array.isArray(options) && options.length === 0)) {
		return []
	}

	if (Array.isArray(options)) {
		return options.map((option) => ({
			value: option,
			label:
				hasManualOptions && manualOptions[option]
					? manualOptions[option]
					: codeToNorskLabel(option),
		}))
	} else {
		return Object.entries(options).map(([key, value]) => ({
			value: key,
			label: value,
		}))
	}
}

export const getInntektsaarOptions = (antallAar = 5) => {
	const inntektsaarListe = []
	const currentAar = new Date().getFullYear()
	for (let aar = currentAar - antallAar; aar < currentAar; aar++) {
		inntektsaarListe.push({ value: aar.toString(), label: aar.toString() })
	}
	return inntektsaarListe
}
