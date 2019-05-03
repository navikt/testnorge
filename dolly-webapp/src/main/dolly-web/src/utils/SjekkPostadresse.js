import { TpsfApi } from '~/service/Api'

export const sjekkPostadresse = async values => {
	let gyldigAdresse = true

	if (values['postLand'] === '' || values['postLand'] === 'NOR') {
		if (values['postLinje3']) {
			gyldigAdresse = await _sjekkPostnummer(values['postLinje3'])
		} else if (values['postLinje2']) {
			gyldigAdresse = await _sjekkPostnummer(values['postLinje2'])
		} else {
			gyldigAdresse = await _sjekkPostnummer(values['postLinje1'])
		}
	}
	return gyldigAdresse
}

const _sjekkPostnummer = async postnummer => {
	const regex = /^\d{4}(\s|$)/
	try {
		const res = await TpsfApi.checkPostnummer(postnummer)
		return regex.test(postnummer) && res.data.response.status.kode === '04'
	} catch (err) {
		console.log('err :', err)
	}
}
