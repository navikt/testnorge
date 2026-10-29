import useSWRImmutable from 'swr/immutable'
import { fetcher } from '@/api'
import {
	KontoregisterLandkode,
	landkoder,
	settKontoregisterLandkoder,
} from '@/service/services/kontoregister/landkoder'

const kontoregisterLandkoderUrl = '/testnav-dolly-proxy/kontoregister/api/system/v1/hent-landkoder'

type LandkodeInfo = {
	landkode: string
	alternativLandkode?: string | null
	land: string
	kreverIban: boolean
	ibanLengde?: number | null
	kreverBankkode: boolean
	bankkodeLengde?: number | null
}

const tilKontoregisterLandkoder = (data?: LandkodeInfo[]): KontoregisterLandkode[] | null => {
	if (!Array.isArray(data) || data.length === 0) {
		return null
	}
	return data.map((landkodeInfo) => ({
		landkode: landkodeInfo.landkode,
		land: landkodeInfo.land,
		kreverIban: landkodeInfo.kreverIban,
		ibanLengde: landkodeInfo.ibanLengde,
		kreverBankkode: landkodeInfo.kreverBankkode,
		bankkodeLengde: landkodeInfo.bankkodeLengde,
		kreverBicOgBankkode: landkodeInfo.kreverBankkode,
	}))
}

export const useKontoregisterLandkoder = () => {
	const { data, isLoading, error } = useSWRImmutable<LandkodeInfo[], Error>(
		kontoregisterLandkoderUrl,
		fetcher,
		{
			onSuccess: (response) => {
				const hentedeLandkoder = tilKontoregisterLandkoder(response)
				if (hentedeLandkoder) {
					settKontoregisterLandkoder(hentedeLandkoder)
				}
			},
		},
	)

	return {
		kontoregisterLandkoder: tilKontoregisterLandkoder(data) ?? landkoder,
		loading: isLoading,
		error: error,
	}
}
