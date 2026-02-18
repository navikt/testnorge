import useSWR from 'swr'
import axios from 'axios'

const skattekortBaseUrl = '/testnav-dolly-proxy/skattekort/api/v1/person'

interface SkattekortResponse {
	utstedtDato?: string
	inntektsaar: number
	resultatForSkattekort: string
	forskuddstrekkList: ForskuddstrekkDTO[]
	tilleggsopplysningList?: string[]
}

interface ForskuddstrekkDTO {
	trekkode?: string
	frikortBeloep?: number
	tabell?: string
	prosentSats?: number
	antallMndForTrekk?: number
}

const SKATTEKORT_KODEVERK: Record<string, Record<string, string>> = {
	RESULTATSTATUS: {
		'Skattekortopplysninger OK': 'SKATTEKORTOPPLYSNINGER_OK',
		'Ikke trekkplikt': 'IKKE_TREKKPLIKT',
		'Ikke skattekort': 'IKKE_SKATTEKORT',
	},
	TILLEGGSOPPLYSNING: {
		'Opphold på Svalbard': 'OPPHOLD_PAA_SVALBARD',
		'Kildeskatt på pensjon': 'KILDESKATT_PAA_PENSJON',
		'Opphold i tiltakssone': 'OPPHOLD_I_TILTAKSSONE',
	},
	TREKKODE: {
		'Lønn fra NAV': 'LOENN_FRA_NAV',
		'Pensjon fra NAV': 'PENSJON_FRA_NAV',
		'Uføretrygd fra NAV': 'UFOERETRYGD_FRA_NAV',
	},
}

export const useSkattekort = (ident: string, harSkattekortBestilling: boolean) => {
	const shouldFetch = Boolean(ident && harSkattekortBestilling)

	const { data, isLoading, error } = useSWR<SkattekortResponse[], Error>(
		shouldFetch ? [`${skattekortBaseUrl}/hent-skattekort`, ident] : null,
		async ([url, fnr]: [string, string]) => {
			try {
				const res = await axios.post(
					url,
					{ fnr },
					{
						headers: {
							'Content-Type': 'application/json',
						},
					},
				)
				return res.data
			} catch (e: unknown) {
				if (axios.isAxiosError(e) && e.response?.status === 404) {
					return null
				}
				throw e
			}
		},
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		skattekortData: data,
		loading: isLoading,
		error: error,
	}
}

export const useSkattekortKodeverk = (kodeverkstype: string) => {
	const kodeverkData = SKATTEKORT_KODEVERK[kodeverkstype]

	const koder =
		kodeverkData &&
		Object.keys(kodeverkData)?.map((key) => ({
			label: key,
			value: kodeverkData[key],
		}))

	return {
		kodeverk: koder,
		loading: false,
		error: null,
	}
}
