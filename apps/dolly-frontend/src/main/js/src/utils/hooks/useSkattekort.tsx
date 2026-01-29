import useSWR from 'swr';
import axios from 'axios';

const skattekortBaseUrl = '/testnav-dolly-proxy/skattekort/api/v1'

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
		'Skattekortopplysninger OK': 'skattekortopplysningerOK',
		'Ikke skattekort': 'ikkeSkattekort',
		'Ikke trekkplikt': 'ikkeTrekkplikt',
	},
	TILLEGGSOPPLYSNING: {
		'Opphold på Svalbard': 'oppholdPaaSvalbard',
		Kildeskattpensjonist: 'kildeskattpensjonist',
		'Opphold i tiltakssone': 'oppholdITiltakssone',
		'Kildeskatt på lønn': 'kildeskattPaaLoenn',
	},
	TREKKODE: {
		'Lønn fra NAV': 'loennFraNAV',
		'Pensjon fra NAV': 'pensjonFraNAV',
		'Uføretrygd fra NAV': 'ufoeretrygdFraNAV',
	},
	TABELLTYPE: {
		Trekktabell: 'trekktabell',
		Standardtabell: 'standardtabell',
		Pensjonisttabell: 'pensjonisttabell',
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
