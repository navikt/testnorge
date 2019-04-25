import { TpsfApi } from '~/service/Api'

const PostadresseSjekk = {}

PostadresseSjekk.sjekkPostadresse = async values => {
	let gyldigAdresse = false
	const regex = /^\d{4}(\s|$)/

	if (values['postLand'] === '' || values['postLand'] === 'NOR') {
		if (values['postLinje1'] && !values['postLinje2'] && !values['postLinje3']) {
			if (
				regex.test(values['postLinje1']) &&
				(await PostadresseSjekk.sjekkPostnummer(values['postLinje1'])) === '04'
			) {
				gyldigAdresse = true
			}
		} else if (values['postLinje1'] && values['postLinje2'] && !values['postLinje3']) {
			if (
				regex.test(values['postLinje2']) &&
				(await PostadresseSjekk.sjekkPostnummer(values['postLinje2'])) === '04'
			) {
				gyldigAdresse = true
			}
		} else if (values['postLinje1'] && values['postLinje2'] && values['postLinje3']) {
			if (
				regex.test(values['postLinje3']) &&
				(await PostadresseSjekk.sjekkPostnummer(values['postLinje3'])) === '04'
			) {
				gyldigAdresse = true
			}
		} else {
			gyldigAdresse = false
		}
	} else gyldigAdresse = true
	return gyldigAdresse
}

PostadresseSjekk.sjekkPostnummer = async postnummer => {
	let respons = await TpsfApi.checkPostnummer(postnummer)
	const status = respons.data.response.status.kode
	return status
}

export default PostadresseSjekk
