import { useAsync } from 'react-use'
import { BrregstubApi, DollyApi, KrrApi, PdlforvalterApi } from '@/service/Api'
import Api from '@/api'
import { Person, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { getAlder } from '@/ducks/fagsystem'

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
				console.log('id: ', id) //TODO - SLETT MEG
				const navn =
					id.person.navn?.length > 0
						? `- ${id.person.navn[0].fornavn} ${id.person.navn[0].etternavn}`
						: ''
				personListe.push({
					value: id.person.ident,
					label: `${id.person.ident} ${navn}`,
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

	hentKrrLeverandoerer: () => {
		return useAsync(async () => KrrApi.getSdpLeverandoerListe(), [KrrApi.getSdpLeverandoerListe])
	},

	hentInntektsmeldingOptions: (enumtype: string) =>
		Api.fetchJson(`${uri}/inntektsmelding/${enumtype}`, { method: 'GET' }),

	hentArbeidsforholdstyperInntektstub: () => {
		return useAsync(
			async () => DollyApi.getKodeverkByNavn('Arbeidsforholdstyper'),
			[DollyApi.getKodeverkByNavn],
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
}
