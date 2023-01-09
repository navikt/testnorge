export const pdlBulkpersonerMock = {
	hentPersonBolk: [
		{
			ident: '12345678912',
			person: {
				navn: [
					{
						fornavn: 'Testesen',
						etternavn: 'Cafe',
						metadata: {
							historisk: false,
						},
					},
				],
				foedsel: [
					{
						foedekommune: null,
						foedeland: 'NOR',
						foedested: null,
						foedselsaar: 1960,
						foedselsdato: '1960-01-01',
						metadata: {
							historisk: false,
						},
					},
				],
				forelderBarnRelasjon: [],
				sivilstand: [
					{
						type: 'UGIFT',
						gyldigFraOgMed: '1982-04-07',
						relatertVedSivilstand: null,
						metadata: {
							historisk: false,
						},
					},
				],
				doedsfall: [],
				utflyttingFraNorge: [],
				innflyttingTilNorge: [],
				kjoenn: [
					{
						kjoenn: 'KVINNE',
						metadata: {
							historisk: false,
						},
					},
				],
				folkeregisteridentifikator: [
					{
						identifikasjonsnummer: '12345678912',
						type: 'FNR',
						metadata: {
							historisk: false,
						},
					},
				],
				bostedsadresse: [
					{
						id: null,
						kilde: null,
						master: null,
						folkeregistermetadata: {
							ajourholdstidspunkt: '2022-09-09',
							gjeldende: null,
							gyldighetstidspunkt: '1960-01-01',
							opphoerstidspunkt: null,
						},
						adresseIdentifikatorFraMatrikkelen: null,
						gyldigFraOgMed: '1960-01-01T00:00:00',
						gyldigTilOgMed: null,
						coAdressenavn: null,
						opprettCoAdresseNavn: null,
						angittFlyttedato: '1960-01-01T00:00:00',
						vegadresse: {
							adressekode: null,
							adressenavn: 'Testeveien',
							tilleggsnavn: null,
							bruksenhetsnummer: null,
							husbokstav: null,
							husnummer: '999',
							kommunenummer: '3804',
							bydelsnummer: null,
							postnummer: '3243',
						},
						ukjentBosted: null,
						matrikkeladresse: null,
						utenlandskAdresse: null,
					},
				],
				kontaktadresse: [],
				oppholdsadresse: [],
				deltBosted: [],
				foreldreansvar: [],
				kontaktinformasjonForDoedsbo: [],
				utenlandskIdentifikasjonsnummer: [],
				falskIdentitet: null,
				adressebeskyttelse: [],
				folkeregisterpersonstatus: [
					{
						status: 'BOSATT',
						folkeregistermetadata: {
							gyldighetstidspunkt: '1960-01-01T00:00:00',
							aarsak: null,
							ajourholdstidspunkt: '2022-09-09T00:00:00',
							opphoerstidspunkt: null,
						},
					},
				],
				tilrettelagtKommunikasjon: [],
				statsborgerskap: [
					{
						land: 'NOR',
						gyldigFraOgMed: '1960-01-01T00:00:00',
						gyldigTilOgMed: null,
					},
				],
				opphold: [],
				telefonnummer: [],
				fullmakt: [],
				vergemaalEllerFremtidsfullmakt: [],
				sikkerhetstiltak: [],
				doedfoedtBarn: [],
			},
		},
	],
	hentGeografiskTilknytningBolk: [
		{
			ident: '12345678912',
			geografiskTilknytning: {
				gtType: 'KOMMUNE',
				gtLand: null,
				gtKommune: '1820',
				gtBydel: null,
				regel: '2',
			},
		},
	],
	hentIdenterBolk: [
		{
			ident: '12345678912',
			identer: [
				{
					ident: '12345678912',
					gruppe: 'FOLKEREGISTERIDENT',
					historisk: false,
				},
				{
					ident: '12123456789',
					gruppe: 'AKTORID',
					historisk: false,
				},
			],
		},
	],
}

