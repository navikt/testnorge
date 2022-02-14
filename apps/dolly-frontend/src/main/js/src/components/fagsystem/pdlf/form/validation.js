import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'
import { differenceInWeeks, isAfter, isBefore, isSameDay } from 'date-fns'

const personnavnSchema = Yup.object({
	fornavn: Yup.string(),
	mellomnavn: Yup.string(),
	etternavn: Yup.string(),
})

const nyPerson = Yup.object({
	identtype: Yup.string().nullable(),
	kjoenn: Yup.string().nullable(),
	foedtEtter: Yup.string().nullable(),
	foedtFoer: Yup.string().nullable(),
	alder: Yup.string().nullable(),
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
			const values = this.options.context
			const foedtFoer = _get(values, 'pdldata.opprettNyPerson.foedtFoer')
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
			const values = this.options.context
			const foedtEtter = _get(values, 'pdldata.opprettNyPerson.foedtEtter')
			if (!value || !foedtEtter) return true
			return isAfter(new Date(value), new Date(foedtEtter))
		}
	)
}

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

export const validation = {
	pdldata: Yup.object({
		opprettNyPerson: Yup.object({
			alder: Yup.number()
				.transform((i, j) => (j === '' ? null : i))
				.nullable(),
			foedtEtter: testFoedtEtter(Yup.date().nullable()),
			foedtFoer: testFoedtFoer(Yup.date().nullable()),
		}).nullable(),
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
			sivilstand: ifPresent('$pdldata.person.sivilstand', sivilstand),
			kjoenn: ifPresent('$pdldata.person.kjoenn', kjoenn),
			navn: ifPresent('$pdldata.person.navn', navn),
			vergemaal: ifPresent('$pdldata.person.vergemaal', vergemaal),
		}),
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
