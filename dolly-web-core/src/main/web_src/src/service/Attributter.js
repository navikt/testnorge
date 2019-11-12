import { ArenaApi, InstApi } from '~/service/Api'
import _memoize from 'lodash/memoize'
import _mapValues from 'lodash/mapValues'

export const getAttributter = () => attributter

// InitialValues er alle deselected
export const getInitialValues = _memoize(() => _mapValues(groupByValue(), v => false))

export const groupByValue = _memoize(() => {
	return getAttributter().reduce((acc, curr) => {
		curr.kategori.forEach(kategori => {
			kategori.sub.forEach(sub => {
				acc[sub.name] = {
					panel: curr.panel,
					kategori: kategori.navn,
					label: sub.label,
					name: sub.name
				}
			})
		})
		return acc
	}, {})
})

export const getAttributterForUtvalgListe = (formikValues = {}) => {
	return getAttributter()
		.map(o => ({
			panel: o.panel,
			values: o.kategori.reduce((acc, curr) => {
				// Filter selected values
				const selected = curr.sub.filter(f => formikValues[f.name])
				return acc.concat(selected)
			}, [])
		}))
		.filter(kat => kat.values.length)
}

const attributter = [
	{
		panel: 'Personinformasjon',
		infotekst:
			'Data om institusjonsopphold blir ikke distribuert til alle miljøer, og et eller flere av miljøene under må derfor velges i siste steg.',
		tilgjengeligeMiljoeEndepunkt: InstApi.getTilgjengeligeMiljoer,
		kategori: [
			{
				navn: 'Alder',
				sub: [
					{
						label: 'Født før',
						name: 'foedtFor'
					},
					{
						label: 'Født etter',
						name: 'foedtEtter'
					},
					{
						label: 'Dødsdato',
						name: 'doedsdato'
					}
				]
			},
			{
				navn: 'Nasjonalitet',
				sub: [
					{
						label: 'Statsborgerskap',
						name: 'statsborgerskap'
					},
					{
						label: 'Innvandret fra',
						name: 'innvandret'
					},
					{
						label: 'Utvandret til',
						name: 'utvandret'
					}
				]
			},
			{
				navn: 'Instutisjonsopphold',
				sub: [
					{
						label: 'Har instutisjonsopphold',
						name: 'instutisjonsopphold'
					}
				]
			},
			{
				navn: 'Identifikasjon',
				sub: [
					{
						label: 'Har identhistorikk',
						name: 'identhistorikk'
					},
					{
						label: 'Har utenlands-id',
						name: 'utenlandsid'
					},
					{
						label: 'Har falsk identitet',
						name: 'falskidentitet'
					}
				]
			},
			{
				navn: 'Diverse',
				sub: [
					{
						label: 'Kjønn',
						name: 'kjonn'
					},
					{
						label: 'Mellomnavn',
						name: 'mellomnavn'
					},
					{
						label: 'Sivilstand',
						name: 'sivilstand'
					},
					{
						label: 'Språk',
						name: 'sprak'
					},
					{
						label: 'Egenansatt',
						name: 'egenansatt'
					},
					{
						label: 'Diskresjonskode',
						name: 'diskresjonskode'
					},
					{
						label: 'Forsvunnet',
						name: 'forsvunnet'
					}
				]
			}
		]
	},
	{
		panel: 'Adresser',
		kategori: [
			{
				navn: 'Boadresse',
				sub: [
					{
						label: 'Har boadresse',
						name: 'boadresse'
					},
					{
						label: 'Flyttedato',
						name: 'flyttedato'
					}
				]
			},
			{
				navn: 'Postadresse',
				sub: [
					{
						label: 'Har postadresse',
						name: 'postadresse'
					}
				]
			}
		]
	},
	{
		panel: 'Familierelasjoner',
		kategori: [
			{
				navn: 'Partner',
				sub: [
					{
						label: 'Har partner',
						name: 'partner'
					}
				]
			},
			{
				navn: 'Barn',
				sub: [
					{
						label: 'Har barn',
						name: 'barn'
					}
				]
			}
		]
	},
	{
		panel: 'Arbeid og inntekt',
		infotekst:
			'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.',
		kategori: [
			{
				navn: 'Arbeidsforhold',
				sub: [
					{
						label: 'Har arbeidsforhold',
						name: 'arbeidsforhold'
					}
				]
			},
			{
				navn: 'Inntekt',
				sub: [
					{
						label: 'Har inntekt',
						name: 'inntekt'
					}
				]
			}
		]
	},
	{
		panel: 'Kontakt- og reservasjonsregisteret',
		infotekst:
			'KRR - benyttes for offentlige virksomheter for å avklare om den enkelte bruker har reservert seg mot digital kommunikasjon eller ikke. I tillegg skal varslene som sendes til bruker benytte den kontaktinformasjonen som ligger i registeret. Dette kan enten være mobiltelefonnummer for utsendelse av sms, eller epostadresse for utsendelse av epost.',
		kategori: [
			{
				navn: 'Kontakt- og reservasjonsregisteret',
				sub: [
					{
						label: 'Har kontaktinformasjon',
						name: 'kontaktinformasjon'
					}
				]
			}
		]
	},
	{
		panel: 'Kontaktinformasjon for dødsbo',
		infotekst:
			'Kontaktinformasjon for dødsbo blir kun distribuert til Q2, og dette miljøet må derfor velges i siste steg.',
		kategori: [
			{
				navn: 'Kontaktinformasjon for dødsbo',
				sub: [
					{
						label: 'Har kontaktinformasjon for dødsbo',
						name: 'kontaktinformasjonDoedsbo'
					}
				]
			}
		]
	},
	{
		panel: 'Arena',
		infotekst:
			'Arena-data blir ikke distribuert til alle miljøer, og et eller flere av miljøene under må derfor velges i siste steg.',
		tilgjengeligeMiljoeEndepunkt: ArenaApi.getTilgjengeligeMiljoe,
		kategori: [
			{
				navn: 'Arena',
				sub: [
					{
						label: 'Aktiver/inaktiver bruker',
						name: 'arenaAktiverBruker'
					}
				]
			}
		]
	},
	{
		panel: 'UDI',
		kategori: [
			{
				navn: 'Gjeldende oppholdsstatus',
				sub: [
					{
						label: 'Har oppholdsstatus',
						name: 'oppholdsstatus'
					}
				]
			},
			{
				navn: 'Arbeidsadgang',
				sub: [
					{
						label: 'Har arbeidsadgang',
						name: 'arbeidsadgang'
					}
				]
			},
			{
				navn: 'Alias',
				sub: [
					{
						label: 'Har aliaser',
						name: 'aliaser'
					}
				]
			},
			{
				navn: 'Diverse',
				sub: [
					{
						label: 'Flyktningstatus',
						name: 'flyktningstatus'
					},
					{
						label: 'Asylsøker',
						name: 'asylsoker'
					}
				]
			}
		]
	}
]