export const pdlForvalterMock = [
	{
		id: 1,
		person: {
			ident: '12345678912',
			identtype: 'DNR',
			navn: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: null,
					etternavn: 'Cafe',
					fornavn: 'Testytest',
					mellomnavn: null,
					hasMellomnavn: null,
					gyldigFraOgMed: '1992-06-04T00:00:00',
				},
			],
			foedsel: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '1992-06-04T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '1992-06-04T00:00:00',
						opphoerstidspunkt: null,
					},
					foedekommune: null,
					foedeland: 'NOR',
					foedested: null,
					foedselsaar: 1992,
					foedselsdato: '1992-06-04T00:00:00',
				},
			],
			forelderBarnRelasjon: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2023-01-09T10:24:01',
						gjeldende: null,
						gyldighetstidspunkt: '2023-01-09T10:24:01',
						opphoerstidspunkt: null,
					},
					minRolleForPerson: 'FAR',
					relatertPerson: '56789123456',
					relatertPersonsRolle: 'BARN',
					relatertPersonUtenFolkeregisteridentifikator: null,
					borIkkeSammen: null,
					nyRelatertPerson: null,
					partnerErIkkeForelder: null,
					eksisterendePerson: false,
					deltBosted: null,
				},
			],
			sivilstand: [
				{
					id: 2,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2023-01-09T10:24:52',
						gjeldende: null,
						gyldighetstidspunkt: '2023-01-09T10:24:52',
						opphoerstidspunkt: null,
					},
					bekreftelsesdato: null,
					relatertVedSivilstand: '34567891234',
					sivilstandsdato: null,
					type: 'GIFT',
					borIkkeSammen: null,
					nyRelatertPerson: null,
					eksisterendePerson: false,
				},
			],
			bostedsadresse: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'PDL',
					folkeregistermetadata: {
						ajourholdstidspunkt: '1992-06-04T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '1992-06-04T00:00:00',
						opphoerstidspunkt: null,
					},
					adresseIdentifikatorFraMatrikkelen: null,
					gyldigFraOgMed: '1992-06-04T00:00:00',
					gyldigTilOgMed: null,
					coAdressenavn: null,
					opprettCoAdresseNavn: null,
					angittFlyttedato: '1992-06-04T00:00:00',
					vegadresse: null,
					ukjentBosted: null,
					matrikkeladresse: null,
					utenlandskAdresse: {
						adressenavnNummer: '1KOLEJOWA 6/5',
						boenhet: null,
						bySted: 'CAPITAL WEST',
						bygning: null,
						bygningEtasjeLeilighet: null,
						distriktsnavn: null,
						etasjenummer: null,
						landkode: 'SYC',
						postboksNummerNavn: null,
						postkode: '3000',
						region: null,
						regionDistriktOmraade: '18-500 KOLNO',
					},
				},
			],
			kjoenn: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '1992-06-04T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '1992-06-04T00:00:00',
						opphoerstidspunkt: null,
					},
					kjoenn: 'KVINNE',
				},
			],
			innflytting: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2004-10-06T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '2004-10-06T00:00:00',
						opphoerstidspunkt: null,
					},
					fraflyttingsland: 'AGO',
					fraflyttingsstedIUtlandet: '',
					innflyttingsdato: '2004-10-06T00:00:00',
				},
			],
			utenlandskIdentifikasjonsnummer: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2023-01-09T10:24:01',
						gjeldende: null,
						gyldighetstidspunkt: '2023-01-09T10:24:01',
						opphoerstidspunkt: null,
					},
					identifikasjonsnummer: '12345',
					opphoert: false,
					utstederland: 'AND',
				},
			],
			falskIdentitet: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: null,
						gjeldende: null,
						gyldighetstidspunkt: null,
						opphoerstidspunkt: null,
					},
					erFalsk: true,
					gyldigFraOgMed: null,
					gyldigTilOgMed: null,
					nyFalskIdentitetPerson: null,
					rettIdentitetErUkjent: null,
					rettIdentitetVedIdentifikasjonsnummer: '23456789123',
					rettIdentitetVedOpplysninger: null,
				},
			],
			folkeregisterPersonstatus: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: null,
						gjeldende: null,
						gyldighetstidspunkt: null,
						opphoerstidspunkt: null,
					},
					status: 'MIDLERTIDIG',
					gyldigFraOgMed: null,
					gyldigTilOgMed: null,
				},
			],
			tilrettelagtKommunikasjon: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'PDL',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2023-01-09T10:24:01',
						gjeldende: null,
						gyldighetstidspunkt: '2023-01-09T10:24:01',
						opphoerstidspunkt: null,
					},
					spraakForTaletolk: 'AZ',
					spraakForTegnspraakTolk: 'ES',
				},
			],
			statsborgerskap: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '1992-06-04T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '1992-06-04T00:00:00',
						opphoerstidspunkt: null,
					},
					landkode: 'CZE',
					gyldigFraOgMed: '1992-06-04T00:00:00',
					gyldigTilOgMed: null,
					bekreftelsesdato: null,
				},
			],
			telefonnummer: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'PDL',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2023-01-09T10:24:01',
						gjeldende: null,
						gyldighetstidspunkt: '2023-01-09T10:24:01',
						opphoerstidspunkt: null,
					},
					landskode: '+376',
					nummer: '12345678',
					prioritet: 1,
				},
			],
			vergemaal: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'FREG',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2023-01-09T10:24:01',
						gjeldende: null,
						gyldighetstidspunkt: '2023-01-09T10:24:01',
						opphoerstidspunkt: null,
					},
					vergemaalEmbete: 'FMIN',
					sakType: 'ANN',
					gyldigFraOgMed: null,
					gyldigTilOgMed: null,
					nyVergeIdent: {
						identtype: null,
						kjoenn: null,
						foedtEtter: '1948-01-09T10:24:49',
						foedtFoer: '2005-01-09T10:24:49',
						alder: null,
						syntetisk: true,
						nyttNavn: {
							hasMellomnavn: false,
						},
						statsborgerskapLandkode: null,
						gradering: null,
						eksisterendeIdent: null,
					},
					vergeIdent: '23456789123',
					mandatType: 'FOR',
					eksisterendePerson: false,
				},
			],
			sikkerhetstiltak: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'PDL',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '2022-10-03T00:00:00',
						opphoerstidspunkt: '2022-11-16T00:00:00',
					},
					tiltakstype: 'TFUS',
					beskrivelse: 'Telefonisk utestengelse',
					kontaktperson: {
						personident: 'Z577742',
						enhet: '0211',
					},
					gyldigFraOgMed: '2022-10-03T00:00:00',
					gyldigTilOgMed: '2022-11-16T00:00:00',
				},
			],
		},
	},
]

