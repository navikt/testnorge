import useSWR from 'swr'
import { fetcher } from '@/api'
import { Option } from '@/service/SelectOptionsOppslag'

type NodeType = {
	kode: string
	hjelpetekst?: { nb: string } | null
	termer: any
	undernoder?: Record<string, NodeType> | null
}

type HierarkiType = {
	hierarkinivaaer: string[]
	noder: Record<string, NodeType>
}

export const useFullmektig = () => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[
			'/testnav-fullmakt-proxy/api/fullmektig',
			{ accept: 'application/json', 'Content-Type': 'application/json' },
		],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		fullmektig: data,
		loading: isLoading,
		error: error,
	}
}

export const useFullmaktOmraader = () => {
	const { data, isLoading, error } = useSWR<HierarkiType, Error>(
		[
			'/testnav-fullmakt-proxy/api/omraade',
			{ accept: 'application/json', 'Content-Type': 'application/json' },
		],
		([url, headers]) => fetcher(url, headers),
	)

	const omraadeKodeverk = data?.noder

	return {
		omraadeKodeverk: mapOmraadeKodeverkToOptions(omraadeKodeverk),
		loading: isLoading,
		error: error,
	}
}
const mapOmraadeKodeverkToOptions = (
	omraadeKodeverk: Record<string, NodeType> | undefined,
): Option[] => {
	if (!omraadeKodeverk) {
		return []
	}
	const options: Option[] = []

	const traverseNodes = (nodes: Record<string, NodeType>) => {
		for (const key in nodes) {
			const node = nodes[key]
			if (node.undernoder) {
				traverseNodes(node.undernoder)
			} else {
				options.push({
					value: node.kode,
					label: node.termer.nb,
				})
			}
		}
	}

	traverseNodes(omraadeKodeverk)
	return options
}
