import * as Yup from 'yup'
import { ifPresent, messages, requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'
import { differenceInWeeks, isAfter, isBefore, isEqual, isSameDay } from 'date-fns'
import { landkoder, landkodeIsoMapping } from '~/service/services/kontoregister/landkoder'
import { isNil } from 'lodash'

const testTelefonnummer = () =>
	Yup.string()
		.max(20, 'Telefonnummer kan ikke ha mer enn 20 sifre')
		.when('landskode', {
			is: '+47',
			then: Yup.string().length(8, 'Norsk telefonnummer må ha 8 sifre'),
		})
		.required('Feltet er påkrevd')
		.matches(/^[1-9]\d*$/, 'Telefonnummer må være numerisk, og kan ikke starte med 0')

const testPrioritet = (val) => {
	return val.test('prioritet', 'Ugyldig prioritet', function erEgenPrio() {
		const values = this?.options?.context
		if (!values || Object.keys(values).length < 1) return true
		const index = this?.options?.index || 0
		const tlfListe = _get(values, 'pdldata.person.telefonnummer') || _get(values, 'telefonnummer')
		if (tlfListe?.length < 2) {
			return tlfListe?.[index]?.prioritet === 1
		}
		const index2 = index === 0 ? 1 : 0
		return tlfListe?.[index]?.prioritet !== tlfListe?.[index2]?.prioritet
	})
}

export const testDatoFom = (val, tomPath, feilmelding) => {
	return val.test(
		'is-before-tom',
		feilmelding || 'Dato må være før til-dato',
		function isBeforeTom(value) {
			const datoTom = _get(this, `parent.${tomPath}`)
			if (!value || !datoTom) return true
			if (isEqual(new Date(value), new Date(datoTom))) return true
			return isBefore(new Date(value), new Date(datoTom))
		}
	)
}

export const testDatoTom = (val, fomPath, feilmelding) => {
	return val.test(
		'is-after-fom',
		feilmelding || 'Dato må være etter fra-dato',
		function isAfterFom(value) {
			const datoFom = _get(this, `parent.${fomPath}`)
			if (!value || !datoFom) return true
			if (isEqual(new Date(value), new Date(datoFom))) return true
			return isAfter(new Date(value), new Date(datoFom))
		}
	)
}

const testForeldreansvar = (val) => {
	return val.test('er-gyldig-foreldreansvar', function erGyldigForeldreansvar(selected) {
		let feilmelding = null
		const values = this.options.context

		const foreldrerelasjoner = _get(values, 'pdldata.person.forelderBarnRelasjon')?.map(
			(a) => a.minRolleForPerson
		)
		const sivilstander = _get(values, 'pdldata.person.sivilstand')?.map((a) => a.type)
		const barn = _get(values, 'pdldata.person.forelderBarnRelasjon')?.filter(
			(a) => a.relatertPersonsRolle === 'BARN'
		)
		const kjoennListe = _get(values, 'pdldata.person.kjoenn')

		const gyldigeSivilstander = ['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER']

		if (selected === 'MOR' || selected === 'MEDMOR') {
			const gyldigeRelasjoner = ['MOR', 'MEDMOR']
			if (
				(foreldrerelasjoner?.includes('FORELDER') &&
					!kjoennListe?.some((a) => a.kjoenn === 'KVINNE') &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a))) ||
				(!foreldrerelasjoner?.includes('FORELDER') &&
					!gyldigeRelasjoner.some((a) => foreldrerelasjoner?.includes(a)) &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a)))
			) {
				feilmelding = 'Barn med foreldrerolle mor eller medmor finnes ikke'
			}
			if (!barn?.some((a) => !a.partnerErIkkeForelder)) {
				feilmelding = 'Partner er ikke forelder'
			}
		}
		if (selected === 'FAR') {
			if (
				(foreldrerelasjoner?.includes('FORELDER') &&
					!kjoennListe?.some((a) => a.kjoenn === 'MANN') &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a))) ||
				(!foreldrerelasjoner?.includes('FORELDER') &&
					!foreldrerelasjoner?.includes('FAR') &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a)))
			) {
				feilmelding = 'Barn med foreldrerolle far finnes ikke'
			}
			if (!barn?.some((a) => !a.partnerErIkkeForelder)) {
				feilmelding = 'Partner er ikke forelder'
			}
		}
		if (selected === 'FELLES') {
			if (!gyldigeSivilstander?.some((a) => sivilstander?.includes(a))) {
				feilmelding =
					'Partner med sivilstand gift, registrert partner, separert eller separert partner finnes ikke'
			}
			if (!barn?.some((a) => !a.partnerErIkkeForelder)) {
				feilmelding = 'Partner er ikke forelder'
			}
		}
		return feilmelding ? this.createError({ message: feilmelding }) : true
	})
}

