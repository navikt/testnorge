import useSWR from 'swr'
import Request from '@/service/services/Request'

const dollySearchUrl = '/testnav-dolly-search-service/api/v1'

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
