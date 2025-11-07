import { initialValues } from './utils'
import * as _ from 'lodash-es'
import { filterMiljoe } from '@/components/miljoVelger/MiljoeInfo'
import {
	BostedData,
	KontaktadresseData,
	OppholdsadresseData,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import {
	Foreldreansvar,
	ForeldreBarnRelasjon,
	FullmaktValues,
	NyIdent,
	SivilstandData,
	VergemaalValues,
} from '@/components/fagsystem/pdlf/PdlTypes'
import { addDays, addMonths, isAfter, setDate, subYears } from 'date-fns'
import {
	initialArbeidsavtale,
	initialArbeidsgiverOrg,
	initialFartoy,
} from '@/components/fagsystem/aareg/form/initialValues'
import { initialValuesDetaljertSykemelding } from '@/components/fagsystem/sykdom/form/initialValues'
import { FullmaktHandling } from '@/components/fagsystem/fullmakt/FullmaktType'

export const initialValuesBasedOnMal = (mal: any, environments: any) => {
	const initialValuesMal = Object.assign({}, mal.bestilling)

	if (initialValuesMal.aareg) {
		initialValuesMal.aareg = getUpdatedAaregData(initialValuesMal.aareg)
	}
	if (initialValuesMal.inntektsmelding) {
		initialValuesMal.inntektsmelding.inntekter = getUpdatedInntektsmeldingData(
			initialValuesMal.inntektsmelding.inntekter,
		)
	}
	if (initialValuesMal.inntektstub) {
		initialValuesMal.inntektstub = getUpdatedInntektstubData(initialValuesMal.inntektstub)
	}
	if (initialValuesMal.skattekort) {
		initialValuesMal.skattekort = getUpdatedSkattekortData(initialValuesMal.skattekort)
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
	if (initialValuesMal.bankkonto) {
		initialValuesMal.bankkonto = getUpdatedBankkonto(initialValuesMal.bankkonto)
	}
	if (initialValuesMal.arbeidsplassenCV) {
		initialValuesMal.arbeidsplassenCV = getUpdatedArbeidsplassenData(
			initialValuesMal.arbeidsplassenCV,
		)
	}
	if (initialValuesMal.arenaforvalter) {
		initialValuesMal.arenaforvalter = getUpdatedArenaforvalterData(initialValuesMal.arenaforvalter)
	}
	if (initialValuesMal.pensjonforvalter?.alderspensjon) {
		initialValuesMal.pensjonforvalter.alderspensjon = getUpdatedAlderspensjonData(
			initialValuesMal.pensjonforvalter.alderspensjon,
		)
	}
	if (initialValuesMal.pensjonforvalter?.uforetrygd) {
		initialValuesMal.pensjonforvalter.uforetrygd = getUpdatedUforetrygdData(
			initialValuesMal.pensjonforvalter.uforetrygd,
		)
	}
	if (initialValuesMal.tpsMessaging?.norskBankkonto) {
		_.set(
			initialValuesMal,
			'bankkonto.norskBankkonto',
			initialValuesMal.tpsMessaging.norskBankkonto,
		)
		delete initialValuesMal.tpsMessaging.norskBankkonto
	}
	if (initialValuesMal.tpsMessaging?.utenlandskBankkonto) {
		_.set(
			initialValuesMal,
			'bankkonto.utenlandskBankkonto',
			initialValuesMal.tpsMessaging.utenlandskBankkonto,
		)
		delete initialValuesMal.tpsMessaging.utenlandskBankkonto
	}
	if (initialValuesMal.dokarkiv) {
		initialValuesMal.dokarkiv = getUpdatedDokarkiv(initialValuesMal.dokarkiv)
	}
	if (initialValuesMal.sykemelding?.syntSykemelding) {
		initialValuesMal.sykemelding.detaljertSykemelding = getUpdatedSykemelding(
			initialValuesMal.sykemelding.syntSykemelding,
		)
	}
	if (initialValuesMal.fullmakt) {
		initialValuesMal.fullmakt = getUpdatedFullmaktData(initialValuesMal.fullmakt)
	}

	initialValuesMal.environments = filterMiljoe(environments, mal.bestilling?.environments)
	return initialValuesMal
}

const getUpdatedFullmaktData = (fullmaktData) => {
	const newHandling = (handling) => {
		if (handling?.includes('SKRIV')) {
			return FullmaktHandling.lesOgSkriv
		}
		return FullmaktHandling.les
	}
	return fullmaktData?.map((fullmakt) => ({
		...fullmakt,
		omraade: fullmakt.omraade.map((omraade: any) => ({
			...omraade,
			handling: newHandling(omraade.handling),
		})),
	}))
}

const getUpdatedAlderspensjonData = (alderspensjonData) => {
	const newAlderspensjonData = Object.assign({}, alderspensjonData)
	if (!isAfter(new Date(newAlderspensjonData.iverksettelsesdato), new Date())) {
		newAlderspensjonData.iverksettelsesdato = setDate(addMonths(new Date(), 1), 1)
	}
	return newAlderspensjonData
}

const getUpdatedUforetrygdData = (uforetrygdData) => {
	const newUforetrygdData = Object.assign({}, uforetrygdData)
	if (!isAfter(new Date(newUforetrygdData.onsketVirkningsDato), new Date())) {
		newUforetrygdData.onsketVirkningsDato = setDate(addMonths(new Date(), 1), 1)
	}
	return newUforetrygdData
}

const getUpdatedArenaforvalterData = (arenaforvalterData) => {
	let filtrertArenaforvalterData = Object.assign({}, arenaforvalterData)
	if (_.isEmpty(filtrertArenaforvalterData.aap)) {
		delete filtrertArenaforvalterData.aap
	}
	if (_.isEmpty(filtrertArenaforvalterData.aap115)) {
		delete filtrertArenaforvalterData.aap115
	}
	if (_.isEmpty(filtrertArenaforvalterData.dagpenger)) {
		delete filtrertArenaforvalterData.dagpenger
	}
	if (_.isEmpty(filtrertArenaforvalterData.inaktiveringDato)) {
		delete filtrertArenaforvalterData.inaktiveringDato
	}
	return filtrertArenaforvalterData
}

const getUpdatedArbeidsplassenData = (arbeidsplassenData) => {
	return Object.fromEntries(
		Object.entries(arbeidsplassenData)?.filter((kategori) => {
			return (
				(kategori?.[0] === 'jobboensker' && kategori?.[1]) ||
				kategori?.[1]?.length > 0 ||
				_.isBoolean(kategori?.[1])
			)
		}),
	)
}

const getUpdatedInntektstubData = (inntektstubData: any) => {
	const newInntektstubData = Object.assign({}, inntektstubData)
	newInntektstubData.inntektsinformasjon = newInntektstubData.inntektsinformasjon?.map(
		(inntekt: any) => updateData(inntekt, initialValues.inntektstub),
	)
	return newInntektstubData
}

const getUpdatedAaregData = (aaregData: any) => {
	return aaregData.map((data: any) => {
		data = updateData(data, initialValues.aareg)
		data.amelding = undefined
		data.genererPeriode = undefined
		data.navArbeidsforholdPeriode = undefined
		if (!data.ansettelsesPeriode) {
			data.ansettelsesPeriode = {
				fom: subYears(new Date(), 20),
				tom: null,
				sluttaarsak: null,
			}
		}
		if (!data.arbeidsavtale) {
			data.arbeidsavtale = initialArbeidsavtale
		}
		if (!data.arbeidsgiver) {
			data.arbeidsgiver = initialArbeidsgiverOrg
		}
		if (data.fartoy?.length < 1) {
			if (data.arbeidsforholdstype === 'maritimtArbeidsforhold') {
				data.fartoy = initialFartoy
			} else data.fartoy = undefined
		}
		data.permisjon = data.permisjon?.map((permisjon: any) =>
			updateData(permisjon, initialValues.permisjon),
		)
		data.utenlandsopphold = data.utenlandsopphold?.map((opphold: any) =>
			updateData(opphold, initialValues.utenlandsopphold),
		)
		return data
	})
}

const getUpdatedInntektsmeldingData = (inntektsmeldingData: any) =>
	inntektsmeldingData.map((inntekt: any) => updateData(inntekt, initialValues.inntektsmelding))

const getUpdatedSkattekortData = (skattekortData: any) => {
	const newSkattekortData = Object.assign({}, skattekortData)
	newSkattekortData.arbeidsgiverSkatt = newSkattekortData.arbeidsgiverSkatt.map(
		(arbeidsgiver: any) => {
			const identifikator = Object.fromEntries(
				Object.entries(arbeidsgiver?.arbeidsgiveridentifikator)?.filter(([key, value]) => value),
			)
			_.set(arbeidsgiver, 'arbeidsgiveridentifikator', identifikator)
			const forskuddstrekk = arbeidsgiver?.arbeidstaker?.[0]?.skattekort?.forskuddstrekk?.map(
				(forskuddstrekk: any) =>
					Object.fromEntries(Object.entries(forskuddstrekk)?.filter(([key, value]) => value)),
			)
			_.set(arbeidsgiver, 'arbeidstaker[0].skattekort.forskuddstrekk', forskuddstrekk)
			return arbeidsgiver
		},
	)
	return newSkattekortData
}

const getUpdatedInstData = (instData: any) =>
	instData.map((data: any) => updateData(data, initialValues.instdata))

const getUpdatedPdlfData = (pdlfData: any) => {
	const newPdlfData = Object.assign({}, pdlfData)
	if (pdlfData.kontaktinformasjonForDoedsbo) {
		newPdlfData.kontaktinformasjonForDoedsbo = updateData(
			newPdlfData.kontaktinformasjonForDoedsbo,
			initialValues.kontaktinformasjonForDoedsbo,
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
				initialValues.udistub[0],
			)
		} else if (oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[1],
			)
		} else if (oppholdStatus.eosEllerEFTAOppholdstillatelse) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[2],
			)
		} else if (oppholdStatus.oppholdSammeVilkaar) {
			newUdistubData.oppholdStatus = updateData(
				newUdistubData.oppholdStatus,
				initialValues.udistub[3],
			)
		}
	}
	if (udistubData.arbeidsadgang && udistubData.arbeidsadgang.harArbeidsAdgang === 'JA') {
		newUdistubData.arbeidsadgang = updateData(
			newUdistubData.arbeidsadgang,
			initialValues.arbeidsadgang,
		)
	}

	return newUdistubData
}

