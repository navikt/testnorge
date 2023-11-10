import useSWR from 'swr'
import { fetcher } from '@/api'
import * as _ from 'lodash'

type KodeverkListe = {
	koder: Array<KodeverkType>
	land?: string
	landkode?: string
	valuta?: string
	valutakode?: string
}

type KodeverkType = {
	label?: string
	value?: string
}

const getKodeverkUrl = (kodeverkNavn) =>
	kodeverkNavn.includes('Valuta')
		? `/testnav-kontoregister-person-proxy/api/system/v1/hent-valutakoder`
		: `/dolly-backend/api/v1/kodeverk/${kodeverkNavn}`

export const useKodeverk = (kodeverkNavn) => {
	const { data, isLoading, error } = useSWR<KodeverkListe, Error>(
		[
			getKodeverkUrl(kodeverkNavn),
			{ accept: 'application/json', 'Content-Type': 'application/json' },
		],
		([url, headers]) => fetcher(url, headers),
	)

	const koder =
		(_.isArray(data) &&
			data[0]?.valutakode &&
			data.map((kode) => ({
				label: kode.valuta + ` (${kode.valutakode})`,
				value: kode.valutakode,
			}))) ||
		data?.koder

	return {
		kodeverk: koder || [],
		loading: isLoading,
		error: error,
	}
}