const testDeltBostedAdressetype = (value) => {
	return value.test('har-gyldig-adressetype', function harGyldigAdressetype(selected) {
		let feilmelding = null
		if (selected === 'PARTNER_ADRESSE') {
			const values = this.options.context
			const personFoerLeggTil = values.personFoerLeggTil

			let fantPartner = false
			const nyePartnere = _get(values, 'pdldata.person.sivilstand')
			if (nyePartnere?.length > 0) {
				fantPartner = nyePartnere[0].borIkkeSammen
			} else if (personFoerLeggTil?.pdlforvalter?.relasjoner) {
				const partnere = personFoerLeggTil.pdlforvalter.relasjoner.filter(
					(relasjon) => relasjon.relasjonType === 'EKTEFELLE_PARTNER'
				)
				if (partnere.length > 0) {
					const partnerAdresseId =
						partnere[0].relatertPerson?.bostedsadresse?.[0]?.adresseIdentifikatorFraMatrikkelen
					const identAdresseId =
						personFoerLeggTil?.pdlforvalter?.person?.bostedsadresse?.[0]
							?.adresseIdentifikatorFraMatrikkelen
					if (partnerAdresseId && partnerAdresseId !== identAdresseId) {
						fantPartner = true
					}
				}
			}
			feilmelding = fantPartner ? null : 'Fant ikke gyldig partner for delt bosted'
		}

		return feilmelding ? this.createError({ message: feilmelding }) : true
	})
}

const gyldigDatoFom = Yup.lazy((val) =>
	val instanceof Date
		? testDatoFom(Yup.date().nullable(), 'gyldigTilOgMed')
		: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed')
)

const gyldigDatoTom = Yup.lazy((val) =>
	val instanceof Date
		? testDatoTom(Yup.date().nullable(), 'gyldigFraOgMed')
		: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed')
)

const personnavnSchema = Yup.object({
	fornavn: Yup.string(),
	mellomnavn: Yup.string(),
	etternavn: Yup.string(),
})

const nyPerson = Yup.object({
	identtype: Yup.string().nullable(),
	kjoenn: Yup.string().nullable(),
	alder: Yup.number()
		.transform((i, j) => (j === '' ? null : i))
		.nullable(),
	foedtEtter: testDatoFom(Yup.date().nullable(), 'foedtFoer', 'Dato må være før født før-dato'),
	foedtFoer: testDatoTom(Yup.date().nullable(), 'foedtEtter', 'Dato må være etter født etter-dato'),
	syntetisk: Yup.boolean(),
	nyttNavn: Yup.object({
		hasMellomnavn: Yup.boolean(),
	}),
	statsborgerskapLandkode: Yup.string().nullable(),
	gradering: Yup.string().nullable(),
})

const vegadresse = Yup.object({
	adressekode: Yup.string().nullable(),
	adressenavn: Yup.string().nullable(),
	tilleggsnavn: Yup.string().nullable(),
	bruksenhetsnummer: Yup.string().nullable(),
	husbokstav: Yup.string().nullable(),
	husnummer: Yup.lazy((val) =>
		typeof val === 'string' ? Yup.string().nullable() : Yup.number().nullable()
	),
	kommunenummer: Yup.string().nullable(),
	postnummer: Yup.string().nullable(),
})

const matrikkeladresse = Yup.object({
	kommunenummer: Yup.string().nullable(),
	gaardsnummer: Yup.lazy((val) =>
		typeof val === 'string'
			? Yup.string().max(5, 'Gårdsnummeret må være under 99999').nullable()
			: Yup.number().max(99999, 'Gårdsnummeret må være under 99999').nullable()
	),
	bruksnummer: Yup.lazy((val) =>
		typeof val === 'string'
			? Yup.string().max(4, 'Bruksnummeret må være under 9999').nullable()
			: Yup.number().max(9999, 'Bruksnummeret må være under 9999').nullable()
	),
	postnummer: Yup.string().nullable(),
	bruksenhetsnummer: Yup.string()
		.matches(
			/^[HULK]\d{4}$/,
			'Bruksenhetsnummer består av bokstaven H, L, U eller K etterfulgt av 4 sifre'
		)
		.transform((i, j) => (j === '' ? null : i))
		.nullable(),
	tilleggsnavn: Yup.string().nullable(),
})

