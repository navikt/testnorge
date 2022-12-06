import { initialValues } from './utils'
import _ from 'lodash'
import { filterMiljoe } from '@/components/miljoVelger/MiljoeInfo'
import {
	BostedData,
	KontaktadresseData,
	OppholdsadresseData,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ForeldreBarnRelasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

export const initialValuesBasedOnMal = (mal: any) => {
	const { dollyEnvironments } = useDollyEnvironments()
	const initialValuesMal = Object.assign({}, mal.bestilling)

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
		initialValuesMal.tpsf = null
	}
	if (initialValuesMal.udistub) {
		initialValuesMal.udistub = getUpdatedUdistubData(initialValuesMal.udistub)
	}
	if (initialValuesMal.pdldata) {
		initialValuesMal.pdldata = getUpdatedPdldata(initialValuesMal.pdldata)
	}
	if (initialValuesMal.skjerming) {
		initialValuesMal.tpsMessaging = initialValuesMal.skjerming
	}
	if (initialValuesMal.bankkonto) {
		initialValuesMal.bankkonto = getUpdatedBankkonto(initialValuesMal.bankkonto)
	}

	initialValuesMal.environments = filterMiljoe(dollyEnvironments, mal.bestilling.environments)
	return initialValuesMal
}

const getUpdatedInntektstubData = (inntektstubData: any) => {
	const newInntektstubData = Object.assign({}, inntektstubData)
	newInntektstubData.inntektsinformasjon = newInntektstubData.inntektsinformasjon?.map(
		(inntekt: any) => updateData(inntekt, initialValues.inntektstub)
	)
	return newInntektstubData
}

const getUpdatedAaregData = (aaregData: any) => {
	return aaregData.map((data: any) => {
		data = updateData(data, initialValues.aareg[0])
		data.permisjon = data.permisjon?.map((permisjon: any) =>
			updateData(permisjon, initialValues.permisjon)
		)
		data.utenlandsopphold = data.utenlandsopphold?.map((opphold: any) =>
			updateData(opphold, initialValues.utenlandsopphold)
		)
		return data
	})
}
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

const getUpdatedPdldata = (pdldata: any) => {
	const newPdldata = Object.assign({}, pdldata)
	const nyPerson = newPdldata?.opprettNyPerson
	if (nyPerson) {
		if (nyPerson.alder === null && nyPerson.foedtFoer === null && nyPerson.foedtEtter === null) {
			newPdldata.opprettNyPerson = {
				identtype: nyPerson.identtype,
				syntetisk: nyPerson.syntetisk,
			}
		}
	} else {
		newPdldata.opprettNyPerson = {}
	}
	const person = newPdldata?.person
	if (person?.bostedsadresse) {
		newPdldata.person.bostedsadresse = person.bostedsadresse.map((adresse: BostedData) => {
			return updateAdressetyper(adresse, false)
		})
	}
	if (person?.oppholdsadresse) {
		newPdldata.person.oppholdsadresse = person.oppholdsadresse.map(
			(adresse: OppholdsadresseData) => {
				return updateAdressetyper(adresse, false)
			}
		)
	}
	if (person?.kontaktadresse) {
		newPdldata.person.kontaktadresse = person.kontaktadresse.map((adresse: KontaktadresseData) => {
			return updateAdressetyper(adresse, false)
		})
	}
	if (person?.forelderBarnRelasjon) {
		newPdldata.person.forelderBarnRelasjon = person.forelderBarnRelasjon.map(
			(relasjon: ForeldreBarnRelasjon) => {
				relasjon.typeForelderBarn = updateTypeForelderBarn(relasjon)
				if (relasjon.relatertPersonsRolle === 'BARN' && relasjon.deltBosted) {
					relasjon.deltBosted = updateAdressetyper(relasjon.deltBosted, true)
				}
				return relasjon
			}
		)
	}

	if (person?.kontaktinformasjonForDoedsbo) {
		newPdldata.person.kontaktinformasjonForDoedsbo = person.kontaktinformasjonForDoedsbo.map(
			(kontaktinfo: any) => updateKontaktType(kontaktinfo)
		)
	}
	return newPdldata
}

