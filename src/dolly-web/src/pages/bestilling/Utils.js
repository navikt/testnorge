import { DollyApi } from '~/service/Api'

export const isPage = {
	first: page => page === 1,
	second: page => page === 2,
	last: page => page === 3
}

export const extraComponentProps = item => {
	switch (item.inputType) {
		case 'select': {
			if (item.apiKodeverkId) {
				return {
					loadOptions: () =>
						DollyApi.getKodeverkByNavn(item.apiKodeverkId).then(
							DollyApi.Utils.NormalizeKodeverkForDropdown
						)
				}
			} else {
				return {
					options: item.options
				}
			}
		}
		default:
			return {}
	}
}
