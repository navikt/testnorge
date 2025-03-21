import { fetcher } from '@/api'
import * as _ from 'lodash-es'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { SortKodeverkArray } from '@/service/services/dolly/Utils'
import useSWRImmutable from 'swr/immutable'

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
		: `/testnav-kodeverk-service/api/v1/kodeverk/${kodeverkNavn}`

export const useKodeverk = (kodeverkNavn) => {
	const { data, isLoading, error, mutate } = useSWRImmutable<KodeverkListe, Error>(
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
		_.cloneDeep(data?.koder)

	const kodeverkSortert = SortKodeverkArray({ koder: koder, name: kodeverkNavn })

	return {
		kodeverk: SelectOptionsFormat.formatOptions(
			_.toLower(kodeverkNavn),
			kodeverkSortert,
			isLoading,
		),
		loading: isLoading,
		error: error,
		mutate,
	}
}
