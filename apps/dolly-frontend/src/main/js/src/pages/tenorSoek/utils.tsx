import { codeToNorskLabel } from '@/utils/DataFormatter'

export const manualOptions = {
	DNummer: 'D-nummer',
	DNummerOgFoedselsnr: 'D-nummer og fødselsnummer',
	EndringIFoedselINorge: 'Endring i fødsel i Norge',
	endringIFoedselINorge: 'Endring i fødsel i Norge',
	EndringIOppholdPaaSvalbard: 'Endring i opphold på Svalbard',
	endringIOppholdPaaSvalbard: 'Endring i opphold på Svalbard',
}

export const createOptions = (optionsList: Array<string>, hasManualOptions = false) => {
	if (!optionsList || optionsList.length === 0) {
		return []
	}
	if (hasManualOptions) {
		return optionsList.map((option) => ({
			value: option,
			label: manualOptions[option] || codeToNorskLabel(option),
		}))
	}
	return optionsList.map((option) => ({
		value: option,
		label: codeToNorskLabel(option),
	}))
}
