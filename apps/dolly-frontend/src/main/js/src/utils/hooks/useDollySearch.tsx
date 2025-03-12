import { ResponseIdenter, SoekRequest } from '@/pages/dollySoek/DollySoekTypes'
import useSWR from 'swr'
import Request from '@/service/services/Request'

const dollySearchUrl = '/testnav-dolly-search-service/api/v1'

export const usePersonerSearch = (request: SoekRequest) => {
	console.log('request: ', request) //TODO - SLETT MEG

	const getRegistreRequest = () => {
		if (!request?.registreRequest || request?.registreRequest.length === 0) {
			return null
		}
		let url = ''
		request?.registreRequest?.forEach((type, idx) =>
			idx === 0 ? (url += `?registreRequest=${type}`) : (url += `&registreRequest=${type}`),
		)
		return url
	}

	const registre = getRegistreRequest()

	const { data, isLoading, error, mutate } = useSWR<ResponseIdenter, Error>(
		request ? [`${dollySearchUrl}/personer${registre ? registre : ''}`, request] : null,
		([url, headers]) => Request.post(url, headers),
	)

	return {
		result: data?.data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const usePersonerTyper = () => {
	const { data, isLoading, error } = useSWR(`${dollySearchUrl}/personer/typer`, (url) =>
		Request.get(url),
	)

	const selectOptions = data?.data?.map((option) => {
		return {
			value: option?.type,
			label: option?.beskrivelse,
		}
	})

	return {
		typer: selectOptions,
		loading: isLoading,
		error: error,
	}
}

//TODO: identer-search-controller
