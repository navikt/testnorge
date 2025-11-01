import useSWR from 'swr'
import { fetcher } from '@/api'
import { Option } from '@/service/SelectOptionsOppslag'
import useSWRImmutable from 'swr/immutable'

type NodeType = {
	kode: string
	hjelpetekst?: { nb: string } | null
	termer: any
	undernoder?: Record<string, NodeType> | null
}

type FullmaktKodeverkType = {
	noder: Record<string, NodeType>
}

export const useFullmektig = (ident: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[
			ident && '/testnav-dolly-proxy/fullmakt/api/fullmaktsgiver',
			{ accept: 'application/json', 'Content-Type': 'application/json', fnr: ident },
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
	const { data, isLoading, error } = useSWRImmutable<FullmaktKodeverkType, Error>(
		[
			'/testnav-dolly-proxy/fullmakt/api/omraade',
			{ accept: 'application/json', 'Content-Type': 'application/json', fnr: '12808012345' },
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
					label: `${node.termer.nb} - ${node.kode}`,
				})
			}
		}
	}

	traverseNodes(omraadeKodeverk)
	return options
}
