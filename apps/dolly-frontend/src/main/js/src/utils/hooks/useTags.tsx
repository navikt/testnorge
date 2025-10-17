import useSWR from 'swr'
import { fetcher } from '@/api'
import { Option } from '@/service/SelectOptionsOppslag'

type TagResponse = {
	tag: string
	beskrivelse: string
}

export const useTags = () => {
	const { data, error } = useSWR(`/dolly-backend/api/v1/tags`, fetcher)

	const tags: Option[] = Array.isArray(data)
		? data.map((response: TagResponse) => ({ value: response.tag, label: response.beskrivelse }))
		: []

	return {
		tagOptions: tags,
		loading: !data && !error,
		error,
	}
}
