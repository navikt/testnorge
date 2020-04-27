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
		initialValuesMal.inntektsmelding.inntekter = getUpdatedInntektsmeldingData(
			initialValuesMal.inntektsmelding.inntekter
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
	let newInntektstubData = Object.assign({}, inntektstubData)
	newInntektstubData.inntektsinformasjon = newInntektstubData.inntektsinformasjon.map(inntekt =>
		updateData(inntekt, initialValues.inntektstub)
	)
	return newInntektstubData
}

const getUpdatedAaregData = aaregData =>
	aaregData.map(data => {
		data = updateData(data, initialValues.aareg[0])
		data.permisjon = data.permisjon.map(data => updateData(data, initialValues.permisjon))
		data.utenlandsopphold = data.utenlandsopphold.map(data =>
			updateData(data, initialValues.utenlandsopphold)
		)
		return data
	})
const getUpdatedInntektsmeldingData = inntektsmeldingData =>
	inntektsmeldingData.map(inntekt => updateData(inntekt, initialValues.inntektsmelding))

const getUpdatedInstData = instData =>
	instData.map(data => updateData(data, initialValues.instdata))

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
	var newTpsfData = Object.assign({}, tpsfData)
	if (tpsfData.statsborgerskap) {
		newTpsfData = updateData(newTpsfData, initialValues.statborgerskap)
	}
	if (tpsfData.innvandretFraLand) {
		newTpsfData = updateData(newTpsfData, initialValues.innvandretFra)
	}
	if (tpsfData.utvandretTilLand) {
		newTpsfData = updateData(newTpsfData, initialValues.utvandretTil)
	}
	if (tpsfData.relasjoner) {
		if (tpsfData.relasjoner.partnere) {
			newTpsfData.relasjoner.partnere = newTpsfData.relasjoner.partnere.map(partner =>
				updateData(partner, initialValues.partnere)
			)
		}
		if (tpsfData.relasjoner.barn) {
			newTpsfData.relasjoner.barn = newTpsfData.relasjoner.barn.map(barn =>
				updateData(barn, initialValues.barn)
			)
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
