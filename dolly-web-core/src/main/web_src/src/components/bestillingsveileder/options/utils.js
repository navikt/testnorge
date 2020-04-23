import { initialValues as initialValuesInntektstub } from '~/components/fagsystem/inntektstub/form/Form'
import {initialValues as initialValuesAareg} from '~/components/fagsystem/aareg/form/initialValues'
import {initialTimeloennet, initialPermisjon, initialUtenlandsopphold} from '~/components/fagsystem/aareg/form/initialValues'
import {initialValues as initialValuesInntektsmelding} from '~/components/fagsystem/inntektsmelding/form/Form'
import _isNil from 'lodash/isNil'

export const initialValuesBasedOnMal = mal => {
	let initialValuesMal = Object.assign({}, mal.bestilling)
	if (!_isNil(initialValuesMal.inntektstub)) {
		initialValuesMal.inntektstub = getUpdatedInntektstubData(initialValuesMal.inntektstub)
	}
	if(!_isNil(initialValuesMal.aareg)){
		initialValuesMal.aareg = getUpdatedAaregData(initialValuesMal.aareg)
	}
	if(!_isNil(initialValuesMal.inntektsmelding)){
		initialValuesMal.inntektsmelding = getUpdatedInntektsmeldingData(initialValuesMal.inntektsmelding)
	}
	if(!_isNil(initialValuesMal.inntektstub)){
		initialValuesMal.inntektstub = getUpdatedInntektstubData(initialValuesMal.inntektstub)
	}


	return initialValuesMal
}

const getUpdatedInntektstubData = inntektstubData => {
	const newInntektstubData = Object.assign({}, inntektstubData)
	const inntektInfo = newInntektstubData.inntektsinformasjon
	for (let i = 0; i < inntektInfo.length; i++) {
		inntektInfo[i] = updateData(inntektInfo[i], initialValuesInntektstub)
	}
	return newInntektstubData
}

const getUpdatedAaregData = aaregData => {
	const newAaregData = Object.assign([], aaregData)

	for(let i=0; i<newAaregData.length;i++){
		newAaregData[i] = updateData(newAaregData[i], initialValuesAareg[0])
		const timeloennet = newAaregData[i]['antallTimerForTimeloennet']
		const permisjon = newAaregData[i]['permisjon']
		const utenlandsopphold = newAaregData[i]['utenlandsopphold']
		if(timeloennet.length>0){
			for(let j =0; j<timeloennet.length ; j++){
				timeloennet[j] = updateData(timeloennet[j], initialTimeloennet)
			}
		}
		if(permisjon.length>0){
			for(let j =0; j<permisjon.length ; j++){
				permisjon[j] = updateData(permisjon[j], initialPermisjon)
			}
		}
		if(utenlandsopphold.length>0){
			for(let j =0; j<utenlandsopphold.length ; j++){
				utenlandsopphold[j] = updateData(utenlandsopphold[j], initialPermisjon)
			}
		}
	}
	return newAaregData
}

const getUpdatedInntektsmeldingData = inntektsmeldingData => {
	const newInntektsmeldingData  = Object.assign({}, inntektsmeldingData )
	const inntekter = newInntektsmeldingData .inntekter
	for (let i = 0; i < inntekter.length; i++) {
		inntekter[i] = updateData(inntekter[i], initialValuesInntektsmelding)
	}
	return newInntektsmeldingData
}

const updateData = (data, initalValues) => {
	var newData = Object.assign({}, data)
	newData = _.extend({}, initalValues, newData)
	for (let key in initalValues) {
		if (Array.isArray(initalValues[key])) {
			for (let i = 0; i < newData[key].length; i++) {
				newData[key][i] = updateData(newData[key][i], initalValues[key][0])
			}
		}
		else if (Object.prototype.toString.call(initalValues[key]) === '[object Object]') {
			newData[key] = updateData(newData[key], initalValues[key])
		}
	}
	return newData
}