const getUpdatedPdldata = (pdldata: any) => {
	const newPdldata = Object.assign({}, pdldata)
	const nyPerson = newPdldata?.opprettNyPerson
	if (nyPerson) {
		newPdldata.opprettNyPerson.syntetisk = true
		newPdldata.opprettNyPerson.identtype = nyPerson.identtype ?? 'FNR'
		newPdldata.opprettNyPerson.id2032 = nyPerson.id2032 ?? false
		if (nyPerson.alder === null && nyPerson.foedtFoer === null && nyPerson.foedtEtter === null) {
			newPdldata.opprettNyPerson = {
				identtype: nyPerson.identtype ?? 'FNR',
				id2032: nyPerson.id2032 ?? false,
				syntetisk: true,
			}
		}
	} else {
		newPdldata.opprettNyPerson = {}
	}
	const person = newPdldata?.person
	if (!person) {
		newPdldata.person = undefined
		return newPdldata
	}
	if (person?.bostedsadresse) {
		newPdldata.person.bostedsadresse = person.bostedsadresse.map((adresse: BostedData) => {
			return updateAdressetyper(adresse, false)
		})
	}
	if (person?.oppholdsadresse) {
		newPdldata.person.oppholdsadresse = person.oppholdsadresse.map(
			(adresse: OppholdsadresseData) => {
				return updateAdressetyper(adresse, false)
			},
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
				if (relasjon.nyRelatertPerson) {
					relasjon.nyRelatertPerson.syntetisk = true
				}
				return relasjon
			},
		)
	}

	if (person?.foreldreansvar) {
		newPdldata.person.foreldreansvar = person.foreldreansvar.map((relasjon: Foreldreansvar) => {
			if (relasjon.nyAnsvarlig) {
				relasjon.nyAnsvarlig.syntetisk = true
			}
			return relasjon
		})
	}

	if (person?.sivilstand) {
		newPdldata.person.sivilstand = person.sivilstand.map((relasjon: SivilstandData) => {
			if (relasjon.nyRelatertPerson) {
				relasjon.nyRelatertPerson.syntetisk = true
			}
			return relasjon
		})
	}

	if (person?.fullmakt) {
		newPdldata.person.fullmakt = person.fullmakt.map((fullmektig: FullmaktValues) => {
			if (fullmektig.nyFullmektig) {
				fullmektig.nyFullmektig.syntetisk = true
			}
			return fullmektig
		})
	}

	if (person?.vergemaal) {
		newPdldata.person.vergemaal = person.vergemaal.map((verge: VergemaalValues) => {
			if (verge.nyVergeIdent) {
				verge.nyVergeIdent.syntetisk = true
			}
			return verge
		})
	}

	if (person?.kontaktinformasjonForDoedsbo) {
		newPdldata.person.kontaktinformasjonForDoedsbo = person.kontaktinformasjonForDoedsbo.map(
			(kontaktinfo: any) => {
				kontaktinfo = updateKontaktType(kontaktinfo)
				if (kontaktinfo.personSomKontakt?.nyKontaktperson) {
					kontaktinfo.personSomKontakt.nyKontaktperson.syntetisk = true
				}
				return kontaktinfo
			},
		)
	}

	if (person?.nyident) {
		newPdldata.person.nyident = person.nyident.map((ident: NyIdent) => {
			ident.syntetisk = true
			return ident
		})
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

const getUpdatedSykemelding = (syntSykemelding: any) => {
	let updatedSykemelding = initialValuesDetaljertSykemelding
	updatedSykemelding.startDato = new Date()
	updatedSykemelding.mottaker.orgNr = syntSykemelding.orgnummer
	updatedSykemelding.perioder[0].fom = addDays(syntSykemelding.startDato, -7)
	updatedSykemelding.perioder[0].tom = addDays(syntSykemelding.startDato, -1)
	return updatedSykemelding
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

const getUpdatedDokarkiv = (dokarkiv: any) => {
	let newDokarkiv = dokarkiv?.map((dok) => {
		let newDok = { ...dok }
		if (newDok.avsenderMottaker) {
			newDok.avsenderMottaker = {
				id: newDok.avsenderMottaker?.id || '',
				navn: newDok.avsenderMottaker?.navn || '',
				idType: newDok.avsenderMottaker?.idType || '',
			}
		}
		return newDok
	})
	return newDokarkiv
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
