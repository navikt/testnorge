import { initialValues as initialValuesInntektstub } from '~/components/fagsystem/inntektstub/form/Form'
import { initialValues as initialValuesAareg } from '~/components/fagsystem/aareg/form/initialValues'
import {
	initialPermisjon,
	initialUtenlandsopphold
} from '~/components/fagsystem/aareg/form/initialValues'
import { initialValues as initialValuesInntektsmelding } from '~/components/fagsystem/inntektsmelding/form/Form'
import { initialValues as initialValuesInst } from '~/components/fagsystem/inst/form/Form'

const initialValuesKontaktinfoForDoedsbo = {
	adressat: { adressatType: '' },
	adresselinje1: '',
	adresselinje2: '',
	postnummer: '',
	poststedsnavn: '',
	landkode: 'NOR',
	skifteform: '',
	utstedtDato: ''
}

const initialValuesArbeidsAdgang = {
	arbeidsOmfang: null,
	harArbeidsAdgang: 'JA',
	periode: {
		fra: null,
		til: null
	},
	typeArbeidsadgang: null
}

const initialValuesUdistub = [
	{
		eosEllerEFTABeslutningOmOppholdsrettPeriode: {
			fra: null,
			til: null
		},
		eosEllerEFTABeslutningOmOppholdsrettEffektuering: null,
		eosEllerEFTABeslutningOmOppholdsrett: ''
	},
	{
		eosEllerEFTAVedtakOmVarigOppholdsrettPeriode: {
			fra: null,
			til: null
		},
		eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering: null,
		eosEllerEFTAVedtakOmVarigOppholdsrett: ''
	},
	{
		eosEllerEFTAOppholdstillatelsePeriode: {
			fra: null,
			til: null
		},
		eosEllerEFTAOppholdstillatelseEffektuering: null,
		eosEllerEFTAOppholdstillatelse: ''
	},
	{
		oppholdSammeVilkaar: {
			oppholdSammeVilkaarPeriode: {
				fra: null,
				til: null
			},
			oppholdSammeVilkaarEffektuering: null,
			oppholdstillatelseVedtaksDato: null,
			oppholdstillatelseType: ''
		}
	}
]

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
	// if(!_isNil(initialValuesMal.tpsf)){
	// 	initialValuesMal.tpsf = getUpdatedTpsfData(initialValuesMal.tpsf)
	// }
	if(initialValuesMal.udistub){
		initialValuesMal.udistub = getUpdatedUdistubData(initialValuesMal.udistub)
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

	for (let i = 0; i < newAaregData.length; i++) {
		newAaregData[i] = updateData(newAaregData[i], initialValuesAareg[0])
		const permisjon = newAaregData[i]['permisjon']
		const utenlandsopphold = newAaregData[i]['utenlandsopphold']
		if (permisjon.length > 0) {
			for (let j = 0; j < permisjon.length; j++) {
				permisjon[j] = updateData(permisjon[j], initialPermisjon)
			}
		}
		if (utenlandsopphold.length > 0) {
			for (let j = 0; j < utenlandsopphold.length; j++) {
				utenlandsopphold[j] = updateData(utenlandsopphold[j], initialUtenlandsopphold)
			}
		}
	}
	return newAaregData
}

const getUpdatedInntektsmeldingData = inntektsmeldingData => {
	const newInntektsmeldingData = Object.assign({}, inntektsmeldingData)
	const inntekter = newInntektsmeldingData.inntekter
	for (let i = 0; i < inntekter.length; i++) {
		inntekter[i] = updateData(inntekter[i], initialValuesInntektsmelding)
	}
	return newInntektsmeldingData
}

const getUpdatedInstData = instData => {
	const newInstData = Object.assign([], instData)
	for (let i = 0; i < newInstData.length; i++) {
		newInstData[i] = updateData(newInstData[i], initialValuesInst)
	}
	return newInstData
}

const getUpdatedPdlfData = pdlfData => {
	const newPdlfData = Object.assign({}, pdlfData)
	if (pdlfData.kontaktinformasjonForDoedsbo) {
		newPdlfData.kontaktinformasjonForDoedsbo = updateData(
			newPdlfData.kontaktinformasjonForDoedsbo,
			initialValuesKontaktinfoForDoedsbo
		)
	}
	return newPdlfData
}

const getUpdatedUdistubData = udistubData => {
	const newUdistubData = Object.assign({}, udistubData)
	const oppholdStatus = udistubData.oppholdStatus
	if (oppholdStatus) {
		if (oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValuesUdistub[0]
			)
		} else if (oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValuesUdistub[1]
			)
		} else if (oppholdStatus.eosEllerEFTAOppholdstillatelse) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValuesUdistub[2]
			)
		} else if (oppholdStatus.oppholdSammeVilkaar) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValuesUdistub[3]
			)
		}
	}
	if (udistubData.arbeidsadgang && udistubData.arbeidsadgang.harArbeidsAdgang === 'JA') {
		newUdistubData.arbeidsadgang = updateData(
			newUdistubData.arbeidsadgang,
			initialValuesArbeidsAdgang
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
