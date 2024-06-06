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
			label: hasManualOptions ? manualOptions[option] || codeToNorskLabel(option) : option,
		}))
	} else {
		return Object.entries(options).map(([key, value]) => ({
			value: key,
			label: value,
		}))
	}
}
