import * as Yup from 'yup'
import { ifPresent, messages, requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'
import { differenceInWeeks, isAfter, isBefore, isSameDay } from 'date-fns'

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
	return val.test('prioritet', 'Kan ikke ha lik prioritet', function erEgenPrio() {
		const values = this.options.context
		const index = this.options.index
		const tlfListe = _get(values, 'pdldata.person.telefonnummer')
		if (tlfListe.length < 2) return true
		const index2 = index === 0 ? 1 : 0
		return tlfListe[index]?.prioritet !== tlfListe[index2]?.prioritet
	})
}

const testFoedtEtter = (val) => {
	return val.test(
		'is-before-foedt-foer',
		'Dato må være før født før-dato',
		function isBeforeFoedtFoer(value) {
			const foedtFoer = _get(this, 'parent.foedtFoer')
			if (!value || !foedtFoer) return true
			return isBefore(new Date(value), new Date(foedtFoer))
		}
	)
}

const testFoedtFoer = (val) => {
	return val.test(
		'is-after-foedt-etter',
		'Dato må være etter født etter-dato',
		function isAfterFoedtEtter(value) {
			const foedtEtter = _get(this, 'parent.foedtEtter')
			if (!value || !foedtEtter) return true
			return isAfter(new Date(value), new Date(foedtEtter))
		}
	)
}