const updateAdressetyper = (adresse: any, deltBosted: boolean) => {
	if (adresse.vegadresse) {
		updateVegadressetype(adresse.vegadresse)
		adresse.adressetype = 'VEGADRESSE'
	} else if (adresse.matrikkeladresse) {
		adresse.adressetype = 'MATRIKKELADRESSE'
		adresse.matrikkeladresse.matrikkeladresseType = 'DETALJERT'
	} else if (adresse.utenlandskAdresse) {
		adresse.adressetype = 'UTENLANDSK_ADRESSE'
	} else if (adresse.ukjentBosted) {
		adresse.adressetype = 'UKJENT_BOSTED'
	} else if (adresse.oppholdAnnetSted) {
		adresse.adressetype = 'OPPHOLD_ANNET_STED'
	} else if (adresse.postboksadresse) {
		adresse.adressetype = 'POSTBOKSADRESSE'
	} else if (deltBosted) {
		adresse.adressetype = 'PARTNER_ADRESSE'
	}
	return adresse
}

const updateVegadressetype = (adresse: any) => {
	const notNullKeys = Object.keys(adresse).filter((key) => adresse[key] !== null)
	if (notNullKeys.length === 1 && notNullKeys.includes('kommunenummer')) {
		adresse.vegadresseType = 'KOMMUNENUMMER'
	} else if (notNullKeys.length === 1 && notNullKeys.includes('postnummer')) {
		adresse.vegadresseType = 'POSTNUMMER'
	} else if (notNullKeys.length === 1 && notNullKeys.includes('bydelsnummer')) {
		adresse.vegadresseType = 'BYDELSNUMMER'
	} else if (notNullKeys.length !== 0) {
		adresse.vegadresseType = 'DETALJERT'
	}
}

const updateTypeForelderBarn = (relasjon: ForeldreBarnRelasjon) => {
	if (relasjon.relatertPerson) {
		return 'EKSISTERENDE'
	} else if (relasjon.nyRelatertPerson) {
		return 'NY'
	} else if (relasjon.relatertPersonUtenFolkeregisteridentifikator) {
		return 'UTEN_ID'
	}
	return null
}

const getUpdatedBankkonto = (bankkonto: any) => {
	if (bankkonto.norskBankkonto) {
		delete bankkonto.utenlandskBankkonto
	} else {
		delete bankkonto.norskBankkonto
		for (let field of ['kontonummer', 'swift']) {
			if (!bankkonto.utenlandskBankkonto[field]) {
				bankkonto.utenlandskBankkonto[field] = ''
			}
		}
	}
	return bankkonto
}

const updateKontaktType = (kontaktinfo: any) => {
	if (kontaktinfo?.advokatSomKontakt) {
		kontaktinfo.kontaktType = 'ADVOKAT'
	} else if (kontaktinfo?.organisasjonSomKontakt) {
		kontaktinfo.kontaktType = 'ORGANISASJON'
	} else if (kontaktinfo?.personSomKontakt) {
		const person = kontaktinfo.personSomKontakt
		if (person.nyKontaktperson) {
			kontaktinfo.kontaktType = 'NY_PERSON'
		} else if (person.identifikasjonsnummer) {
			kontaktinfo.kontaktType = 'PERSON_FDATO'
			kontaktinfo.personSomKontakt.identifikasjonsnummer = null
		} else if (person.foedselsdato) {
			kontaktinfo.kontaktType = 'PERSON_FDATO'
		}
		kontaktinfo.personSomKontakt.navn = null
		delete kontaktinfo.personSomKontakt.eksisterendePerson
		delete kontaktinfo.personSomKontakt.nyKontaktperson
	}

	for (let field of ['personSomKontakt', 'advokatSomKontakt', 'organisasjonSomKontakt']) {
		if (!kontaktinfo[field]) {
			delete kontaktinfo[field]
		}
	}

	return kontaktinfo
}

const updateData = (data: any, initalValues: any) => {
	let newData = Object.assign({}, data)
	newData = _.extend({}, initalValues, newData)
	for (const key in initalValues) {
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