export const pdlPersonEnkeltMock = {
	data: {
		hentIdenter: {
			identer: [
				{
					ident: '23456789123',
					historisk: true,
					gruppe: 'FOLKEREGISTERIDENT',
				},
				{
					ident: '12345678912',
					historisk: false,
					gruppe: 'FOLKEREGISTERIDENT',
				},
				{
					ident: '2994017826241',
					historisk: false,
					gruppe: 'AKTORID',
				},
			],
		},
		hentGeografiskTilknytning: {
			gtType: 'KOMMUNE',
			gtKommune: '3819',
			regel: '2',
		},
		hentPerson: {
			falskIdentitet: {
				rettIdentitetVedIdentifikasjonsnummer: '22456823657',
				metadata: {
					endringer: [
						{
							kilde: 'Dolly',
							registrert: '2022-10-03T11:57:43',
							registrertAv: 'Folkeregisteret',
							systemkilde: 'FREG',
							type: 'OPPRETT',
						},
					],
					historisk: false,
					master: 'FREG',
					opplysningsId: 'd200d3d5-5808-4f7b-9018-aa654358085d',
				},
			},
			bostedsadresse: [
				{
					angittFlyttedato: '1992-01-11',
					gyldigFraOgMed: '1992-01-11T00:00',
					vegadresse: {
						matrikkelId: 410298366,
						husnummer: '2077',
						adressenavn: 'Testeveien',
						postnummer: '3697',
						kommunenummer: '3819',
						koordinater: {
							x: 488662.6,
							y: 6623765.7,
						},
					},
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:41',
						gyldighetstidspunkt: '1992-01-11T00:00',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:41',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: 'a7e33427-476e-4e94-9a53-7e42172555d1',
					},
				},
			],
			oppholdsadresse: [],
			deltBosted: [],
			forelderBarnRelasjon: [
				{
					relatertPersonsIdent: '34567891234',
					relatertPersonsRolle: 'BARN',
					minRolleForPerson: 'FAR',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:42',
						gyldighetstidspunkt: '2022-10-03T11:57:42',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:42',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '1d26fcc4-6b20-4925-a03e-2dc74b7ebc02',
					},
				},
			],
			kontaktadresse: [],
			kontaktinformasjonForDoedsbo: [],
			utenlandskIdentifikasjonsnummer: [
				{
					identifikasjonsnummer: '12345',
					utstederland: 'AND',
					opphoert: false,
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:43',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '0752cda2-993e-4749-976a-f6b6fb898093',
					},
				},
			],
			adressebeskyttelse: [],
			foedsel: [
				{
					foedselsaar: 1992,
					foedselsdato: '1992-01-11',
					foedeland: 'NOR',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:40',
						gyldighetstidspunkt: '2022-10-03T11:57:40',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:40',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: 'c8eb5066-14ec-4bf7-acb5-365cae1deaf3',
					},
				},
			],
			doedfoedtBarn: [],
			doedsfall: [],
			kjoenn: [
				{
					kjoenn: 'MANN',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:40',
						gyldighetstidspunkt: '2022-10-03T11:57:40',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:40',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '017a6239-91c2-463b-b9d1-1e732c3db5e5',
					},
				},
			],
			navn: [
				{
					fornavn: 'Cafe',
					etternavn: 'Test',
					gyldigFraOgMed: '2022-10-03',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:40',
						gyldighetstidspunkt: '2022-10-03T11:57:40',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:40',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '764dc813-3c85-42c3-abb6-472f6f30d953',
					},
				},
			],
			folkeregisterpersonstatus: [
				{
					status: 'bosatt',
					forenkletStatus: 'bosattEtterFolkeregisterloven',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T00:00',
						gyldighetstidspunkt: '1992-01-11T00:00',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:40',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '6385742c-b8a6-401a-b7fa-fc8d6ab6a643',
					},
				},
			],
			identitetsgrunnlag: [],
			tilrettelagtKommunikasjon: [
				{
					talespraaktolk: {
						spraak: 'AZ',
					},
					tegnspraaktolk: {
						spraak: 'ES',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:47',
								registrertAv: 'dev-fss:dolly:testnav-pdl-proxy-trygdeetaten',
								systemkilde: 'dev-fss:dolly:testnav-pdl-proxy-trygdeetaten',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'PDL',
						opplysningsId: 'edf9337a-2606-4ce7-bc19-1a18fd103fe7',
					},
				},
			],
			fullmakt: [],
			folkeregisteridentifikator: [
				{
					identifikasjonsnummer: '12345678912',
					status: 'OPPHOERT',
					type: 'FNR',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-02T11:57:39',
						gyldighetstidspunkt: '2022-10-02T11:57:39',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:39',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: true,
						master: 'FREG',
						opplysningsId: '7c181bd3-6607-4cf4-90c1-d268d229ea2c',
					},
				},
				{
					identifikasjonsnummer: '12345678912',
					status: 'I_BRUK',
					type: 'FNR',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:39',
						gyldighetstidspunkt: '2022-10-03T11:57:39',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:39',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '3409010e-1679-4550-a5ca-30a88bff68c1',
					},
				},
			],
			statsborgerskap: [
				{
					land: 'NOR',
					gyldigFraOgMed: '1992-01-11',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:41',
						gyldighetstidspunkt: '1992-01-11T00:00',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:41',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: 'd4171e17-b1ab-4977-88e7-8892d62cbc88',
					},
				},
			],
			sikkerhetstiltak: [
				{
					tiltakstype: 'TFUS',
					beskrivelse: 'Telefonisk utestengelse',
					kontaktperson: {
						personident: 'Z577742',
						enhet: '0211',
					},
					gyldigFraOgMed: '2022-10-03',
					gyldigTilOgMed: '2022-11-16',
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:48',
								registrertAv: 'dev-fss:dolly:testnav-pdl-proxy-trygdeetaten',
								systemkilde: 'dev-fss:dolly:testnav-pdl-proxy-trygdeetaten',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'PDL',
						opplysningsId: '4ee9f772-7296-4439-85cf-5182689193dc',
					},
				},
			],
			opphold: [],
			sivilstand: [
				{
					type: 'GIFT',
					relatertVedSivilstand: '23456789123',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T11:57:42',
						gyldighetstidspunkt: '2022-10-03T11:57:42',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:42',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: 'a1646dc9-0b0f-4afd-ba39-21d6a395781a',
					},
				},
			],
			telefonnummer: [
				{
					landskode: '+376',
					nummer: '12345678',
					prioritet: 1,
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:43',
								registrertAv: 'dev-fss:dolly:testnav-pdl-proxy-trygdeetaten',
								systemkilde: 'dev-fss:dolly:testnav-pdl-proxy-trygdeetaten',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'PDL',
						opplysningsId: '0bdc4885-7436-407b-a3c8-0f5bc11533d3',
					},
				},
			],
			innflyttingTilNorge: [
				{
					fraflyttingsland: 'AGO',
					fraflyttingsstedIUtlandet: '',
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T00:00',
						gyldighetstidspunkt: '2004-10-06T00:00',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:41',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: '29a0db5d-c52f-426c-8fa1-06422a9b2d10',
					},
				},
			],
			utflyttingFraNorge: [],
			vergemaalEllerFremtidsfullmakt: [
				{
					type: 'forvaltningUtenforVergemaal',
					embete: 'Statsforvalteren i Innlandet',
					vergeEllerFullmektig: {
						navn: {
							fornavn: 'Testesen',
							etternavn: 'Cafe',
						},
						motpartsPersonident: '23456789123',
						omfangetErInnenPersonligOmraade: true,
					},
					folkeregistermetadata: {
						ajourholdstidspunkt: '2022-10-03T00:00',
						gyldighetstidspunkt: '2022-10-03T11:57:42',
						kilde: 'Dolly',
					},
					metadata: {
						endringer: [
							{
								kilde: 'Dolly',
								registrert: '2022-10-03T11:57:42',
								registrertAv: 'Folkeregisteret',
								systemkilde: 'FREG',
								type: 'OPPRETT',
							},
						],
						historisk: false,
						master: 'FREG',
						opplysningsId: 'f80d7429-dec4-49af-b933-88d07a143017',
					},
				},
			],
			foreldreansvar: [],
		},
	},
}