const utenlandskAdresse = Yup.object({
	adressenavnNummer: Yup.string().nullable(),
	postboksNummerNavn: Yup.string().nullable(),
	postkode: Yup.string().nullable(),
	bySted: Yup.string().nullable(),
	landkode: Yup.string().nullable(),
	bygningEtasjeLeilighet: Yup.string().nullable(),
	regionDistriktOmraade: Yup.string().nullable(),
})

const postboksadresse = Yup.object({
	postboks: requiredString.nullable(),
	postbokseier: Yup.string().nullable(),
	postnummer: requiredString.nullable(),
})

const ukjentBosted = Yup.object({
	bostedskommune: Yup.string().nullable(),
})

export const bostedsadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	angittFlyttedato: Yup.lazy((val) =>
		val instanceof Date ? Yup.date().nullable() : Yup.string().nullable()
	),
	gyldigFraOgMed: gyldigDatoFom,
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: vegadresse,
	}),
	matrikkeladresse: Yup.mixed().when('adressetype', {
		is: 'MATRIKKELADRESSE',
		then: matrikkeladresse,
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: utenlandskAdresse,
	}),
	ukjentBosted: Yup.mixed().when('adressetype', {
		is: 'UKJENT_BOSTED',
		then: ukjentBosted,
	}),
})

export const oppholdsadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	gyldigFraOgMed: gyldigDatoFom,
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: vegadresse,
	}),
	matrikkeladresse: Yup.mixed().when('adressetype', {
		is: 'MATRIKKELADRESSE',
		then: matrikkeladresse,
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: utenlandskAdresse,
	}),
	oppholdAnnetSted: Yup.mixed().when('adressetype', {
		is: 'OPPHOLD_ANNET_STED',
		then: requiredString.nullable(),
	}),
})

export const kontaktadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	gyldigFraOgMed: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed'),
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: vegadresse,
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: utenlandskAdresse,
	}),
	postboksadresse: Yup.mixed().when('adressetype', {
		is: 'POSTBOKSADRESSE',
		then: postboksadresse,
	}),
})

export const adressebeskyttelse = Yup.object({
	gradering: requiredString.nullable(),
})

const fullmakt = Yup.array().of(
	Yup.object({
		omraader: Yup.array().min(1, 'Velg minst ett område'),
		gyldigFraOgMed: testDatoFom(requiredDate, 'gyldigTilOgMed'),
		gyldigTilOgMed: testDatoTom(requiredDate, 'gyldigFraOgMed'),
		motpartsPersonident: Yup.string().nullable(),
		nyFullmektig: nyPerson,
	})
)

const tilrettelagtKommunikasjon = Yup.array().of(
	Yup.object().shape(
		{
			spraakForTaletolk: Yup.mixed().when('spraakForTegnspraakTolk', {
				is: null,
				then: requiredString.nullable(),
			}),
			spraakForTegnspraakTolk: Yup.mixed().when('spraakForTaletolk', {
				is: null,
				then: requiredString.nullable(),
			}),
		},
		['spraakForTaletolk', 'spraakForTegnspraakTolk']
	)
)

