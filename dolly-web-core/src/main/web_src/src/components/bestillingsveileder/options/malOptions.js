import { initialValues } from './utils'

export const initialValuesBasedOnMal = mal => {
	let initialValuesMal = Object.assign({}, mal.bestilling)
	if (initialValuesMal.inntektstub) {
		initialValuesMal.inntektstub = getUpdatedInntektstubData(initialValuesMal.inntektstub)
	}
	if (initialValuesMal.aareg) {
		initialValuesMal.aareg = getUpdatedAaregData(initialValuesMal.aareg)
	}
	if (initialValuesMal.inntektsmelding) {
		initialValuesMal.inntektsmelding = getUpdatedInntektsmeldingData(
			initialValuesMal.inntektsmelding
		)
	}
	if (initialValuesMal.inntektstub) {
		initialValuesMal.inntektstub = getUpdatedInntektstubData(initialValuesMal.inntektstub)
	}
	if (initialValuesMal.instdata) {
		initialValuesMal.instdata = getUpdatedInstData(initialValuesMal.instdata)
	}
	if (initialValuesMal.pdlforvalter) {
		initialValuesMal.pdlforvalter = getUpdatedPdlfData(initialValuesMal.pdlforvalter)
	}
	if (initialValuesMal.tpsf) {
		initialValuesMal.tpsf = getUpdatedTpsfData(initialValuesMal.tpsf)
	}
	if (initialValuesMal.udistub) {
		initialValuesMal.udistub = getUpdatedUdistubData(initialValuesMal.udistub)
	}

	return initialValuesMal
}

const getUpdatedInntektstubData = inntektstubData => {
	const newInntektstubData = Object.assign({}, inntektstubData)
	const inntektInfo = newInntektstubData.inntektsinformasjon
	for (let i = 0; i < inntektInfo.length; i++) {
		inntektInfo[i] = updateData(inntektInfo[i], initialValues.inntektstub)
	}
	return newInntektstubData
}

const getUpdatedAaregData = aaregData => {
	const newAaregData = Object.assign([], aaregData)

	for (let i = 0; i < newAaregData.length; i++) {
		newAaregData[i] = updateData(newAaregData[i], initialValues.aareg[0])
		const permisjon = newAaregData[i]['permisjon']
		const utenlandsopphold = newAaregData[i]['utenlandsopphold']
		if (permisjon.length > 0) {
			for (let j = 0; j < permisjon.length; j++) {
				permisjon[j] = updateData(permisjon[j], initialValues.permisjon)
			}
		}
		if (utenlandsopphold.length > 0) {
			for (let j = 0; j < utenlandsopphold.length; j++) {
				utenlandsopphold[j] = updateData(utenlandsopphold[j], initialValues.utenlandsopphold)
			}
		}
	}
	return newAaregData
}

const getUpdatedInntektsmeldingData = inntektsmeldingData => {
	const newInntektsmeldingData = Object.assign({}, inntektsmeldingData)
	const inntekter = newInntektsmeldingData.inntekter
	for (let i = 0; i < inntekter.length; i++) {
		inntekter[i] = updateData(inntekter[i], initialValues.inntektsmelding)
	}
	return newInntektsmeldingData
}

const getUpdatedInstData = instData => {
	const newInstData = Object.assign([], instData)
	for (let i = 0; i < newInstData.length; i++) {
		newInstData[i] = updateData(newInstData[i], initialValues.instdata)
	}
	return newInstData
}

const getUpdatedPdlfData = pdlfData => {
	const newPdlfData = Object.assign({}, pdlfData)
	if (pdlfData.kontaktinformasjonForDoedsbo) {
		newPdlfData.kontaktinformasjonForDoedsbo = updateData(
			newPdlfData.kontaktinformasjonForDoedsbo,
			initialValues.kontaktinformasjonForDoedsbo
		)
	}
	return newPdlfData
}

const getUpdatedTpsfData = tpsfData => {
	const newTpsfData = Object.assign({}, tpsfData)
	if(tpsfData.relasjoner){
		if (tpsfData.relasjoner.partnere) {
			console.log("test")
			for (let i = 0; i < tpsfData.relasjoner.partnere.length; i++) {
				newTpsfData.relasjoner.partnere[i] = updateData(
					newTpsfData.relasjoner.partnere[i],
					initialValues.partnere
				)
			}
		}
		if (tpsfData.relasjoner.barn) {
			for (let i = 0; i < tpsfData.relasjoner.barn.length; i++) {
				newTpsfData.relasjoner.barn[i] = updateData(
					newTpsfData.relasjoner.barn[i],
					initialValues.barn
				)
			}
		}
	}


	return newTpsfData
}

const getUpdatedUdistubData = udistubData => {
	const newUdistubData = Object.assign({}, udistubData)
	const oppholdStatus = udistubData.oppholdStatus
	if (oppholdStatus) {
		if (oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[0]
			)
		} else if (oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[1]
			)
		} else if (oppholdStatus.eosEllerEFTAOppholdstillatelse) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[2]
			)
		} else if (oppholdStatus.oppholdSammeVilkaar) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[3]
			)
		}
	}
	if (udistubData.arbeidsadgang && udistubData.arbeidsadgang.harArbeidsAdgang === 'JA') {
		newUdistubData.arbeidsadgang = updateData(
			newUdistubData.arbeidsadgang,
			initialValues.arbeidsadgang
		)
	}

	return newUdistubData
}

const updateData = (data, initalValues) => {
	var newData = Object.assign({}, data)
	newData = _.extend({}, initalValues, newData)
	for (let key in initalValues) {
		if (Array.isArray(initalValues[key])) {
			for (let i = 0; i < newData[key].length; i++) {
				newData[key][i] = updateData(newData[key][i], initalValues[key][0])
			}
		} else if (Object.prototype.toString.call(initalValues[key]) === '[object Object]') {
			newData[key] = updateData(newData[key], initalValues[key])
		}
	}
	return newData
}
