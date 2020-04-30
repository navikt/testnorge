import { initialValues } from './utils'
import _ from 'lodash'

export const initialValuesBasedOnMal = (mal: any) => {
	let initialValuesMal = Object.assign({}, mal.bestilling)

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

const getUpdatedInntektstubData = (inntektstubData: any) => {
	let newInntektstubData = Object.assign({}, inntektstubData)
	newInntektstubData.inntektsinformasjon = newInntektstubData.inntektsinformasjon.map(
		(inntekt: any) => updateData(inntekt, initialValues.inntektstub)
	)
	return newInntektstubData
}

const getUpdatedAaregData = (aaregData: any) =>
	aaregData.map((data: any) => {
		data = updateData(data, initialValues.aareg[0])
		data.permisjon = data.permisjon.map((data: any) => updateData(data, initialValues.permisjon))
		data.utenlandsopphold = data.utenlandsopphold.map((data: any) =>
			updateData(data, initialValues.utenlandsopphold)
		)
		return data
	})
const getUpdatedInntektsmeldingData = (inntektsmeldingData: any) =>
	inntektsmeldingData.map((inntekt: any) => updateData(inntekt, initialValues.inntektsmelding))

const getUpdatedInstData = (instData: any) =>
	instData.map((data: any) => updateData(data, initialValues.instdata))

const getUpdatedPdlfData = (pdlfData: any) => {
	const newPdlfData = Object.assign({}, pdlfData)
	if (pdlfData.kontaktinformasjonForDoedsbo) {
		newPdlfData.kontaktinformasjonForDoedsbo = updateData(
			newPdlfData.kontaktinformasjonForDoedsbo,
			initialValues.kontaktinformasjonForDoedsbo
		)
	}
	return newPdlfData
}

const getUpdatedTpsfData = (tpsfData: any) => {
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
			newTpsfData.relasjoner.partnere = newTpsfData.relasjoner.partnere.map((partner: any) =>
				updateData(partner, initialValues.partnere)
			)
		}
		if (tpsfData.relasjoner.barn) {
			newTpsfData.relasjoner.barn = newTpsfData.relasjoner.barn.map((barn: any) =>
				updateData(barn, initialValues.barn)
			)
		}
	}
	if (tpsfData.boadresse) {
		newTpsfData.boadresse = updateData(newTpsfData.boadresse, initialValues.boadresse)
	}

	return newTpsfData
}

const getUpdatedUdistubData = (udistubData: any) => {
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

const updateData = (data: any, initalValues: any) => {
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