const testForeldreansvar = (val) => {
	return val.test('er-gyldig-foreldreansvar', function erGyldigForeldreansvar(selected) {
		var feilmelding = null
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
			const sivilstand = _get(values, 'pdldata.person.sivilstand')
			if (sivilstand && sivilstand.length > 0) {
				fantPartner = sivilstand[0].borIkkeSammen
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
	foedtEtter: testFoedtEtter(Yup.date().nullable()),
	foedtFoer: testFoedtFoer(Yup.date().nullable()),
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
	husnummer: Yup.string().nullable(),
	kommunenummer: Yup.string().nullable(),
	postnummer: Yup.string().nullable(),
})

const matrikkeladresse = Yup.object({
	kommunenummer: Yup.string().nullable(),
	gaardsnummer: Yup.string().max(5, 'Gårdsnummeret må være under 99999').nullable(),
	bruksnummer: Yup.string().max(4, 'Bruksnummeret må være under 9999').nullable(),
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

const bostedsadresse = Yup.array().of(
	Yup.object({
		adressetype: Yup.string().nullable(),
		angittFlyttedato: Yup.string().nullable(),
		gyldigFraOgMed: Yup.string().nullable(),
		gyldigTilOgMed: Yup.string().nullable(),
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
)

const oppholdsadresse = Yup.array().of(
	Yup.object({
		adressetype: Yup.string().nullable(),
		gyldigFraOgMed: Yup.string().nullable(),
		gyldigTilOgMed: Yup.string().nullable(),
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
)

const kontaktadresse = Yup.array().of(
	Yup.object({
		adressetype: Yup.string().nullable(),
		gyldigFraOgMed: Yup.string().nullable(),
		gyldigTilOgMed: Yup.string().nullable(),
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
)

const adressebeskyttelse = Yup.array().of(
	Yup.object({
		gradering: requiredString.nullable(),
	})
)

const fullmakt = Yup.array().of(
	Yup.object({
		omraader: Yup.array().min(1, 'Velg minst ett område'),
		gyldigFraOgMed: requiredDate,
		gyldigTilOgMed: requiredDate,
		motpartsPersonident: Yup.string().nullable(),
		nyFullmektig: nyPerson,
	})
)

const tilrettelagtKommunikasjon = Yup.array().of(
	Yup.object({
		spraakForTaletolk: Yup.string().nullable(),
		spraakForTegnspraakTolk: Yup.string().nullable(),
	})
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

const doedsfall = Yup.array().of(
	Yup.object({
		doedsdato: requiredDate.nullable(),
	})
)

const doedfoedtBarn = Yup.array().of(
	Yup.object({
		dato: requiredDate.nullable(),
	})
)

const statsborgerskap = Yup.array().of(
	Yup.object({
		landkode: requiredString.nullable(),
		gyldigFraOgMed: Yup.date().optional().nullable(),
		gyldigTilOgMed: Yup.date().optional().nullable(),
		bekreftelsesdato: Yup.date().optional().nullable(),
	})
)

const telefonnummer = Yup.array().of(
	Yup.object({
		landskode: requiredString,
		nummer: testTelefonnummer(),
		prioritet: testPrioritet(requiredString).nullable(),
	})
)

const innflytting = Yup.array().of(
	Yup.object({
		fraflyttingsland: requiredString,
		fraflyttingsstedIUtlandet: Yup.string().optional().nullable(),
		innflyttingsdato: requiredDate.nullable(),
	})
)

const utflytting = Yup.array().of(
	Yup.object({
		tilflyttingsland: requiredString,
		tilflyttingsstedIUtlandet: Yup.string().optional().nullable(),
		utflyttingsdato: requiredDate.nullable(),
	})
)

const sivilstand = Yup.array().of(
	Yup.object({
		type: requiredString.nullable(),
		sivilstandsdato: Yup.mixed().when('bekreftelsesdato', {
			is: null,
			then: requiredString.nullable(),
		}),
		relatertVedSivilstand: Yup.string().nullable(),
		bekreftelsesdato: Yup.string().nullable(),
		borIkkeSammen: Yup.boolean(),
		nyRelatertPerson: nyPerson,
	})
)

const deltBosted = Yup.object({
	adressetype: testDeltBostedAdressetype(requiredString.nullable()),
	startdatoForKontrakt: Yup.date().optional().nullable(),
	sluttdatoForKontrakt: Yup.date().optional().nullable(),
	vegadresse: vegadresse,
	matrikkeladresse: matrikkeladresse,
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
		relatertPerson: Yup.string().nullable(),
		borIkkeSammen: Yup.boolean(),
		nyRelatertPerson: nyPerson,
		deltBosted: deltBosted.nullable(),
	})
)

const kjoenn = Yup.array().of(
	Yup.object({
		kjoenn: requiredString.nullable(),
	})
)

const navn = Yup.array().of(
	Yup.object({
		fornavn: Yup.string().nullable(),
		mellomnavn: Yup.string().nullable(),
		etternavn: Yup.string().nullable(),
		hasMellomnavn: Yup.boolean(),
	})
)

const vergemaal = Yup.array().of(
	Yup.object({
		vergemaalEmbete: requiredString.nullable(),
		sakType: requiredString.nullable(),
		gyldigFraOgMed: Yup.string().nullable(),
		gyldigTilOgMed: Yup.string().nullable(),
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

export const validation = {
	pdldata: Yup.object({
		opprettNyPerson: Yup.object()
			.shape(
				{
					alder: Yup.mixed().when(['foedtEtter', 'foedtFoer'], {
						is: null,
						then: Yup.mixed().required(messages.required).nullable(),
					}),
					foedtEtter: testFoedtEtter(
						Yup.mixed().when(['alder', 'foedtFoer'], {
							is: (alder, foedtFoer) =>
								(alder === null || alder === '') && (foedtFoer === null || foedtFoer === ''),
							then: requiredDate.nullable(),
						})
					),
					foedtFoer: testFoedtFoer(
						Yup.mixed().when(['alder', 'foedtEtter'], {
							is: (alder, foedtEtter) =>
								(alder === null || alder === '') && (foedtEtter === null || foedtEtter === ''),
							then: requiredDate.nullable(),
						})
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
			bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', bostedsadresse),
			oppholdsadresse: ifPresent('$pdldata.person.oppholdsadresse', oppholdsadresse),
			kontaktadresse: ifPresent('$pdldata.person.kontaktadresse', kontaktadresse),
			adressebeskyttelse: ifPresent('$pdldata.person.adressebeskyttelse', adressebeskyttelse),
			fullmakt: ifPresent('$pdldata.person.fullmakt', fullmakt),
			sikkerhetstiltak: ifPresent('$pdldata.person.sikkerhetstiltak', sikkerhetstiltak),
			tilrettelagtKommunikasjon: ifPresent(
				'$pdldata.person.tilrettelagtKommunikasjon',
				tilrettelagtKommunikasjon
			),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			telefonnummer: ifPresent('$pdldata.person.telefonnummer', telefonnummer),
			statsborgerskap: ifPresent('$pdldata.person.statsborgerskap', statsborgerskap),
			doedsfall: ifPresent('$pdldata.person.doedsfall', doedsfall),
			doedfoedtBarn: ifPresent('$pdldata.person.doedfoedtBarn', doedfoedtBarn),
			innflytting: ifPresent('$pdldata.person.innflytting', innflytting),
			utflytting: ifPresent('$pdldata.person.utflytting', utflytting),
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
			kjoenn: ifPresent('$pdldata.person.kjoenn', kjoenn),
			navn: ifPresent('$pdldata.person.navn', navn),
			vergemaal: ifPresent('$pdldata.person.vergemaal', vergemaal),
			foreldreansvar: ifPresent('$pdldata.person.foreldreansvar', foreldreansvar),
		}).nullable(),
	}),
	tpsMessaging: ifPresent(
		'$tpsMessaging',
		Yup.object({
			spraakKode: ifPresent('$tpsMessaging.spraakKode', requiredString),
			egenAnsattDatoFom: ifPresent(
				'$tpsMessaging.egenAnsattDatoFom',
				Yup.string().test(
					'is-before-today',
					'Dato kan ikke være etter dagens dato',
					function validDate(dato) {
						return isBefore(new Date(dato), new Date())
					}
				)
			),
			egenAnsattDatoTom: ifPresent(
				'$tpsMessaging.egenAnsattDatoTom',
				Yup.string().test(
					'is-after-dato-fom',
					'Dato må være etter fra-dato og senest dagens dato',
					function validDate(dato) {
						const values = this.options.context
						return (
							isAfter(new Date(dato), new Date(_get(values, 'tpsMessaging.egenAnsattDatoFom'))) &&
							!isAfter(new Date(dato), new Date())
						)
					}
				)
			),
			utenlandskBankkonto: ifPresent(
				'$tpsMessaging.utenlandskBankkonto',
				Yup.object().shape({
					kontonummer: requiredString.nullable(),
					swift: Yup.string().nullable().optional(),
					landkode: requiredString.nullable(),
					iban: Yup.string().nullable().optional(),
					valuta: requiredString.nullable(),
					banknavn: Yup.string().nullable().optional(),
					bankAdresse1: Yup.string().nullable().optional(),
					bankAdresse2: Yup.string().nullable().optional(),
					bankAdresse3: Yup.string().nullable().optional(),
				})
			),
			norskBankkonto: ifPresent(
				'$tpsMessaging.norskBankkonto',
				Yup.object().shape({
					kontonummer: requiredString.nullable(),
				})
			),
		})
	),
}
