import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { DollyApi, PdlforvalterApi } from '~/service/Api'

export const getPlaceholder = (values, selectedValuePath) => {
	const fornavn = _get(values, `${selectedValuePath}.fornavn`)
	let mellomnavn = _get(values, `${selectedValuePath}.mellomnavn`)
	const etternavn = _get(values, `${selectedValuePath}.etternavn`)

	mellomnavn = mellomnavn !== '' ? ' ' + mellomnavn : mellomnavn
	return !_isNil(fornavn) && fornavn !== '' ? fornavn + mellomnavn + ' ' + etternavn : 'Velg..'
}

export const setNavn = (navn, path, setFieldValue) => {
	if (!navn) {
		setFieldValue(path, null)
	} else {
		const deltNavn = navn.label.split(' ')
		setFieldValue(path, {
			fornavn: navn.value,
			mellomnavn: deltNavn.length === 3 ? deltNavn[1] : '',
			etternavn: deltNavn[deltNavn.length - 1],
		})
	}
}

export const setValue = (value, path, setFieldValue) => {
	setFieldValue(`${path}`, value.value)
}

export const getPersonOptions = async (gruppeId) => {
	const gruppe = await DollyApi.getGruppeById(gruppeId).then((response) => {
		return response.data?.identer?.map((person) => {
			if (person.master === 'PDL' || person.master === 'PDLF') return person.ident
		})
	})
	const options = await PdlforvalterApi.getPersoner(gruppe).then((response) => {
		const personListe = []
		response.data.forEach((id) => {
			personListe.push({
				value: id.person.ident,
				label: `${id.person.ident} - ${id.person.navn[0].fornavn} ${id.person.navn[0].etternavn}`,
			})
		})
		return personListe
	})
	return options ? options : Promise.resolve()
}
