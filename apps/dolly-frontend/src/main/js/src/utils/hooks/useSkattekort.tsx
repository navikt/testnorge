import useSWR from 'swr'
import axios from 'axios'

const skattekortUrl = (miljoe: string) => `/testnav-dolly-proxy/skattekort/${miljoe}/api/v1/person`

const SKATTEKORT_MILJOER = ['q1', 'q2']

interface SkattekortResponse {
	utstedtDato?: string
	inntektsaar: number
	resultatForSkattekort: string
	forskuddstrekkList: ForskuddstrekkDTO[]
	tilleggsopplysningList?: string[]
}

interface ForskuddstrekkDTO {
	trekkode?: string
	frikort?: FrikortDTO
	prosentkort?: ProsentkortDTO
	trekktabell?: TabellkortDTO
}

interface FrikortDTO {
	frikortBeloep: number
}

interface ProsentkortDTO {
	prosentSats: number
	antallMndForTrekk?: number
}

interface TabellkortDTO {
	tabell: string
	prosentSats: number
	antallMndForTrekk: number
}

const SKATTEKORT_KODEVERK: Record<string, Record<string, string>> = {
	RESULTATSTATUS: {
		'Skattekortopplysninger OK': 'SKATTEKORTOPPLYSNINGER_OK',
		'Ikke trekkplikt': 'IKKE_TREKKPLIKT',
		'Ikke skattekort': 'IKKE_SKATTEKORT',
		'Utgått d-nummer': 'UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT',
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
	RESULTATSTATUS_FRA_SOKOS: {
		'Skattekortopplysninger OK': 'skattekortopplysningerOK',
		'Ikke trekkplikt': 'ikkeTrekkplikt',
		'Ikke skattekort': 'ikkeSkattekort',
		'Utgått d-nummer': 'utgaattDnummerSkattekortForFoedselsnummerErLevert',
	},
	TILLEGGSOPPLYSNING_FRA_SOKOS: {
		'Opphold på Svalbard': 'oppholdPaaSvalbard',
		'Kildeskatt på pensjon': 'kildeskattPaaPensjon',
		'Opphold i tiltakssone': 'oppholdITiltakssone',
	},
	TREKKODE_FRA_SOKOS: {
		'Lønn fra NAV': 'loennFraNAV',
		'Pensjon fra NAV': 'pensjonFraNAV',
		'Uføretrygd fra NAV': 'ufoeretrygdFraNAV',
	},
}

const fetchSkattekortFromMiljoe = async (miljoe: string, fnr: string) => {
	try {
		const res = await axios.post(
			`${skattekortUrl(miljoe)}/hent-skattekort`,
			{ fnr },
			{
				headers: {
					'Content-Type': 'application/json',
				},
			},
		)
		return res.data || []
	} catch (e: unknown) {
		if (axios.isAxiosError(e) && e.response?.status === 404) {
			return []
		}
		throw e
	}
}

interface MiljoSkattekortData {
	miljo: string
	data: SkattekortResponse[]
}

export const useSkattekort = (ident: string, harSkattekortBestilling: boolean) => {
	const shouldFetch = Boolean(ident && harSkattekortBestilling)

	const { data, isLoading, error } = useSWR<MiljoSkattekortData[], Error>(
		shouldFetch ? ['skattekort-alle-miljoer', ident] : null,
		async ([, fnr]: [string, string]) => {
			const results = await Promise.allSettled(
				SKATTEKORT_MILJOER.map(async (miljoe) => {
					const miljoData = await fetchSkattekortFromMiljoe(miljoe, fnr)
					return { miljo: miljoe, data: miljoData } as MiljoSkattekortData
				}),
			)
			const fulfilled = results
				.filter((r): r is PromiseFulfilledResult<MiljoSkattekortData> => r.status === 'fulfilled')
				.map((r) => r.value)
			const rejected = results.filter((r) => r.status === 'rejected')

			if (fulfilled.length === 0 && rejected.length > 0) {
				throw (rejected[0] as PromiseRejectedResult).reason
			}
			return fulfilled
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