const sikkerhetstiltak = Yup.array().of(
	Yup.object({
		tiltakstype: requiredString.nullable(),
		beskrivelse: Yup.string().nullable(),
		kontaktperson: Yup.object({
			personident: requiredString.nullable(),
			enhet: requiredString.nullable(),
		}),
		gyldigFraOgMed: requiredDate.nullable(),
		gyldigTilOgMed: Yup.string()
			.test(
				'is-after-startdato',
				'Dato må være lik eller etter startdato, og ikke mer enn 12 uker etter startdato',
				function validDate(dato) {
					const values = this.options.context
					return (
						(isAfter(
							new Date(dato),
							new Date(_get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
						) ||
							isSameDay(
								new Date(dato),
								new Date(_get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
							)) &&
						differenceInWeeks(
							new Date(dato),
							new Date(_get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
						) <= 12
					)
				}
			)
			.nullable(),
	})
)

const falskIdentitet = Yup.array().of(
	Yup.object({
		rettIdentErUkjent: Yup.boolean(),
		rettIdentitetVedIdentifikasjonsnummer: Yup.string().nullable(),
		rettIdentitetVedOpplysninger: Yup.object({
			foedselsdato: Yup.string().nullable(),
			kjoenn: Yup.string().nullable(),
			personnavn: personnavnSchema.nullable(),
			statsborgerskap: Yup.array().of(Yup.string()),
		}),
	})
)

const utenlandskId = Yup.array().of(
	Yup.object({
		identifikasjonsnummer: requiredString,
		opphoert: requiredString,
		utstederland: requiredString,
	})
)

const kontaktDoedsbo = Yup.array().of(
	Yup.object().shape({
		skifteform: requiredString.nullable(),
		attestutstedelsesdato: requiredDate,
		kontaktType: requiredString.nullable(),
		adresse: Yup.object({
			adresselinje1: Yup.string().nullable(),
			adresselinje2: Yup.string().nullable(),
			postnummer: Yup.string().nullable(),
			poststedsnavn: Yup.string().nullable(),
			landkode: Yup.string().nullable(),
		}),

		advokatSomKontakt: Yup.object().when('kontaktType', {
			is: 'ADVOKAT',
			then: Yup.object({
				organisasjonsnummer: requiredString.nullable(),
				organisasjonsnavn: Yup.string().nullable(),
				kontaktperson: Yup.object({
					fornavn: Yup.string().nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: Yup.string().nullable(),
				}).nullable(),
			}),
		}),

		organisasjonSomKontakt: Yup.object().when('kontaktType', {
			is: 'ORGANISASJON',
			then: Yup.object({
				organisasjonsnummer: requiredString.nullable(),
				organisasjonsnavn: Yup.string().nullable(),
				kontaktperson: Yup.object({
					fornavn: Yup.string().nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: Yup.string().nullable(),
				}).nullable(),
			}),
		}),

		personSomKontakt: Yup.object()
			.when('kontaktType', {
				is: 'PERSON_FDATO',
				then: Yup.object().shape(
					{
						identifikasjonsnummer: Yup.mixed().when('foedselsdato', {
							is: null,
							then: requiredString.nullable(),
						}),
						foedselsdato: Yup.mixed().when('identifikasjonsnummer', {
							is: null,
							then: requiredString.nullable(),
						}),
						navn: Yup.object({
							fornavn: Yup.string().nullable(),
							mellomnavn: Yup.string().nullable(),
							etternavn: Yup.string().nullable(),
						}).nullable(),
					},
					['identifikasjonsnummer', 'foedselsdato']
				),
			})
			.when('kontaktType', {
				is: 'NY_PERSON',
				then: Yup.object({
					nyKontaktperson: nyPerson,
				}),
			}),
	})
)

export const doedsfall = Yup.object({
	doedsdato: requiredDate.nullable(),
})

const doedfoedtBarn = Yup.array().of(
	Yup.object({
		dato: requiredDate.nullable(),
	})
)

export const statsborgerskap = Yup.object({
	landkode: requiredString.nullable(),
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
	bekreftelsesdato: Yup.date().optional().nullable(),
})

export const telefonnummer = Yup.object({
	landskode: requiredString,
	nummer: testTelefonnummer(),
	prioritet: testPrioritet(Yup.mixed().required()).nullable(),
}).nullable()

const validInnflyttingsdato = (val) => {
	return val.test('gyldig-innflyttingsdato', function isWithinTest(value) {
		if (!value) return true
		const innflyttingsdato = value
		const values = this.options.context
		const utflyttingsdato = _get(values, 'pdldata.person.utflytting[0].utflyttingsdato')
		if (
			!isNil(utflyttingsdato) &&
			new Date(innflyttingsdato).getDate() === new Date(utflyttingsdato).getDate()
		) {
			return this.createError({ message: 'Innflyttingsdato kan ikke være lik utflyttingsdato' })
		}
		return true
	})
}

export const innflytting = Yup.object({
	fraflyttingsland: requiredString,
	fraflyttingsstedIUtlandet: Yup.string().optional().nullable(),
	innflyttingsdato: validInnflyttingsdato(requiredDate),
})

export const utflytting = Yup.object({
	tilflyttingsland: requiredString,
	tilflyttingsstedIUtlandet: Yup.string().optional().nullable(),
	utflyttingsdato: requiredDate.nullable(),
})

const sivilstand = Yup.array().of(
	Yup.object({
		type: requiredString.nullable(),
		sivilstandsdato: Yup.string().nullable(),
		relatertVedSivilstand: Yup.string().nullable(),
		bekreftelsesdato: Yup.string().nullable(),
		borIkkeSammen: Yup.boolean(),
		nyRelatertPerson: nyPerson,
	})
)

const deltBosted = Yup.object({
	adressetype: testDeltBostedAdressetype(requiredString.nullable()),
	startdatoForKontrakt: testDatoFom(
		Yup.date().optional().nullable(),
		'sluttdatoForKontrakt',
		'Dato må være før sluttdato'
	),
	sluttdatoForKontrakt: testDatoTom(
		Yup.date().optional().nullable(),
		'startdatoForKontrakt',
		'Dato må være etter startdato'
	),
	vegadresse: vegadresse.nullable(),
	matrikkeladresse: matrikkeladresse.nullable(),
	ukjentBosted: Yup.mixed().when('adressetype', {
		is: 'UKJENT_BOSTED',
		then: Yup.object({
			bostedskommune: requiredString.nullable(),
		}),
	}),
})

const forelderBarnRelasjon = Yup.array().of(
	Yup.object({
		minRolleForPerson: requiredString,
		relatertPersonsRolle: requiredString,
		relatertPerson: Yup.string().nullable(),
		borIkkeSammen: Yup.mixed().when('relatertPersonsRolle', {
			is: 'BARN',
			then: Yup.mixed().notRequired(),
			otherwise: Yup.boolean(),
		}),
		nyRelatertPerson: nyPerson.nullable(),
		deltBosted: Yup.mixed().when('relatertPersonsRolle', {
			is: 'BARN',
			then: deltBosted.nullable(),
		}),
	})
)

export const kjoenn = Yup.object({
	kjoenn: requiredString.nullable(),
})

export const navn = Yup.object({
	fornavn: Yup.string().nullable(),
	mellomnavn: Yup.string().nullable(),
	etternavn: Yup.string().nullable(),
	hasMellomnavn: Yup.boolean().nullable(),
})

const vergemaal = Yup.array().of(
	Yup.object({
		vergemaalEmbete: requiredString.nullable(),
		sakType: requiredString.nullable(),
		gyldigFraOgMed: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed'),
		gyldigTilOgMed: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed'),
		nyVergeIdent: nyPerson,
		vergeIdent: Yup.string().nullable(),
		mandatType: Yup.string().nullable(),
	})
)

const foreldreansvar = Yup.array().of(
	Yup.object({
		ansvar: testForeldreansvar(requiredString.nullable()),
	})
)

const validInputOrCheckboxTest = (val, checkboxPath, feilmelding, inputValidation) => {
	return val.test('is-input-or-checkbox', function isInputOrCheckbox(value) {
		if (value) {
			if (inputValidation) {
				const inputError = inputValidation(value, this)
				if (inputError) {
					return this.createError({ message: inputError })
				}
			}
			return true
		}

		const path = this.path.substring(0, this.path.lastIndexOf('.'))
		const values = this.options.context

		const checkbox = _get(values, `${path}.${checkboxPath}`)

		if (!checkbox) {
			return this.createError({ message: feilmelding })
		}

		return true
	})
}

// from - begynner med 0
const replaceSubstringAtPos = (str, from, to, replaceWith) => {
	return str.substring(0, from) + replaceWith + str.substr(to + 1)
}

const validateIban = (kontonummer, form) => {
	if (!kontonummer) {
		return messages.required
	}

	if (kontonummer && (kontonummer.length < 1 || kontonummer.length > 36)) {
		return 'Kontonummer kan være mellom 1 og 36 tegn'
	}
	if (!/^[A-Z0-9]*$/.test(kontonummer)) {
		return 'Kontonummer kan kun bestå av tegnene A-Z eller 0-9'
	}

	const path = form.path.substring(0, form.path.lastIndexOf('.'))
	const values = form.options.context

	const landkode = _get(values, `${path}.landkode`)
	if (landkode) {
		const isoLandkode = landkodeIsoMapping[landkode]
		const mappedLandkode = isoLandkode ? isoLandkode : landkode.substring(0, 2)
		if (kontonummer.substring(0, 2) !== mappedLandkode) {
			return `Feil landkode i kontonumer. Den bør være ${mappedLandkode} (${mappedLandkode}${kontonummer})`
		}

		const kontoregisterLandkode = landkoder.find((k) => k.landkode === mappedLandkode)
		if (
			kontoregisterLandkode &&
			kontoregisterLandkode.ibanLengde &&
			kontonummer.length !== kontoregisterLandkode.ibanLengde
		) {
			return `Kontonummer for ${mappedLandkode} må være ${kontoregisterLandkode.ibanLengde} tegn (nå er den ${kontonummer.length} tegn)`
		}
	}

	return ''
}

const validateSwift = (val) => {
	return val.test('swift-validering', function isSwiftValid() {
		const path = this.path.substring(0, this.path.lastIndexOf('.'))
		const values = this.options.context
		const landkode = _get(values, `${path}.landkode`)

		let mappedLandkode = null

		if (landkode) {
			const isoLandkode = landkodeIsoMapping[landkode]
			mappedLandkode = isoLandkode ? isoLandkode : landkode.substring(0, 2)
		}

		const value = _get(values, `${path}.swift`) // henter siste verdi for swift å unngå gammel swift validering når land endres
		if (!value) {
			if (mappedLandkode) {
				const kontoregisterLandkode = landkoder.find((k) => k.landkode === mappedLandkode)
				if (kontoregisterLandkode.kreverIban) {
					return this.createError({ message: messages.required })
				}
			}

			return true
		}

		if (value.length !== 11 && value.length !== 8) {
			return this.createError({ message: 'Må være enten 8 eller 11 tegn' })
		}

		const swiftFormatExplain =
			'BBBBLLCCDDD - hvor BBBB er bankkode (4 tegn fra A-Z), LL er landkode (2 tegn), CC er sted (2 tegn fra 0-9, A-Z), og DDD er bransjekode 3 tegn fra 0-9, A-Z.'

		if (!/^[A-Z]{6}[0-9A-Z]{2}([0-9A-Z]{3})?$/.test(value)) {
			return this.createError({
				message: `Ugyldig format. ${swiftFormatExplain}`,
			})
		}

		if (mappedLandkode) {
			if (value.substring(4, 6) !== mappedLandkode) {
				return this.createError({
					message:
						`Feil landkode i SWIFT. Den bør være ${mappedLandkode} (${replaceSubstringAtPos(
							value,
							4,
							5,
							mappedLandkode
						)}). \n\n` + `Swift format: ${swiftFormatExplain}`,
				})
			}
		}
		return true
	})
}

export const folkeregisterpersonstatus = Yup.object({
	status: requiredString.nullable(),
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
})

export const validation = {
	pdldata: Yup.object({
		opprettNyPerson: Yup.object()
			.shape(
				{
					alder: Yup.mixed().when(['foedtEtter', 'foedtFoer'], {
						is: null,
						then: Yup.mixed()
							.test(
								'max',
								`Alder må være mindre enn ${new Date().getFullYear() - 1899} år`,
								(val) => val && new Date().getFullYear() - parseInt(val) >= 1900
							)
							.test('min', 'Alder må være minst 0 år', (val) => val && parseInt(val) >= 0)
							.required(messages.required)
							.nullable(),
					}),
					foedtEtter: testDatoFom(
						Yup.mixed().when(['alder', 'foedtFoer'], {
							is: (alder, foedtFoer) =>
								(alder === null || alder === '') && (foedtFoer === null || foedtFoer === ''),
							then: requiredDate.nullable(),
						}),
						'foedtFoer',
						'Dato må være før født før-dato'
					),
					foedtFoer: testDatoTom(
						Yup.mixed().when(['alder', 'foedtEtter'], {
							is: (alder, foedtEtter) =>
								(alder === null || alder === '') && (foedtEtter === null || foedtEtter === ''),
							then: requiredDate.nullable(),
						}),
						'foedtEtter',
						'Dato må være etter født etter-dato'
					),
				},
				[
					['foedtEtter', 'foedtFoer'],
					['alder', 'foedtFoer'],
					['alder', 'foedtEtter'],
				]
			)
			.nullable(),
		person: Yup.object({
			bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', Yup.array().of(bostedsadresse)),
			oppholdsadresse: ifPresent(
				'$pdldata.person.oppholdsadresse',
				Yup.array().of(oppholdsadresse)
			),
			kontaktadresse: ifPresent('$pdldata.person.kontaktadresse', Yup.array().of(kontaktadresse)),
			adressebeskyttelse: ifPresent(
				'$pdldata.person.adressebeskyttelse',
				Yup.array().of(adressebeskyttelse)
			),
			fullmakt: ifPresent('$pdldata.person.fullmakt', fullmakt),
			sikkerhetstiltak: ifPresent('$pdldata.person.sikkerhetstiltak', sikkerhetstiltak),
			tilrettelagtKommunikasjon: ifPresent(
				'$pdldata.person.tilrettelagtKommunikasjon',
				tilrettelagtKommunikasjon
			),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			telefonnummer: ifPresent('$pdldata.person.telefonnummer', Yup.array().of(telefonnummer)),
			statsborgerskap: ifPresent(
				'$pdldata.person.statsborgerskap',
				Yup.array().of(statsborgerskap)
			),
			doedsfall: ifPresent('$pdldata.person.doedsfall', Yup.array().of(doedsfall)),
			doedfoedtBarn: ifPresent('$pdldata.person.doedfoedtBarn', doedfoedtBarn),
			innflytting: ifPresent('$pdldata.person.innflytting', Yup.array().of(innflytting)),
			utflytting: ifPresent('$pdldata.person.utflytting', Yup.array().of(utflytting)),
			utenlandskIdentifikasjonsnummer: ifPresent(
				'$pdldata.person.utenlandskIdentifikasjonsnummer',
				utenlandskId
			),
			kontaktinformasjonForDoedsbo: ifPresent(
				'$pdldata.person.kontaktinformasjonForDoedsbo',
				kontaktDoedsbo
			),
			forelderBarnRelasjon: ifPresent('$pdldata.person.forelderBarnRelasjon', forelderBarnRelasjon),
			sivilstand: ifPresent('$pdldata.person.sivilstand', sivilstand),
			kjoenn: ifPresent('$pdldata.person.kjoenn', Yup.array().of(kjoenn)),
			navn: ifPresent('$pdldata.person.navn', Yup.array().of(navn)),
			vergemaal: ifPresent('$pdldata.person.vergemaal', vergemaal),
			foreldreansvar: ifPresent('$pdldata.person.foreldreansvar', foreldreansvar),
		}).nullable(),
	}),
	tpsMessaging: ifPresent(
		'$tpsMessaging',
		Yup.object({
			spraakKode: ifPresent('$tpsMessaging.spraakKode', requiredString),
			egenAnsattDatoFom: ifPresent('$tpsMessaging.egenAnsattDatoFom', Yup.string()),
			egenAnsattDatoTom: ifPresent(
				'$tpsMessaging.egenAnsattDatoTom',
				testDatoTom(Yup.string(), 'egenAnsattDatoFom')
			),
		})
	),
	bankkonto: ifPresent(
		'$bankkonto',
		Yup.object({
			utenlandskBankkonto: ifPresent(
				'$bankkonto.utenlandskBankkonto',
				Yup.object().shape({
					kontonummer: validInputOrCheckboxTest(
						Yup.string(),
						'tilfeldigKontonummer',
						messages.required,
						validateIban
					),
					tilfeldigKontonummer: Yup.object().nullable(),
					swift: validateSwift(Yup.string()),
					landkode: requiredString.nullable(),
					iban: Yup.string().nullable().optional(),
					valuta: requiredString
						.nullable()
						.test('length', 'Valutakode kan kun bestå av tre store bokstaver', (val) =>
							/^[A-Z]{3}$/.test(val)
						),
					banknavn: Yup.string().nullable().optional(),
					bankAdresse1: Yup.string()
						.nullable()
						.optional()
						.test(
							'length',
							'Bankadresse må være mellom 1 og 34 tegn',
							(val) => !val || val.length <= 34
						),
					bankAdresse2: Yup.string()
						.nullable()
						.optional()
						.test(
							'length',
							'Bankadresse må være mellom 1 og 34 tegn',
							(val) => !val || val.length <= 34
						),
					bankAdresse3: Yup.string()
						.nullable()
						.optional()
						.test(
							'length',
							'Bankadresse må være mellom 1 og 34 tegn',
							(val) => !val || val.length <= 34
						),
				})
			),
			norskBankkonto: ifPresent(
				'$bankkonto.norskBankkonto',
				Yup.object().shape({
					kontonummer: validInputOrCheckboxTest(
						Yup.string().nullable(),
						'tilfeldigKontonummer',
						messages.required,
						null
					),
					tilfeldigKontonummer: Yup.object().nullable(),
				})
			),
		})
	),
}
