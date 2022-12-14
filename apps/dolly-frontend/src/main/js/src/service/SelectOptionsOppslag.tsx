import { useAsync } from 'react-use'
import { BrregstubApi, DollyApi, KrrApi, Norg2Api, PdlforvalterApi, TpsfApi } from '@/service/Api'
import Api from '@/api'
import * as _ from 'lodash-es'
import { Person, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { getAlder } from '@/ducks/fagsystem'
import { HentPerson } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

const uri = `/dolly-backend/api/v1`

export type Option = {
	value: any
	label: string
	tema?: string
	alder?: number
	sivilstand?: string
	vergemaal?: boolean
	doedsfall?: boolean
	foreldre?: Array<string>
	foreldreansvar?: Array<string>
	relasjoner?: Array<string>
}

type Data = {
	label: string
	id: string
	navn: string
	value: {
		data: any
	}
}

type PersonListe = {
	value: string
	label: string
	alder: number
	sivilstand: string
	vergemaal: boolean
	doedsfall: boolean
	foreldre: Array<string>
	foreldreansvar: Array<string>
}

export const SelectOptionsOppslag = {
	hentGruppeIdentOptions: async (gruppeId: string) => {
		const gruppe = await DollyApi.getGruppeById(gruppeId).then((response: any) => {
			return response.data?.identer?.map((person: PersonData) => {
				if (person.master === 'PDL' || person.master === 'PDLF') {
					return person.ident
				}
			})
		})
		if (gruppe?.length < 1) {
			return null
		}
		const options = await PdlforvalterApi.getPersoner(gruppe).then((response: any) => {
			if (gruppe?.length < 1) {
				return null
			}
			const personListe: Array<PersonListe> = []
			response?.data?.forEach((id: Person) => {
				personListe.push({
					value: id.person.ident,
					label: `${id.person.ident} - ${id.person.navn[0].fornavn} ${id.person.navn[0].etternavn}`,
					alder: getAlder(id.person.foedsel?.[0]?.foedselsdato),
					sivilstand: id.person.sivilstand?.[0]?.type,
					vergemaal: id.person.vergemaal?.length > 0,
					doedsfall: id.person.doedsfall?.length > 0,
					foreldre: id.relasjoner
						?.filter((relasjon) => relasjon.relasjonType === 'FAMILIERELASJON_FORELDER')
						?.map((relasjon) => relasjon.relatertPerson?.ident),
					foreldreansvar: id.relasjoner
						?.filter((relasjon) => relasjon.relasjonType === 'FORELDREANSVAR_BARN')
						?.map((relasjon) => relasjon.relatertPerson?.ident),
				})
			})
			return personListe
		})
		return options || Promise.resolve()
	},

	hentHelsepersonell: () => Api.fetchJson(`${uri}/helsepersonell`, { method: 'GET' }),

	hentKrrLeverandoerer: () => {
		return useAsync(async () => KrrApi.getSdpLeverandoerListe(), [KrrApi.getSdpLeverandoerListe])
	},

	hentNavEnheter: () => {
		return useAsync(async () => Norg2Api.getNavEnheter(), [Norg2Api.getNavEnheter])
	},

	hentPersonnavn: () => {
		return useAsync(async () => DollyApi.getPersonnavn(), [DollyApi.getPersonnavn])
	},

	hentInntektsmeldingOptions: (enumtype: string) =>
		Api.fetchJson(`${uri}/inntektsmelding/${enumtype}`, { method: 'GET' }),

	hentArbeidsforholdstyperInntektstub: () => {
		return useAsync(
			async () => DollyApi.getKodeverkByNavn('Arbeidsforholdstyper'),
			[DollyApi.getKodeverkByNavn]
		)
	},

	hentFullmaktOmraader: () => {
		return useAsync(async () => DollyApi.getKodeverkByNavn('Tema'), [DollyApi.getKodeverkByNavn])
	},

	hentRollerFraBrregstub: () => {
		return useAsync(async () => BrregstubApi.getRoller(), [BrregstubApi.getRoller])
	},

	hentUnderstatusFraBrregstub: () => {
		return useAsync(async () => BrregstubApi.getUnderstatus(), [BrregstubApi.getUnderstatus])
	},

	hentTagsFraDolly: () => {
		return useAsync(async () => DollyApi.getTags(), [DollyApi.getTags])
	},

	formatOptions: (type: string, data: any) => {
		if (!data?.value) {
			if (!data?.loading) {
				console.error('Fant ingen kodeverk for type: ' + type)
			}
			return []
		}
		if (type === 'personnavn') {
			const persondata: any[] = data?.value?.data || []
			const options: Option[] = []
			persondata?.length > 0 &&
				persondata.forEach((personInfo) => {
					if (!_.isNil(personInfo.fornavn)) {
						const mellomnavn = !_.isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
						const navn = personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn
						options.push({ value: personInfo.fornavn.toUpperCase(), label: navn.toUpperCase() })
					}
				})
			return options
		} else if (type === 'fornavn' || type === 'mellomnavn' || type === 'etternavn') {
			const navnData = data?.value?.data || []
			const options: { value: string; label: string }[] = []
			navnData?.length > 0 &&
				navnData.forEach((navn: { [x: string]: any }) => {
					options.push({ value: navn[type], label: navn[type] })
				})
			return options
		} else if (type === 'navnOgFnr') {
			const persondata = data.value && data.value.data ? data.value.data.liste : []
			const options: Option[] = []
			persondata?.length > 0 &&
				persondata.forEach(
					(personInfo: { fornavn: string; mellomnavn: string; etternavn: string; fnr: string }) => {
						if (!_.isNil(personInfo.fornavn)) {
							const mellomnavn = !_.isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
							const navnOgFnr =
								(personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn).toUpperCase() +
								': ' +
								personInfo.fnr
							options.push({ value: personInfo.fnr, label: navnOgFnr })
						}
					}
				)
			return options
		} else if (type === 'arbeidsforholdstyper') {
			const options = data.value ? data.value.data.koder : []
			options?.length > 0 &&
				options.forEach((option: Option) => {
					if (option.value === 'frilanserOppdragstakerHonorarPersonerMm') {
						option.label = 'Frilansere/oppdragstakere, honorar, m.m.'
					}
					if (option.value === 'pensjonOgAndreTyperYtelserUtenAnsettelsesforhold') {
						option.label = 'Pensjoner og andre typer ytelser uten ansettelsesforhold'
					}
				})
			return options
		} else if (type === 'understatuser') {
			const statuser = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			statuser.forEach((status) => {
				options.push({ value: parseInt(status[0]), label: `${status[0]}: ${status[1]}` })
			})
			return options
		} else if (type === 'roller') {
			const roller = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			roller.forEach((rolle: [string, string]) => {
				options.push({ value: rolle[0], label: rolle[1] })
			})
			return options
		} else if (type === 'navEnheter') {
			const enheter = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			enheter?.forEach((enhet: [string, any]) => {
				options.push({
					value: enhet?.[1]?.enhetNr,
					label: `${enhet?.[1]?.navn} (${enhet?.[1]?.enhetNr})`,
				})
			})
			return options
		} else if (type === 'sdpLeverandoer') {
			const leverandoerer = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			leverandoerer.forEach((leverandoer: [string, any]) => {
				data = leverandoer[1]
				options.push({ value: parseInt(data.id), label: data.navn })
			})
			return options
		} else if (type === 'tags') {
			const tags = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			tags.forEach((leverandoer) => {
				data = leverandoer[1]
				options.push({ value: data.tag, label: data.beskrivelse })
			})
			return options
		} else if (type === 'fullmaktOmraader') {
			const omraader = data.value ? Object.entries(data.value.data.koder) : []
			const ugyldigeKoder = ['BII', 'KLA', 'KNA', 'KOM', 'LGA', 'MOT', 'OVR']
			const options: Option[] = []
			options.push({ value: '*', label: '* (Alle)' })
			omraader
				.filter((omr: [string, Option]) => {
					return !ugyldigeKoder.includes(omr[1].value)
				})
				.forEach((omraade: [string, Data]) => {
					data = omraade[1]
					options.push({ value: data.value, label: data.label })
				})
			return options
		}
	},
}
